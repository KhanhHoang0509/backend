package com.shoppingcart.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {//RestController return data
	@Autowired
	private CategoryService service;

//	@PostMapping("/categories/check_name")
//	public String checkDuplicateName(Integer id, String name) {
//		return service.isNameunique(id, name) ? "OK" : "Duplicated";
//	}

	@PostMapping("/categories/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias) {//ko xai param thi ten key phai trung voi params = {id: categoryId,} (js)
		return service.checkUnique(id, name, alias);
	}
}
