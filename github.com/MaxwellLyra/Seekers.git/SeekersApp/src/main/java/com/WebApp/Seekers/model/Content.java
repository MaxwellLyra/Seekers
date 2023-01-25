package com.WebApp.Seekers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//content class object for Text Validator API Purgomalum
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {

	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	//method to check if response has any special characters (*) that are used to represent offensive words
	public boolean isOffensive() {
		for(int i = 0; i < result.length(); i++) {
			
			if (result.charAt(i) == '*') {
				return true;
			}
		}
		return false;
	}
	
}
