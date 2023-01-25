package com.WebApp.Seekers.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.WebApp.Seekers.dto.RequestNewUser;
import com.WebApp.Seekers.model.User;
import com.WebApp.Seekers.repository.UserRepository;

@Controller
@RequestMapping("login")
public class LoginController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping()
	public String goToLogin(Model model) {
		// making user object available for login page/view
		User user = new User();
		model.addAttribute("user", user);
		return "login/login";
	}

	@PostMapping()
	public String validateAccount(User user, Model model, HttpSession session, RequestNewUser requestUser) {
		// try&catch block for index out of bounds in case of search can't find email and password inside list of users
		try {
			//encoding password to check in database similar match
			String encodedPass = requestUser.passwordEncoder(user.getPassword());
			List<User> users = userRepository.findByEmailAndPassword(user.getEmail(), encodedPass);
			
			//creating variables for user email, username and role
			String email = users.get(0).getEmail();
			String username = users.get(0).getUsername();
			String role = users.get(0).getRole();

			//creating session object to be used as global object while browser is open and application is running
			session.setAttribute("email", email);
			session.setAttribute("username", username);
			session.setAttribute("role", role);

			//redirect user to admin page if is ADM, otherwise (user) return home view
			if (users.get(0).getRole().equalsIgnoreCase("adm")) {
				return "admin";
			}

		} catch (IndexOutOfBoundsException IOBE) {
			//adding error message to model to be displayed for user
			String messageError = "Invalid credentials";
			model.addAttribute("messageError", messageError);
			
			return "login/login";
		}

		return "redirect:/home";
	}

}
