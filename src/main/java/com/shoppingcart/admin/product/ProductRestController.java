//package com.shoppingcart.admin.product;
//
//import com.shoppingcart.admin.brand.BrandNotFoundException;
//import com.shoppingcart.admin.brand.BrandService;
//import com.shoppingcart.admin.brand.CategoryDTO;
//import com.shoppingcart.admin.entity.Brand;
//import com.shoppingcart.admin.entity.Category;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//@RestController
//public class ProductRestController {
//
//	@Autowired
//	ProductService service;
//
//	@Autowired
//	BrandService BrandService;
//
//	@GetMapping("/brandCate/{id}")
//	public List<CategoryDTO> categories (@PathVariable(name = "id") Integer id) throws BrandNotFoundException {
//
//		Brand brand = BrandService.returnBrand(id);
//		Set<Category> categories = brand.getCategories();
//
//		List<CategoryDTO> listCategory = new ArrayList();
//		for (Category category : categories) {
//			CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
//			listCategory.add(categoryDTO);
//
//		}
//		return listCategory;
//	}
//}
