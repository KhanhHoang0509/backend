package com.shoppingcart.admin.user;

import com.shoppingcart.admin.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;


// Viết JUnit cho RoleRepositoryTests
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Chay test case voi db that
@Rollback(false) // mac dinh se rollback -> cac thay doi se ko dc luu

public class RoleRepositoryTests {
	@Autowired // Lấy 1 spring bean ra sử dụng
	private RoleRepository repo;

	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "manage everything");
		Role savedRole = repo.save(roleAdmin);

		assertThat(savedRole.getId()).isGreaterThan(0); // kiem tra id > 0 laf save thanh cong
	}

	@Test
	public void testCreateRestRole() {
		Role roleSalesperson = new Role("Salesperson", "manage product price, " + "customers, shipping, orders and sales reports");

		Role roleEditor = new Role("Editor", "manage categories, brands, " + "products, articles and menus");

		Role roleShipper = new Role("Shipper", "view products, view orders, " + "and update order status");

		Role roleAssistant = new Role("Assistant", "manage questions and reviews");

		repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
	}
}
