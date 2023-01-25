package com.WebApp.Seekers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.WebApp.Seekers.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository <Review, Long> {

	//method for reviews
	List<Review> findByCompany(String company);
	
}
