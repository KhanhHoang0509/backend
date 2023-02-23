package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.entity.Brand;
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
public class BrandService {
	public static final int BRANDS_PER_PAGE = 10;

	@Autowired
	private BrandRepository brandRepo;

	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); //ascending: sort tang(mac dinh), descending: sort giam

		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);

		if (keyword != null) {
			return brandRepo.findAll(keyword, pageable);
		}
		//pageNum - 1: page index, USERS_PER_PAGE: so page
		return brandRepo.findAll(pageable);
	}

	public List<Brand> listAll() {
		return (List<Brand>) brandRepo.findAll(Sort.by("name").ascending());
	}

	public void deleteBrand(Integer id) throws BrandNotFoundException {
		Long countById = brandRepo.countById(id);

		if (countById == null || countById == 0) {
			throw new BrandNotFoundException("Could not delete any brand with ID " + id);
		}
		brandRepo.deleteById(id);
	}

	public Brand save(Brand brand) {
		return brandRepo.save(brand);
	}

	public Brand editBrand(Integer id) throws BrandNotFoundException {
		try {
			return brandRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new BrandNotFoundException("Could not delete any brand with ID: " + id);
		}
	}

	public List<Brand> listBrandsUsedInForm() {
		List<Brand> brandsUsedInForm = new ArrayList<>();

		Iterable<Brand> brandsInDB = brandRepo.findRootBrands(Sort.by("name").ascending());

		for (Brand brand : brandsInDB) {
			brandsUsedInForm.add(Brand.copyIdAndName(brand));
		}
		return brandsUsedInForm;
	}

	public Brand get (Integer id) throws BrandNotFoundException{
		try {
			return brandRepo.findById(id).get();
		} catch (Exception e) {
			throw new BrandNotFoundException(e.getMessage());
		}
	}

//	public String checkUnique(Integer id, String name) {
//
//	}


}
