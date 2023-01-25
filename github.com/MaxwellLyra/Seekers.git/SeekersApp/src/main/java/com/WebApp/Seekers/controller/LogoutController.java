package com.WebApp.Seekers.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("logout")
public class LogoutController {
	
	@GetMapping()
	public String logout(HttpSession session) {
		
		//removing session attributes to perform user logout
		session.removeAttribute("email");
		session.removeAttribute("username");
		session.removeAttribute("role");
		
		//redirect to home page 
		return "redirect:/"; 
	}

}
