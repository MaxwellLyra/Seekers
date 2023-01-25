package com.WebApp.Seekers.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WebApp.Seekers.model.User;

public interface UserRepository extends JpaRepository<User, Long>  {

	//method for Login authentication
	List<User> findByEmailAndPassword(String email, String password);
	
	//alternative method to check if email exists
	//boolean findByEmail(String email);
		
	//filter users to analytics
	List<User> findUserByRole(String role);
	//alternative method for clearance
	//boolean findByRole(String role);
	
	//method to perform Search in admin page
	List<User> findUserByEmail(String email);
	
	//method for account to delete specific user
	List<User> deleteById(long Id);
	
	//account NOT WORKING YET
	List<User> deleteByEmail(String email);
	
}
