package com.mayura.library_management_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public String getHello(){
        return "Hello World";
    }

//    @PostMapping("/login")
//    public String login(){
//        return "usr login";
//    }

}

