package com.WebApp.Seekers.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.WebApp.Seekers.model.Job;
import com.WebApp.Seekers.repository.JobRepository;

@Controller
@RequestMapping("/home")
public class HomeController {

	@Autowired
	private JobRepository jobRepository;

	@GetMapping()
	public String home(HttpSession session, Model model) {
		// catching null pointer in case of access without being logged in
		try {
			// checking role for clearance
			if (session.getAttribute("role").toString() == "USER" || session.getAttribute("role").toString() == "ADM") {

				return "home";
			}
		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}

		// displaying all jobs in repository linked with this user only (by session email parameter)
		List<Job> jobs = jobRepository.findByUserEmail(session.getAttribute("email").toString());
		model.addAttribute("jobs", jobs);
		return "home";

	}

	//get request from home anchor tags related to the type of status of jobs
	@GetMapping("/{status}")
	public String byStatus(@PathVariable("status") String status, Model model, HttpSession session) {
		// catching null pointer in case of access without being logged in and role check
		try {
			if (session.getAttribute("role").toString() == "USER" || session.getAttribute("role").toString() == "ADM") {
				return "/{status}";
			}
		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}

		//displaying only the jobs with "this" status (from get requests)
		List<Job> jobs = jobRepository.findByStatusAndUserEmail(String.valueOf(status), session.getAttribute("email").toString());
		model.addAttribute("jobs", jobs);
		
		return "home";
	}

	@GetMapping("update")
	public String updateStatus(@RequestParam(value = "param1", required = false) long jobId,
			@RequestParam(value = "param2", required = false) String status) {
		//update status of jobs when user selects type of status from drop box 
		//the parameter request1 is the userID and request2 comes from the value of option selected by user)
		List<Job> jobs = jobRepository.findById(jobId);

		//updating new status in database
		Job job = jobs.get(0);
		job.setStatus(status);

		jobRepository.save(job);

		return "redirect:/home";
	}

	@GetMapping("delete")
	public String deleteStatus(@RequestParam(value = "param1", required = false) long jobId,
			@RequestParam(value = "param2", required = false) String newStatus) {
		
		//using default method to delete jobs selected by user by using the user ID
		List<Job> jobs = jobRepository.deleteById(jobId);

		return "home";
	}
	
	//handler for Illegal Argument Exception for byStatus method
	@ExceptionHandler(IllegalArgumentException.class)
	public String onError() {
		return "redirect:/home";
	}
}
