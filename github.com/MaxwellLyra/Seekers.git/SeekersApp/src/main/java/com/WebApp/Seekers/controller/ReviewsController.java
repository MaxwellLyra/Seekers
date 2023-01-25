package com.WebApp.Seekers.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.WebApp.Seekers.dto.RequestNewReview;
import com.WebApp.Seekers.model.Content;
import com.WebApp.Seekers.model.Review;
import com.WebApp.Seekers.model.User;
import com.WebApp.Seekers.repository.ReviewRepository;

@Controller
@RequestMapping("reviews")
public class ReviewsController {

	@Autowired
	private ReviewRepository reviewRepository;

	@GetMapping()
	public String goToReviewsPage(HttpSession session) {
		// catch block in case user tries to access this page without being logged in
		// (catching for null pointer - server 500 error)
		try {
			if (session.getAttribute("role").toString() == "USER" || session.getAttribute("role").toString() == "ADM") {
				return "reviews";
			}
		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}
		return "reviews";
	}

	@GetMapping("form")
	public String reviewForm(RequestNewReview requestReview, HttpSession session) {
		// catch block in case user tries to access this page without being logged in and role check
		try {
			if (session.getAttribute("role").toString() == "USER" || session.getAttribute("role").toString() == "ADM") {
				return "reviews";
			}
		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}

		return "review/form";
	}

	@GetMapping("/{company}")
	public String byCompany(@PathVariable("company") String company, Model model, HttpSession session) {
		//using dropdown anchor tags values and path variable to determin which companies will be displayed in page
		List<Review> reviews = reviewRepository.findByCompany(String.valueOf(company));
		model.addAttribute("reviews", reviews);
		model.addAttribute("company", company);

		// Display the name of the company in the header if list isn't empty
		if (!reviews.isEmpty()) {
			String companyHeader = reviews.get(0).getCompany();
			model.addAttribute("companyHeader", companyHeader);
		}

		// display to user that there aren't reviews for this company in case of list hasn't been populated
		if (reviews.isEmpty()) {
			String companyHeader = "There are no reviews for this company";
			model.addAttribute("companyHeader", companyHeader);
		}

		// calculate all satisfaction points given by users for a certain company and obtain average
		if (!reviews.isEmpty()) {

			// creating int variables for counter and total number of reviews (to get average of satisfaction [stars])
			int counter = reviews.get(0).getStar();
			int numbStars = reviews.size();

			// for loop until end of reviews list to get sum of total satisfaction stars given by users
			for (int i = 1; i < reviews.size(); i++) {
				counter = counter + reviews.get(i).getStar();
			}

			// getting average by dividing sum of all rating by size of list (number of reviews given)
			int starsAvg = counter / numbStars;

			model.addAttribute("starsAvg", starsAvg);

		}

		return "reviews";
	}

	@PostMapping("new")
	public String addReview(@Valid RequestNewReview requestReview, BindingResult resultReview,
			RestTemplate restTemplate, Model model, HttpSession session) {
		//returning same view with default error message if any filled is empty (@Valid and @NotBlank annotations)
		if (resultReview.hasErrors()) {
			return "review/form";
		}

		//base url to call text validator api
		String url = "https://www.purgomalum.com/service/json?text=";

		//adding response to an instance of content object
		Content content = restTemplate.getForObject(url + requestReview.getUserReview(), Content.class);

		//checking if text is inappropriate by using customized method inside content class
		if (content.isOffensive()) {
			//returning view with error message 
			String message = "Sorry, message not allowed due to the use of special characters (*) and/or offensive words";
			model.addAttribute("message", message);

			return "review/form";
		}
		
		//setting username to review to link user to review be displayed in reviews page
		requestReview.setUserUsername(session.getAttribute("username").toString());
		Review review = requestReview.toReview();
		reviewRepository.save(review);
		return "redirect:/reviews";
	}

	//search methods that can be available to user to find company by name if dropdown list is replaced or becomes too long 
	/*
	 * @PostMapping("search") public String newSearch(@RequestParam(value =
	 * "companyS", required = false) String companyS, Model model) { List<Review>
	 * reviews = reviewRepository.findByCompany(companyS);
	 * model.addAttribute("reviews", reviews); model.addAttribute("companyS",
	 * companyS); if (companyS.equals(null)) { return "redirect:/reviews"; } return
	 * "reviews";
	 * 
	 * }
	 * 
	 * @GetMapping("reviews/clearSearch") public String clearSearch() { return
	 * "redirect:/reviews"; }
	 */
}
