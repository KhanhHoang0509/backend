package com.shoppingcart.admin.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User extends IdBaseEntity {

	@Column(length = 128, nullable = false, unique = true)
	private String email;

	@Column(length = 64, nullable = false)
	private String password;

	@Column(name = "first_name", length = 45, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 45, nullable = false)
	private String lastName;

	@Column(length = 64)
	private String photos; // Lưu tên img

	private boolean enabled;

	// Mối quan hệ giữa user và roles là ManytoMany -> tạo ra table thứ 3 là
	// user_roles
	// column user_id trỏ tới column id của table users
	// column role_id trỏ tới column id của table roles

	@ManyToMany(fetch = FetchType.EAGER) // Tạo table thứ 3 để nối 2 khoá chính của 2 table. EAGER -> khi get user se lay all cua role tuong ung voi user, LAZY -> chi get user
	@JoinTable(name = "users_roles", // Khai báo tên users trước vì khai báo trong class user
			joinColumns = @JoinColumn(name = "user_id"), // Khai báo tới column của class hiện tại
			inverseJoinColumns = @JoinColumn(name = "role_id")) // Nối với class khác
	private Set<Role> roles = new HashSet<>();

	//@OneToMany(mapped by ="user123")(1 user cos nhieu role nen onetomany) mapped by phai anh xa qua Role nen Role phai co user123
	//private Set<Role> children = new HashSet<>();

	public User() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User(String email, String password, String firstName, String lastName) {
		super();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", photos=" + photos + ", enabled=" + enabled + ", roles=" + roles + "]";
	}

	@Transient
	public String getFullName() {
		return firstName + " " + lastName;
	}

	@Transient
	public String getPhotosImagePath() {
		if (id == null || photos == null)
			return "/images/default-user.png";

		return "/user-photos/" + this.id + "/" + this.photos;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public boolean hasRole(String roleName) {
		Iterator<Role> iterator = roles.iterator();

		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role.getName().equals(roleName)) {
				return true;
			}
		}
		return false;
	}
}
