package com.shoppingcart.admin.product;

import com.shoppingcart.admin.FileUpLoadUtil;
import com.shoppingcart.admin.brand.BrandService;
import com.shoppingcart.admin.category.CategoryService;
import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
import com.shoppingcart.admin.entity.Products;
import com.shoppingcart.admin.product.export.ProductCsvExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {

	@Autowired
	ProductService service;

	@Autowired
	private CategoryService CategoryService;

	@Autowired
	BrandService BrandService;

	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc";

	@GetMapping("/products")
	public String listFirstPage(Model model) {
//		return listByPage(1, model, "name", "asc", null);
		return defaultRedirectURL;
	}

	@GetMapping("products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, @Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {//@Param: sau dau "?" o tren duong link, http://localhost:8082/ShoppingCartAdmin/users/page/1?sortField=firstName&sortDir=asc
		System.out.println("Sort Field: " + sortField);
		System.out.println("Sort Order: " + sortDir);

		Page<Products> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Products> listProducts = page.getContent();//lay ra cac user vao page 1, page 2

		List<Category> listCategory = CategoryService.listCategoriesUsedInForm();

		long startCount = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listCategory", listCategory);
		model.addAttribute("listProducts", listProducts); //model, đưa key listUsers vào html, <tr th:each="user : ${listUsers}">
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "products/products"; //view
	}

	@GetMapping("/products/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		List<Products> listProducts = service.listAll();
		ProductCsvExporter exporter = new ProductCsvExporter();
		exporter.export(listProducts, response);
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			service.deleteProduct(id);

			String productDir = "main-image/" + id;
			FileUpLoadUtil.removeDirectory((productDir));

			redirectAttributes.addFlashAttribute("message", "The product ID: " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = BrandService.listAll();

		Products product = new Products();
		product.setEnabled(true);
		product.setInStock(true);


		List<Category> listCategory = CategoryService.listCategoriesUsedInForm();


		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("listCategory", listCategory);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);

		return "products/product_form";
	}

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Products product = service.editProduct(id);

			List<Category> listCategories = CategoryService.listCategoriesUsedInForm();
//			List<Brand> listBrands = BrandService.listBrandsUsedInForm();
			List<Brand> listBrands = BrandService.listAll();

			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");

			return ("products/product_form");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

	@PostMapping("/products/save")
	public String savedProduct(Products product, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setAlias(filename);
			product.setCreateTime(new Date());
			Products saveProducts = service.save(product);

			String uploadDir = "product-images/" + saveProducts.getId();


			FileUpLoadUtil.cleanDir(uploadDir);
			FileUpLoadUtil.saveFile(uploadDir, filename, multipartFile);
		} else {
			if (product.getAlias().isEmpty()) product.setAlias(null);
			service.save(product);
		}

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");

		return getRedirectURLtoAffectedCategory(product);
//		return "redirect:/categories";
	}

	private String getRedirectURLtoAffectedCategory(Products product) {
		String firstPartOfName = product.getName().split("@")[0];
		return "redirect:/products/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfName;
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String enableProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, @PathVariable(name = "status") Boolean enabled) {
		service.updateProduct(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The product ID: " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return defaultRedirectURL;
	}

	@GetMapping("/products/{id}/stock/{status}")
	public String enableStock(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, @PathVariable(name = "status") Boolean enabled) {
		service.updateStock(id, enabled);
		String status = enabled ? "in stock" : "out of stock";
		String message = "The product ID: " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return defaultRedirectURL;
	}
}
