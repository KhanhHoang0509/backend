package com.shoppingcart.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoppingcart.admin.entity.Role;
import com.shoppingcart.admin.entity.User;

@Service //Đánh dấu một Class là tầng Service, phục vụ các logic nghiệp vụ, xử lý logic
@Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 4;//USERS_PER_PAGE = 4 co 4 dong du lieu
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); //ascending: sort tang(mac dinh), descending: sort giam

		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

		if (keyword != null) {
			return userRepo.findAll(keyword, pageable);
		}
		//pageNum - 1: page index, USERS_PER_PAGE: so page
		return userRepo.findAll(pageable);
	}

	public List<User> listAll() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending()); //tra ve tat ca user, ascending: sort tang(mac dinh)
	}

	public User editUser(Integer id) throws UserNotFoundException{
		try {
			return userRepo.findById(id).get(); //dung findById de lay ra user de hien thi tren html
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}

	public void deleteUser(Integer id) throws UserNotFoundException{
		Long countById = userRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not delete any user with ID " + id);
		}
			userRepo.deleteById(id);
	}

	public void updatedUser(Integer id, boolean enabled) {
		userRepo.updateEnabled(id, enabled);
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null); //tao user moi

		if (isUpdatingUser) {//update user
			User existingUser = userRepo.findById(user.getId()).get(); //(1)

			if (user.getPassword().isEmpty()) { //ko nhap pass moi
				user.setPassword(existingUser.getPassword()); //ko can ma hoa, lay tu (1) qua
			} else {
				encodePassword(user); // ma hoa password
			}
		} else {
			encodePassword(user);
		}
		return userRepo.save(user);
	}

	public boolean isEmailunique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);

		if (userByEmail == null)
			return true; // ko trung email

		boolean isCreatingNew = (id == null);//phan biet create va edit, create co id la new user

		if (isCreatingNew) {//create
			if (userByEmail != null)
				return false;
		} else {// edit
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}

	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}

		if (userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());

		return userRepo.save(userInDB);
	}
}
