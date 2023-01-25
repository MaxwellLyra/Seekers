package com.WebApp.Seekers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.WebApp.Seekers.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

	//method for analytics and home
	List<Job> findByUserEmail(String userEmail);
	
	//methods for home 
	List<Job> findByStatus(String status);
	List<Job> findById(long id);
	List<Job> deleteById(long Id);
	//display list of jobs of specific users
	List <Job> findByStatusAndUserEmail(String status, String userEmail);
	
	//method for analytics
	long countByStatus(String status);
	
	//method for home as alternative in case dropdown is replaced by another kind of company list/display
	//List<Job> findByCompany(String company);
	
	
}