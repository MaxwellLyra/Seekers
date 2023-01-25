package com.WebApp.Seekers.model;

//second object.class to encapsulate response from EVA API email validator
public class Data {

	//attribute to check if email is able to receive correspondence
	private boolean deliverable;
	
	//attribute to check if email is spam
	private boolean spam;
	
	public boolean isDeliverable() {
		return deliverable;
	}
	public void setDeliverable(boolean deliverable) {
		this.deliverable = deliverable;
	}
	public boolean isSpam() {
		return spam;
	}
	public void setSpam(boolean spam) {
		this.spam = spam;
	}
	
	//default to string method
	@Override
	public String toString() {
		return "Data [deliverable=" + deliverable + ", spam=" + spam + "]";
	}
	
	
	
}
