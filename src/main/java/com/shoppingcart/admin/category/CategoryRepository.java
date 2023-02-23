package com.shoppingcart.admin.category;

import com.shoppingcart.admin.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> { //CrudRepository
	Long countById(Integer id);

	@Query("update Category u set u.enabled = ?2 where  u.id = ?1")
	@Modifying
	void updateEnabled(Integer id, Boolean enabled);

	@Query("select u from Category u where u.name like %?1% or u.alias like %?1%")
	Page<Category> findAll(String keyword, Pageable pageable);

	@Query("select u from Category u where u.name = :name")
	Category getCategoryByName(@Param("name") String name);

	@Query("select c from Category c where c.parent.id is null")//.parent vi no la thuoc tinh cua Entity Category(c.parent.name), is la so sanh Null
	List<Category> findRootCategories(Sort sort);


	@Query("select c from Category c where c.name like %?1%")
	Page<Category> search(String keyword, Pageable pageable);

	@Query("select c from Category c where c.parent.id is null")//parent_id
	Page<Category> findRootCategories(Pageable pageable) ;//tra ve page co the get tong so trang, null ket hop voi pageable -> 1 page co 4 cate parent lon nhat

	Category findByName(String name);

	Category findByAlias(String alias);
}
