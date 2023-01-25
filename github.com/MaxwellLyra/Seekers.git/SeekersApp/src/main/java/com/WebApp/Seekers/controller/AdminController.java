package com.WebApp.Seekers.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.exceptions.TemplateInputException;

import com.WebApp.Seekers.dto.AdminRequestNewUser;
import com.WebApp.Seekers.model.Email;
import com.WebApp.Seekers.model.User;
import com.WebApp.Seekers.repository.UserRepository;

@Controller
@RequestMapping("admin")
public class AdminController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping()
	public String goToAdminPage(HttpSession session) {
		// try&catch block in case user tries to access this page without being logged
		// in (catching for null pointer - server 500 error)
		try {
			// checking user role for clearance (only admin allowed) if different role catch
			// block return fail page
			if (!session.getAttribute("role").toString().equalsIgnoreCase("ADM")) {
				return "/login/loginFail";
			}
		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}

		return "admin";
	}

	@GetMapping("display")
	public String displayUsers(Model model, HttpSession session) {
		// checking user role for clearance
		if (session.getAttribute("role").toString() == "ADM") {
			return "admin";
		}

		// displaying all users in database
		List<User> users = userRepository.findAll();
		model.addAttribute("users", users);
		return "admin";
	}

	// hiding all users - redirecting admin page
	@GetMapping("hide")
	public String hideUsers() {
		return "redirect:/admin";
	}

	// directing admin to form page to add new user
	@GetMapping("addUser")
	public String addUser(AdminRequestNewUser request) {
		return "admin/addUserForm";
	}

	@PostMapping("new")
	public String newJob(@Valid AdminRequestNewUser request, BindingResult result, Model model,
			RestTemplate restTemplate,
			@RequestParam(value = "confirmPassword", required = false) String confirmPassword) {

		// return form page if any filled is blank - evaluating made by using @notBlank
		// annotation in adminRequestNewUser DTO and @Valid in Post Mapping method
		if (result.hasErrors()) {
			return "admin/addUserForm";
		}

		// checking if email given already exists in database, if yes than error message
		// is added to model and same view is returned informing the issue
		List<User> usersAlreadyExist = userRepository.findUserByEmail(request.getEmail());

		if (!usersAlreadyExist.isEmpty() && request.getEmail().equals(usersAlreadyExist.get(0).getEmail())) {
			String emailError = "This email is already registered!";
			model.addAttribute("emailError", emailError);

			return "admin/addUserForm";
		}

		// try&catch block for Template Input Exception in called of failed call to API
		try {

			// EMAIL API

			// base url to call API assigned to variable "url" and getting email from the
			// object user and assigning to "userEmail"
			String url = "https://api.eva.pingutil.com/email?email=";
			String userEmail = request.getEmail();

			// creating object "emails" and calling API using restTemplate
			Email emails = restTemplate.getForObject(url + userEmail, Email.class);

			// checking attributes "deliverable" and "spam" from api response saved in
			// object Email, if deliverable is false OR spam is true
			// returning view "registrationFailed" so that user knows that this email isn't
			// valid
			if (emails.getData().isDeliverable() == false || emails.getData().isSpam() == true) {
				String emailError = "The email used is not valid!";
				model.addAttribute("emailError", emailError);

				return "admin/addUserForm";
			}

		} catch (TemplateInputException TIE) {
			return "redirect:/admin";
		}

		// checking if password and confirm password request parameter match, if not
		// same view with error message is displayed
		if (!request.getPassword().equals(confirmPassword)) {
			String passwordError = "Password mismatch";
			model.addAttribute("passwordError", passwordError);

			return "admin/addUserForm";
		}

		// is requirements are met password is encoded and set password method sends it
		// t DTO
		String confirmPassEncoded = request.passwordEncoder(confirmPassword);
		request.setPassword(confirmPassEncoded);

		// DTO saved into user object and saved in database
		User user = request.toUser();
		userRepository.save(user);

		return "redirect:/admin";
	}

	@PostMapping("search")
	public String searchUser(@RequestParam(value = "searchParam", required = false) String userEmail, Model model) {

		// finding user by email provided by admin in input box inside form
		List<User> users = userRepository.findUserByEmail(userEmail);

		// returning object if there's any match
		model.addAttribute("users", users);
		model.addAttribute("userEmail", userEmail);

		// if search button is pressed by input filled is empty than only redirect to
		// same page
		if (userEmail == null) {
			return "redirect:/admin";
		}
		return "admin";

	}
	
	// delete method that can be accessible to admin to remove unused accounts in the future (UNCOMMENT DELETE BUTTON IN ADMIN DASHBOARD TO USE)
		/*
		 * @GetMapping("delete") public String deleteAccount(@RequestParam(value =
		 * "param1", required = false) long userId) { List<User> users =
		 * userRepository.deleteById(userId);
		 * 
		 * return "admin"; }
		 */
	
}
