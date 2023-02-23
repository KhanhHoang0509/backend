package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.FileUpLoadUtil;
import com.shoppingcart.admin.brand.export.BrandCsvExporter;
import com.shoppingcart.admin.brand.export.BrandExcelExporter;
import com.shoppingcart.admin.brand.export.BrandPdfExporter;
import com.shoppingcart.admin.category.CategoryService;
import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
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
import java.util.List;

@Controller
public class BrandController {
	@Autowired
	private BrandService service;

	@Autowired
	private CategoryService CategoryService;

	private String defaultRedirectURL = "redirect:/brands/page/1?sortField=name&sortDir=asc";

	@GetMapping("/brands")
	public String listFirstPage(Model model) {
//		return listByPage(1, model, "name", "asc", null);
		return defaultRedirectURL;
	}

	@GetMapping("brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, @Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {//@Param: sau dau "?" o tren duong link, http://localhost:8082/ShoppingCartAdmin/users/page/1?sortField=firstName&sortDir=asc
		System.out.println("Sort Field: " + sortField);
		System.out.println("Sort Order: " + sortDir);

		Page<Brand> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Brand> listBrands = page.getContent();//lay ra cac user vao page 1, page 2

		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listBrands", listBrands); //model, đưa key listUsers vào html, <tr th:each="user : ${listUsers}">
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "brands/brands"; //view
	}

	@GetMapping("/brands/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		List<Brand> listBrands = service.listAll();
		BrandCsvExporter exporter = new BrandCsvExporter();
		exporter.export(listBrands, response);
	}

	@GetMapping("/brands/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<Brand> listBrands = service.listAll();
		BrandExcelExporter exporter = new BrandExcelExporter();
		exporter.export(listBrands, response);
	}

	@GetMapping("/brands/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<Brand> listBrands = service.listAll();
		BrandPdfExporter exporter = new BrandPdfExporter();
		exporter.export(listBrands, response);
	}

	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			service.deleteBrand(id);

			String brandDir = "brand-logos/" + id;
			FileUpLoadUtil.removeDirectory((brandDir));

			redirectAttributes.addFlashAttribute("message", "The brand ID: " + id + " has been deleted successfully");
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> listCategories = CategoryService.listCategoriesUsedInForm();

		model.addAttribute("brands", new Brand());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Brand");

		return "brands/brand_form";
	}

	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Brand brands = service.editBrand(id);
			List<Category> listCategories = CategoryService.listCategoriesUsedInForm();

			model.addAttribute("brands", brands);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");

			return ("brands/brand_form");
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}

	@PostMapping("/brands/save")
	public String savedBrand(Brand brand, RedirectAttributes redirectAttributes, @RequestParam("logo_image") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogos(filename);
			Brand saveBrand= service.save(brand);

			String uploadDir = "brand-logos/" + saveBrand.getId();

			FileUpLoadUtil.cleanDir(uploadDir);
			FileUpLoadUtil.saveFile(uploadDir, filename, multipartFile);
		} else {
			if (brand.getLogos().isEmpty()) brand.setLogos(null);
			service.save(brand);
		}

		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");

		return getRedirectURLtoAffectedCategory(brand);
//		return "redirect:/categories";
	}

	private String getRedirectURLtoAffectedCategory(Brand brand) {
		String firstPartOfName = brand.getName().split("@")[0];
		return "redirect:/brands/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfName;
	}
}
