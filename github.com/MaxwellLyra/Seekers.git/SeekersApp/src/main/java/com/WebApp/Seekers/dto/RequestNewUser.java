package com.WebApp.Seekers.dto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.validation.constraints.NotBlank;

import com.WebApp.Seekers.model.User;

public class RequestNewUser {

	//not blank annotation to return an error if user don't provide input in form
	@NotBlank
	private String username;
	
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
		
	private String role;
				
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	//setting user attributes from dto
	public User toUser() {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		user.setRole("USER");
		return user;
	}

	//method created to receive a string as parameter and return hashtext
	public String passwordEncoder(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
			}
			return hashtext;
			}
			catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
			}
		
	}
}

