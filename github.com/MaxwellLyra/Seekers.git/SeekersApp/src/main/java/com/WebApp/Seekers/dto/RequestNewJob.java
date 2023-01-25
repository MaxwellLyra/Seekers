package com.WebApp.Seekers.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import com.WebApp.Seekers.model.Job;

public class RequestNewJob {

	private String firstStatus = "applied";
	//not blank annotation to return an error if user don't provide input in form
	@NotBlank
	private String role;
	
	@NotBlank
	private String jobUrl;
	
	@NotBlank
	private String company;
	
	@NotBlank
	private String keywords;
	
	private String status;
	private String userEmail;
	private LocalDateTime applicationDate;
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getJobUrl() {
		return jobUrl;
	}
	public void setJobUrl(String jobUrl) {
		this.jobUrl = jobUrl;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}	
	public LocalDateTime getApplicationDate() {
		return applicationDate;
	}
	public void setApplicationDate(LocalDateTime applicationDate) {
		this.applicationDate = applicationDate;
	}
	
	//setting variables from dto to object user
	public Job toJob() {
		Job job = new Job();
		job.setKeywords(keywords);
		job.setRole(role);
		job.setCompany(company);
		job.setJobUrl(jobUrl);
		
		//setting a default initial status for new job application (applied)
		job.setStatus(firstStatus);
		job.setUserEmail(userEmail);
		
		//setting application date attribute automatically by assigning to LocalDateTime instance.
		LocalDateTime date = LocalDateTime.now();  
		job.setApplicationDate(date);
		return job;
	}
	
	
}
