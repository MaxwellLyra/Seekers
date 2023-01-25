package com.WebApp.Seekers.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.WebApp.Seekers.model.Job;
import com.WebApp.Seekers.model.User;
import com.WebApp.Seekers.repository.JobRepository;
import com.WebApp.Seekers.repository.UserRepository;

@Controller
@RequestMapping("analytics")
public class AnalyticsController {

	@Autowired
	JobRepository jobRepository;

	@Autowired
	UserRepository userRepository;

	@GetMapping()
	public String goToAnalyticsPage(Model model, HttpSession session) {
		// catching null pointer in case of access without being logged in
		try {

		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}

		// try&catch block for Arithmetic Exception to return view even if dived by zero
		// happens
		try {
			// 1 - accessing job repository and counting amount of items stored by different
			// STATUS (data from all users to be use as analysis against specific user data)

			// getting number of JOBS and dividing by number of USERS to obtain average
			// number of jobs applied
			List<User> listUsers = userRepository.findUserByRole("user");
			long intNumberUsers = listUsers.size();

			// getting total number of job instances in repository
			long intNumberJobs = jobRepository.count();

			// getting average of jobs applied by users
			long avgJobs = intNumberJobs / intNumberUsers;
			model.addAttribute("avgJobs", avgJobs);

			// number of jobs of ALL users with "interviewed" status divided by number of
			// users
			long numberInterviewed = jobRepository.countByStatus("interviewed") / intNumberUsers;
			model.addAttribute("numberInterviewed", numberInterviewed);

			// number of jobs of ALL users with "offered" status divided by number of users
			long numberOffered = jobRepository.countByStatus("offered");
			long numberOfferedAvg = numberOffered / intNumberUsers;
			model.addAttribute("numberOfferedAvg", numberOfferedAvg);

			// number of jobs of ALL users with "declined" status divided by number of users
			long numberDeclined = jobRepository.countByStatus("declined") / intNumberUsers;
			model.addAttribute("numberDeclined", numberDeclined);

			// 2 - fetching user data (INDIVIDUAL) from job repository (by user email)

			// getting all data related to specific user (get by user email)
			List<Job> userData = jobRepository.findByUserEmail(session.getAttribute("email").toString());

			// getting size of List to find total number of job applications of SPECIFIC
			// user
			int userJobsTotal = userData.size();
			model.addAttribute("userJobsTotal", userJobsTotal);

			// getting count of jobs present in the list which status match the search
			// "applied" and also user email
			userData = jobRepository.findByStatusAndUserEmail("applied", session.getAttribute("email").toString());
			int userJobsApplied = userData.size();
			model.addAttribute("userJobsApplied", userJobsApplied);

			// getting count of jobs present in the list which status match the search
			// "interviewed" and also user email
			userData = jobRepository.findByStatusAndUserEmail("interviewed", session.getAttribute("email").toString());
			int userJobsInterviewed = userData.size();
			model.addAttribute("userJobsInterviewed", userJobsInterviewed);

			// getting count of jobs present in the list which status match the search
			// "offered" and also user email
			userData = jobRepository.findByStatusAndUserEmail("offered", session.getAttribute("email").toString());
			int userJobsOffered = userData.size();
			model.addAttribute("userJobsOffered", userJobsOffered);

			// getting count of jobs present in the list which status match the search
			// "declined" and also user email
			userData = jobRepository.findByStatusAndUserEmail("declined", session.getAttribute("email").toString());
			int userJobsDeclined = userData.size();
			model.addAttribute("userJobsDeclined", userJobsDeclined);

			// 3 - Building URL to call QuickChart API

			// base URL + initial query
			String urlQC = "https://quickchart.io/chart/chart?w=220&h=200&v=2.9.4&c={ type: 'bar', data: { labels: [";

			// String variables that will become the labels on the generated chart
			String labelOne = "'Applied',";
			String labelTwo = "'Interviewed',";
			String labelThree = "'Offered',";
			String labelFour = "'Decl/Rejec']";

			// part of String URL query to insert numbers (data) to the api call
			String dataSet = ",datasets:[{label:'Average',data:[";

			String dataSet2 = "]}, { label: 'You', data: [";

			// end of URL string
			String closing = "] }, ], }, }";

			// concatenating labels and data to FINAL String "src" to call the API
			String src = urlQC + labelOne + labelTwo + labelThree + labelFour + dataSet + avgJobs + ","
					+ numberInterviewed + "," + numberOfferedAvg + "," + numberDeclined + dataSet2 + userJobsTotal + ","
					+ userJobsInterviewed + "," + userJobsOffered + "," + userJobsDeclined + closing;

			// 4 -adding the URL to the model with generated graph
			model.addAttribute("src", src);

			// 5 - Getting average of job applied to get one offer
			long avgOfOfferReceived = intNumberJobs / numberOffered;

			model.addAttribute("avgOfOfferReceived", avgOfOfferReceived);
		} catch (ArithmeticException AE) {

		}

		return "analytics";
	}

}
