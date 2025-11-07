package com.mayura.library_management_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mayura.library_management_system.Services.UserService;

@Controller
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/registe")
	public String register() {
		return "register";
	}
	
	@PostMapping("/register")
	public String registerUser(@RequestParam String username, String password) {
		userService.registerUser(username, password);
		return "redirect:/login";
		
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/welcome")
	public String welcome() {
		return "welcome";
	}

}
