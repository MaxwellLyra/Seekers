package com.WebApp.Seekers.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.exceptions.TemplateInputException;

import com.WebApp.Seekers.dto.RequestNewUser;
import com.WebApp.Seekers.model.Email;
import com.WebApp.Seekers.model.Job;
import com.WebApp.Seekers.model.User;
import com.WebApp.Seekers.repository.JobRepository;
import com.WebApp.Seekers.repository.UserRepository;

@Controller
@RequestMapping("account")
public class AccountController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JobRepository jobRepository;

	@GetMapping()
	public String goToAccountPage(HttpSession session, Model model) {
		// catch block in case user tries to access this page without being logged in
		// (catching for null pointer - server 500 error - and index out of bounds)
		try {

			// Building url to Avatar Api with base url, username (generates a random image
			// and end of url with custom scale to fit in bootstrap card
			List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());
			User user = users.get(0);

			// creating variables to add to url and setting query parameters as name and
			// scale/size
			String url = "https://avatars.dicebear.com/api/croodles-neutral/";
			String userNameAvatar = session.getAttribute("username").toString();
			String urlClosing = ".svg?scale=100";
			String srcAvatar = url + userNameAvatar + urlClosing;

			// adding image url to model
			model.addAttribute("srcAvatar", srcAvatar);
			model.addAttribute("user", user);
		} catch (IndexOutOfBoundsException IOBE) {
			return "/login/loginFail";
		} catch (NullPointerException NPE) {
			return "/login/loginFail";
		}
		return "account";
	}

	@GetMapping("updateAccountUsername")
	public String goToAccountDetailsUsernamePage(HttpSession session, Model model) {
		// fetching user whose email match the sessions' attribute
		List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());

		User user = users.get(0);
		model.addAttribute("user", user);

		// returning view with users data being displayed
		return "update/accountDetailsUsername";

	}

	@PostMapping("updateUsername")
	public String updateAccountUsername(HttpSession session, Model model,
			@RequestParam(value = "newUsername", required = false) String newUsername) {
		// finding user in database by sessions attribute
		List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());
		User user = users.get(0);

		// if the user just press 'save' without filling any information, in order to
		// don't get an error by posting empty data i'm setting his current username
		// back
		if (newUsername == "") {
			users.get(0).setUsername(user.getUsername());
			return "redirect:/account";
		}

		// if he changes the nickname the request parameter new name will be used to set
		// new username
		users.get(0).setUsername(newUsername);
		userRepository.save(user);

		// updating the session attributes as the uses makes any change
		session.removeAttribute("username");
		session.setAttribute("username", newUsername);

		return "redirect:/account";

	}

	@GetMapping("updateAccountEmail")
	public String goToAccountDetailsEmailPage(HttpSession session, Model model) {

		// finding user in database and adding to model to be displayed
		List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());
		User user = users.get(0);
		model.addAttribute("user", user);

		return "update/accountDetailsEmail";

	}

	@PostMapping("updateEmail")
	public String updateEmail(HttpSession session, Model model, RestTemplate restTemplate,
			@RequestParam(value = "newEmail", required = false) String newEmail) {
		List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());
		User user = users.get(0);

		// if the user just press 'save' without filling any information, in order to
		// don't get an error by posting empty data i'm setting his current username
		// back
		if (newEmail == "") {
			users.get(0).setEmail(user.getEmail());
			return "redirect:/account";
		}

		// using the "newEmail" parameter to check if this email already exists in the 
		// repository (to avoid more than one account linked to the same email)
		List<User> usersAlreadyExist = userRepository.findUserByEmail(newEmail);

		// if there's any match, error message is added to model and another view is
		// returned informing user this email can't be used as it already exists in
		// database
		if (!usersAlreadyExist.isEmpty() && newEmail.equals(usersAlreadyExist.get(0).getEmail())) {
			String emailError = "This email is already registered!";
			model.addAttribute("emailError", emailError);

			return "update/accountDetailsEmailNotValid";
		}

		// try and catch block to handle empty failed api calls and any Template Input
		// Exception
		try {

			// EMAIL API

			// base url to call API assigned to variable "url" and getting email from the
			// object user and assigning to "userEmail"
			String url = "https://api.eva.pingutil.com/email?email=";
			String userEmail = newEmail;

			// creating object "emails" and calling API using restTemplate
			Email emails = restTemplate.getForObject(url + userEmail, Email.class);

			// checking attributes "deliverable" and "spam" from api response saved in
			// object Email, if deliverable is false OR spam is true
			// returning view "registrationFailed" so that user knows that this email isn't
			// valid
			if (emails.getData().isDeliverable() == false || emails.getData().isSpam() == true) {
				String emailError = "The email used is not valid!";
				model.addAttribute("emailError", emailError);

				return "update/accountDetailsEmailNotValid";
			}

		} catch (TemplateInputException TIE) {
			return "redirect:/accountDetailsEmail";
		}

		// if the new email is available than job repository is accessed and all the
		// jobs linked to used have their userEmail updated
		List<Job> jobs = jobRepository.findByUserEmail(session.getAttribute("email").toString());
		for (int i = 0; i < jobs.size(); i++) {
			Job updatedJob = jobs.get(i);
			updatedJob.setUserEmail(newEmail);
			jobRepository.save(updatedJob);
		}

		users.get(0).setEmail(newEmail);

		userRepository.save(user);

		// updating user session attributes
		session.removeAttribute("email");
		session.setAttribute("email", newEmail);

		return "redirect:/account";

	}

	@GetMapping("updateAccountPassword")
	public String goToAccountDetailsPasswordPage(HttpSession session, Model model) {

		// fetching user data by their email and adding to model to be displayed
		List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());

		User user = users.get(0);
		model.addAttribute("user", user);

		return "update/accountDetailsPassword";

	}

	@PostMapping("updatePassword")
	public String updatePassword(HttpSession session, Model model, RequestNewUser requestUser,
			@RequestParam(value = "oldPassword", required = false) String oldPassword,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestParam(value = "confirmNewPassword", required = false) String confirmNewPassword) {
		
		//find user by email method
		List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());
		User user = users.get(0);

		//using oldPassowrd parameter to check if user input matches the password save in database and using encoder (customized) method before evaluation
		String oldPassEncoded = requestUser.passwordEncoder(oldPassword);
		
		//if password does not match error message is added to model and same view is returned informing user about error
		if (!oldPassEncoded.equals(user.getPassword())) {
			String passwordError1 = "Invalid password";
			model.addAttribute("passwordError1", passwordError1);

			return "update/accountDetailsPassword";
		}

		//if the newPassword and confirmNewPassowrd don't match, a different message is added to model and same view returned to inform user about mismatch
		if (!newPassword.equals(confirmNewPassword)) {
			String passwordError2 = "Password mismatch";
			model.addAttribute("passwordError2", passwordError2);

			return "update/accountDetailsPassword";
		}

		//if new password parameters are empty the same view is returned and nothing is done
		if (newPassword == "" || confirmNewPassword == "") {
			return "update/accountDetailsPassword";
		}

		//if all requirements are met, new password is encoded and saved in database
		String confirmNewPassEncoded = requestUser.passwordEncoder(confirmNewPassword);
		user.setPassword(confirmNewPassEncoded);
		userRepository.save(user);

		return "redirect:/account";

	}

	//to avoid the possibility of user pressing delete account by mistake, instead of deleting account directly, another page with warning is returned to 
	//confirm if user wants to proceed with account deletion
	@GetMapping("delete")
	public String goToDeleteAccountPage(HttpSession session, Model model) {
		List<User> users = userRepository.findUserByEmail(session.getAttribute("email").toString());

		User user = users.get(0);
		model.addAttribute("user", user);

		return "update/deleteAccount";

	}

	@GetMapping("deleteAccount")
	public String deleteAccount(@RequestParam(value = "param1", required = false) long userId, HttpSession session) {
		
		//if user confirms deletion, the default method deleById is used and userId is used as parameter from get request sent from delete button
		List<User> users = userRepository.deleteById(userId);

		//clearing session attributes and redirecting user to index (welcome) page
		session.removeAttribute("email");
		session.removeAttribute("username");
		session.removeAttribute("role");
		return "redirect:/";

	}

}
