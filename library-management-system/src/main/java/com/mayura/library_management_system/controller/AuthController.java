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

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // templates/register.html
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        userService.registerUser(username, password);
        return "redirect:/login"; // redirect to login page after registration
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // templates/login.html
    }

    @GetMapping("/welcome")
    public String showWelcomePage() {
        return "welcome"; // templates/welcome.html
    }
}

