package com.WebApp.Seekers.dto;

import javax.validation.constraints.NotBlank;

import com.WebApp.Seekers.model.Review;


public class RequestNewReview {
	
	private Long id;
	//not blank annotation to return an error if user don't provide input in form
	@NotBlank
	private String company;
	
	@NotBlank
	private String userReview;
	
	private int star;
	private String userUsername;
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getUserReview() {
		return userReview;
	}
	public void setUserReview(String userReview) {
		this.userReview = userReview;
	}
	
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserUsername() {
		return userUsername;
	}
	public void setUserUsername(String userUsername) {
		this.userUsername = userUsername;
	}
	
	//setting job object attributes from dto
	public Review toReview() {
		Review review = new Review();
		review.setCompany(company);
		review.setUserReview(userReview);
		review.setStar(star);
		review.setUserUsername(userUsername);
		return review;
	}
}
