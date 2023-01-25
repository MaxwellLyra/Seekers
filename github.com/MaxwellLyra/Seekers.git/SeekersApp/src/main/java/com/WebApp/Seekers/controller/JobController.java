package com.WebApp.Seekers.controller;

import javax.servlet.http.HttpSession;
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
import org.springframework.web.client.RestTemplate;

import com.WebApp.Seekers.dto.RequestNewJob;
import com.WebApp.Seekers.model.Content;
import com.WebApp.Seekers.model.Job;
import com.WebApp.Seekers.repository.JobRepository;

@Controller
@RequestMapping("job")
public class JobController {

	@Bean
	public RestTemplate restTemplateTwo(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Autowired
	private JobRepository jobRepository;

	@GetMapping("form")
	public String form(RequestNewJob request, HttpSession session) {
		// catching null pointer in case of access without being logged in
		try {

			if (session.getAttribute("role").toString() == "USER" || session.getAttribute("role").toString() == "ADM") {
				return "job/form";
			}
		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}
		return "job/form";
	}

	@PostMapping("new")
	public String newJob(@Valid RequestNewJob request, BindingResult result, HttpSession session,
			RestTemplate restTemplateTwo, Model model) {
		if (result.hasErrors()) {
			return "job/form";
		}
		
		//base url to call Text validator API (Purgomalum)
		String url = "https://www.purgomalum.com/service/json?text=";

		//instance of content class created to receive response
		Content content = restTemplateTwo.getForObject(url + request.getKeywords(), Content.class);

		//evaluating if there're any forbiden words by checking if customized method 'isOffensive'
		//created inside Content.class is true. If true, form is returned with error message prompting for new input
		if (content.isOffensive()) {
			String message = "Sorry, message not allowed due to the use of special characters and/or offensive words";
			model.addAttribute("message", message);

			return "job/form";
		}

		//if criteria is met, than a new job object is created and saved in database 
		Job job = request.toJob();
		job.setUserEmail(session.getAttribute("email").toString());
		jobRepository.save(job);

		return "redirect:/home";
	}

}
