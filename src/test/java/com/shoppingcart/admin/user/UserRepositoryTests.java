package com.shoppingcart.admin.user;

import com.shoppingcart.admin.entity.Role;
import com.shoppingcart.admin.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

//Viet Junit cho UserRepositoryTests
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository repo; //ko su dung dc UserRepository nen phai dung entityManager de lay ra id cua role

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User user = new User("hoangbaokhanh0509@gmail.com", "123", "Hoang Bao ", "Khanh");
		user.setEnabled(true);
		user.setId(123);
		user.addRole(roleAdmin);

		User saveUser = repo.save(user);

		assertThat(saveUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewUserWithTwoRole() {
		User userRavi = new User("hoangbaokhanh0509_1@gmail.com", "123", "Hoang Bao ", "Khanh_1");
		Role roleEditor = new Role(6);
		Role roleAssistant = new Role(7);

		userRavi.addRole(roleEditor);
		userRavi.addRole(roleAssistant);

		User saveUser = repo.save(userRavi);

		assertThat(saveUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		for (User user : listUsers) {
			System.out.println(user);
		}
	}
}
