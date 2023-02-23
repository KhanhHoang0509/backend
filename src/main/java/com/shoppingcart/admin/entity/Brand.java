package com.shoppingcart.admin.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
public class Brand extends IdBaseEntity{
	@Column(length = 45, nullable = false, unique = true)
	private String name;

	@Column(length = 128, nullable = false)
	private String logos;

	public static Brand copyIdAndName(Brand brand) {
		Brand copyBrand = new Brand();
		copyBrand.setId(brand.getId());
		copyBrand.setName(brand.getName());

		return copyBrand;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "brands_categories", joinColumns = @JoinColumn(name = "brand_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories = new HashSet<>();

//	@OneToMany(mappedBy = "brand")
//	private Set<Products> products = new HashSet<>();

	public Brand() {
	}

	public Brand(String name, String logos) {
		this.name = name;
		this.logos = logos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogos() {
		return logos;
	}

	public void setLogos(String logos) {
		this.logos = logos;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Transient
	public String getLogosImagePath() {
		if (id == null || logos == null)
			return "/images/default-user.png";

		return "/brand-logos/" + this.id + "/" + this.logos;
	}
}
