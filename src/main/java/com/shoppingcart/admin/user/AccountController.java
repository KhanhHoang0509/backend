package com.shoppingcart.admin.user;

import com.shoppingcart.admin.FileUpLoadUtil;
import com.shoppingcart.admin.entity.User;
import com.shoppingcart.admin.security.ShoppingUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

	@Autowired
	private UserService service;

	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShoppingUserDetails loggedUser, Model model) {
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);

		model.addAttribute("user", user);

		return "users/account";
	}

	@PostMapping("/account/update")
	public String saveDetails(User user, RedirectAttributes redirectAttributes, @AuthenticationPrincipal ShoppingUserDetails loggedUser, @RequestParam("image") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.updateAccount(user);

			String uploadDir = "user-photos/" + savedUser.getId();

			FileUpLoadUtil.cleanDir(uploadDir);
			FileUpLoadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			service.updateAccount(user);
		}
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());

		redirectAttributes.addFlashAttribute("message", "Your account details have been updated");
		return "redirect:/account";
	}


	@PostMapping("/account/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User saveUser = service.save(user);

			String uploadDir = "user-photos/" + saveUser.getId();

			FileUpLoadUtil.cleanDir(uploadDir);
			FileUpLoadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");

		return "users/account";
	}
}
