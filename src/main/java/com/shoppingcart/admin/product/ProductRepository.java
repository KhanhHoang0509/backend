package com.shoppingcart.admin.product;

import com.shoppingcart.admin.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Products, Integer> {
	@Query("select u from Products u where concat(u.id, ' ', u.mainImage, ' ', u.name) like %?1%")
	Page<Products> findAll(String keyword, Pageable pageable);

	Long countById(Integer id);

	@Query("update Products u set u.enabled = ?2 where  u.id = ?1")
	@Modifying
	void updateEnabled(Integer id, Boolean enabled);

	@Query("update Products u set u.inStock = ?2 where  u.id = ?1")
	@Modifying
	void updateStock(Integer id, Boolean enabled);
}
