package com.shoppingcart.admin.category;

import com.shoppingcart.admin.FileUpLoadUtil;
import com.shoppingcart.admin.category.export.CategoryCsvExporter;
import com.shoppingcart.admin.category.export.CategoryExcelExporter;
import com.shoppingcart.admin.category.export.CategoryPdfExporter;
import com.shoppingcart.admin.entity.Category;
import com.shoppingcart.admin.entity.User;
import com.shoppingcart.admin.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class CategoryController {//Controller return view

	@Autowired
	private CategoryService service;

	@Autowired
	private CategoryRepository repo;



	private String defaultRedirectURL = "redirect:/categories/page/1?sortField=name&sortDir=asc";


	@GetMapping("/categories")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null);
	}

//	@GetMapping("/categories")
//	public String listAllCategories(Model model) {
////		List<Category> listCategories = service.listCategories();
////
////		model.addAttribute("listCategories", listCategories);
//
//		return "redirect:/categories/page/1";
//	}

	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			service.deleteCategory(id);

			String categoryPhotoDir = "category-photos/" + id;
			FileUpLoadUtil.removeDirectory((categoryPhotoDir));

			redirectAttributes.addFlashAttribute("message", "The category ID: " + id + " has been deleted successfully");
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}

	@GetMapping("/categories/{id}/enabled/{status}")
	public String enableCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, @PathVariable(name = "status") Boolean enabled) {
		service.updateCategory(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category ID: " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return defaultRedirectURL;
	}

	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategory = service.listCategoriesUsedInForm();

		model.addAttribute("category", new Category());
		model.addAttribute("listCategory", listCategory);
		model.addAttribute("pageTitle", "Create New Category");

		return "categories/category_form";
	}
//	public String newCategory(Model model) {
//		Category category = new Category();
//		List<Category> listCategory = service.listCategory();
//
//		model.addAttribute("category", category);
//		model.addAttribute("pageTitle", "Create New Category");
//		model.addAttribute("listCategory", listCategory);
//
//		return "categories/category_form";
//	}

	@GetMapping("/categories/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		List<Category> listCategories = service.listAll();
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories, response);
	}

	@GetMapping("/categories/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<Category> listCategories = service.listAll();
		CategoryExcelExporter exporter = new CategoryExcelExporter();
		exporter.export(listCategories, response);
	}

	@GetMapping("/categories/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<Category> listCategories = service.listAll();
		CategoryPdfExporter exporter = new CategoryPdfExporter();
		exporter.export(listCategories, response);
	}

	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Category category = service.editCategory(id);

			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

			return ("categories/category_form");
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}

	@PostMapping("/categories/save")
	public String savedCategory(Category category, RedirectAttributes redirectAttributes, @RequestParam("category_image")MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(filename);
			Category saveCategory = service.save(category);

			String uploadDir = "category-photos/" + saveCategory.getId();

			FileUpLoadUtil.cleanDir(uploadDir);
			FileUpLoadUtil.saveFile(uploadDir, filename, multipartFile);
		} else {
			if (category.getImage().isEmpty()) category.setImage(null);
			service.save(category);
		}

		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");

		return getRedirectURLtoAffectedCategory(category);
//		return "redirect:/categories";
	}

	private String getRedirectURLtoAffectedCategory(Category category) {
		String firstPartOfName = category.getName().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfName;
	}

	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, @Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
		System.out.println("Sort Field: " + sortField);
		System.out.println("Sort Order: " + sortDir);

		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);

//		Page<Category> page = service.listByPage(pageNum, sortField, sortDir, keyword);
//		List<Category> listCategories = page.getContent();

		long startCount = (pageNum - 1) * CategoryService.CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "categories/categories";
	}
}
