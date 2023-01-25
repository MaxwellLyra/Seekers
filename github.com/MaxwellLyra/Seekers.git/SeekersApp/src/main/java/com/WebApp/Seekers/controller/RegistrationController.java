package com.WebApp.Seekers.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.WebApp.Seekers.dto.RequestNewUser;
import com.WebApp.Seekers.model.Email;
import com.WebApp.Seekers.model.User;
import com.WebApp.Seekers.repository.UserRepository;

@Controller
@RequestMapping("registration")
public class RegistrationController {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Autowired
	private UserRepository userRepository;

	@GetMapping("form")
	public String goToRegistrationPage(RequestNewUser requestUser) {
		return "registration/form";
	}

	@PostMapping("form")
	public String newUser(@Valid @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
			RequestNewUser requestUser, BindingResult resultUser, RestTemplate restTemplate, User user, Model model) {
		//return for with error message in case filled are blank (@Valid and @NotBlank annotations handling empty fileds issue)
		if (resultUser.hasErrors()) {
			return "registration/form";
		}

		// find user by email provided in form
		List<User> users = userRepository.findUserByEmail(requestUser.getEmail());

		// if already exists return view warning that this email isn't available to use
		if (!users.isEmpty() && users.get(0).getEmail().equals(requestUser.getEmail())) {
			String emailError = "This email is already registered";
			model.addAttribute("emailError", emailError);
			return "registration/form";

		}
		// getting request parameter (confirm password) and checking if value is the
		// same found in the first "password" input box if not equals - error message in thymeleaf is displayed
		if (!confirmPassword.equals(user.getPassword())) {
			String passwordError = "Password mismatch!";
			model.addAttribute("passwordError", passwordError);
			return "registration/form";
		}

		// EMAIL API
		// base url to call API assigned to variable "url" and getting email from the object user and assigning to "userEmail"
		String url = "https://api.eva.pingutil.com/email?email=";
		String userEmail = user.getEmail();

		// creating object "emails" and calling API using restTemplate
		Email emails = restTemplate.getForObject(url + userEmail, Email.class);

		// returning form if object is null (no email added)
		if (emails == null) {
			return "registration/form";
		}

		// adding object to model so that attributes are available in html file
		model.addAttribute("emails", emails);

		// checking attributes "deliverable" and "spam" from api response saved in object Email, if deliverable is false OR spam is true
		// returning view "registrationFailed" so that user knows that this email isn't valid
		if (emails.getData().isDeliverable() == false || emails.getData().isSpam() == true) {
			String emailError = "This email is not valid!";
			model.addAttribute("emailError", emailError);
			return "registration/form";
		}

		// if email email is valid and all information necessary filed then the method
		// "toUser" will save information from DTO to user and the "save" method will save this user in the database
		String passEncoded = requestUser.passwordEncoder(requestUser.getPassword());
		requestUser.setPassword(passEncoded);
		user = requestUser.toUser();
		userRepository.save(user);

		return "registration/registrationSuccessful";
	}
}
