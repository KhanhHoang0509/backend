package com.shoppingcart.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shoppingcart.admin.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> { //Đánh dấu một Class Là tầng Repository, phục vụ truy xuất dữ liệu, giao tiếp với DB
	// tham so dau tien la Entity, sau la kieu khoa chinh
	// CrudRepository là 1 Srping bean nên ko cần @Repository

	//checkDuplicateEmail
	@Query("SELECT u FROM User u WHERE u.email = :email") //truy van dua tren Entity
	User getUserByEmail(@Param("email") String email); //truyen @param(eamil) vao sau dau : -> :email

	 Long countById(Integer id); //Lay thuoc tinh ben entity firstName -> FirstName, id -> Id (jpa)

	//	@Modifying dung cho query update, insert, update
	//	@Query("update User u set u.disabled = ?2 where u.id = ?1")


	@Query("update User u set u.enabled = ?2 where u.id = ?1")  //?1 la tham so 1, ?2 la tham so 2
	@Modifying
	void updateEnabled(Integer id, boolean enabled);

	@Query("select u from User u where concat(u.id, ' ', u.email, ' ', u.firstName, ' '," + " u.lastName) like %?1%")
//	@Query("select u from User u where u.firstName like %?1% or u.lastName like %?1% or u.email like %?1%")
	Page<User> findAll(String keyword, Pageable pageable);



//	@Query("select u from User u where u.email = :email")
//	public User getUserByEmail(@Param("email") String email);
}
