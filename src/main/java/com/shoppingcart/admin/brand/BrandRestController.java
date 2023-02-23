package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {
	@Autowired
	private BrandService service;

//	@PostMapping("brands/check_unique")
//	public String checkUnique(Integer id, String name) {
//		return service.checkUnique(id, name);
//	}

	@GetMapping("/brands/{id}/categories") //{id} id dong
	public List<CategoryDTO> listCategoriesByBrand (@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
		List<CategoryDTO> listCategories = new ArrayList<>();//tao CategoryDTO: (data transform object) de ko bi dinh mqh parent, children

		try {
			Brand brand = service.get(brandId);
			Set<Category> categories = brand.getCategories();

			for (Category category : categories) {
				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
				listCategories.add(dto);
			}
			return listCategories;
		} catch (BrandNotFoundException e) {
			throw new BrandNotFoundRestException();
		}
	}
}
