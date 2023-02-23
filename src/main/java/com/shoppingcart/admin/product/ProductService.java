package com.shoppingcart.admin.product;

import com.shoppingcart.admin.entity.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 4;

	@Autowired
	private ProductRepository productRepo;

	public Page<Products> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); //ascending: sort tang(mac dinh), descending: sort giam

		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);

		if (keyword != null) {
			return productRepo.findAll(keyword, pageable);
		}
		//pageNum - 1: page index, USERS_PER_PAGE: so page
		return productRepo.findAll(pageable);
	}

	public List<Products> listAll() {
		return (List<Products>) productRepo.findAll(Sort.by("name").ascending());
	}

	public void deleteProduct(Integer id) throws ProductNotFoundException {
		Long countById = productRepo.countById(id);

		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not delete any product with ID " + id);
		}
		productRepo.deleteById(id);
	}


	public Products save(Products product) {
		return productRepo.save(product);
	}

	public Products editProduct(Integer id) throws ProductNotFoundException{
		try {
			return productRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not delete any product with ID: " + id);
		}
	}

	public void updateProduct(Integer id, Boolean enabled) {
		productRepo.updateEnabled(id, enabled);
	}

	public void updateStock(Integer id, Boolean enabled) {
		productRepo.updateStock(id, enabled);
	}
}
