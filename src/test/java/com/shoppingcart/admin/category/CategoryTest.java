package com.shoppingcart.admin.category;

import com.shoppingcart.admin.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryTest {
	@Autowired
	private CategoryRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCategory() {
		Category computers = new Category("computers");

		Category desktop = new Category("desktop", computers);

		Category laptops = new Category("laptops", computers);
		Category laptop1= new Category("laptop1", laptops);
		Category laptop2 = new Category("laptop2", laptops);
		Category laptop21 = new Category("laptop21", laptop2);
		Category laptop211 = new Category("laptop211", laptop21);
		Category laptop3 = new Category("laptop3", laptops);

		Category computerComponents = new Category("computer components", computers);
		Category memoryA = new Category("memory A", computerComponents);
		Category a1 = new Category("a1", memoryA);
		Category a2 = new Category("a2", a1);
		Category a3 = new Category("a3", a2);
		Category a4 = new Category("a4", a3);
		Category memoryB = new Category("memory B", computerComponents);
		Category b1 = new Category("b1", memoryB);
		Category b2 = new Category("b2", b1);

		repo.saveAll(List.of(computers, desktop, laptops, laptop1, laptop2, laptop21, laptop211, laptop3, computerComponents, memoryA, a1, a2, a3, a4, memoryB, b1, b2));
	}

	@Test
	public void testParentCallChildren(){
		Category computers = entityManager.find(Category.class, 18);
		System.out.println(computers.getName());
		testPrintChildren(computers.getChildren(), "--");
	}

	//đệ quy
	public void testPrintChildren(Set<Category> listCategory, String underline) {
		for (Category c : listCategory) {
			System.out.print(underline);
			System.out.println(c.getName());

			if (c.getChildren() != null) {
				testPrintChildren(c.getChildren(), underline + "--");
			}
		}
	}
}
