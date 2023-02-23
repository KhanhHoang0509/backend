package com.shoppingcart.admin.category;


import com.shoppingcart.admin.entity.Category;
import com.shoppingcart.admin.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CategoryService {

	public static final int CATEGORIES_PER_PAGE = 4;

	@Autowired
	private CategoryRepository cateRepo;

	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);

		Page<Category> pageCategories = null;

		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = cateRepo.search(keyword, pageable);//tim theo keyword
		} else {
			pageCategories = cateRepo.findRootCategories(pageable);//tim id parent, 1 page co 4 cate lon nhat
		}

		List<Category> rootCategories = pageCategories.getContent();

		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());

		if (keyword != null && !keyword.isEmpty()) {
			List<Category> searchResults = pageCategories.getContent();
			for (Category category : searchResults) {
				category.setHasChildren(category.getChildren().size() > 0);//kiem tra co children
			}
			return searchResults;
		} else {
			return listHierarchicalCategories(rootCategories, sortDir);
		}
	}

	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));

			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));

				listHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}
		return hierarchicalCategories;
	}

	private void listHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();

			hierarchicalCategories.add(Category.copyFull(subCategory, name));//copyFull lay ra toan bo

			listHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}
	}

	public List<Category> listCategory() {
		return (List<Category>) cateRepo.findAll();
	}

	public List<Category> listAll() {
		return (List<Category>) cateRepo.findAll(Sort.by("name").ascending());
	}

	public void deleteCategory(Integer id) throws CategoryNotFoundException {
		Long countById = cateRepo.countById(id);

		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not delete any category with ID " + id);
		}
		cateRepo.deleteById(id);
	}

	public void updateCategory(Integer id, Boolean enabled) {
		cateRepo.updateEnabled(id, enabled);
	}

	public Category save(Category category) {
		return cateRepo.save(category);
	}

	public Category editCategory(Integer id) throws CategoryNotFoundException{
		try {
			return cateRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not delete any category with ID: " + id);
		}
	}

	public boolean isNameunique(Integer id, String name) {
		Category categoryByName = cateRepo.getCategoryByName(name);

		if (categoryByName == null) {
			return true;
		}

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			if (categoryByName != null)
				return false;
		} else {
			if (categoryByName.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public List<Category> listCategoriesUsedInForm() {
		List <Category> categoriesUsedInForm = new ArrayList<>();
		
		Iterable <Category> categoriesInDB = cateRepo.findRootCategories(Sort.by("name").ascending());//tim category xep name tang dan
		
		for (Category category : categoriesInDB) {
			categoriesUsedInForm.add(Category.copyIdAndName(category));//tao copyIdAndName de dung Id vs Name, ko can dung het add(Category)

			Set<Category> children = category.getChildren();
			
			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
				
				listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
			}
		}
		return categoriesUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {//newSubLevel > 1 se them 1 "--"
				name += "--";
			}
			name += subCategory.getName();

			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));//add vao list categoriesUsedInForm dang lap

			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category> () { //Comparator tra ve -1 0 1, equal tra ve String boolean
			@Override
			public int compare(Category cat1, Category cat2) {
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());//1-2 tang dan
				} else {
					return cat2.getName().compareTo(cat1.getName());// giam dan
				}
			}
		});
		sortedChildren.addAll(children);

		return sortedChildren;
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id ==0);

		Category categoryByName = cateRepo.findByName(name);

		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = cateRepo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}

			Category categoryByAlias = cateRepo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		return "OK";
	}




}
