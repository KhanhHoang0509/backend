package com.shoppingcart.admin.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass // Phải có để gán ID qua class entity khác
public abstract class IdBaseEntity {

	@Id // Đại diện khoá chính
	@GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự động tăng
	protected Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
