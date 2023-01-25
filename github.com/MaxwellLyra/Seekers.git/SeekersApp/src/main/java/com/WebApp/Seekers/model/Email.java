package com.WebApp.Seekers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//first object.class to encapsulate response from EVA API email validator
@JsonIgnoreProperties(ignoreUnknown = true)
public class Email {
	
	//check status of response successful or not
	private String status;
	
	//second object o handle nested json object
	public Data data;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	//to string default method
	@Override
	public String toString() {
		return "Email [status=" + status + ", data=" + data + "]";
	}

	

	
	
}
