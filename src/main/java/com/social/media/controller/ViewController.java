package com.social.media.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {
	
	@GetMapping({"/", "/feed"})
    public String feed() {
        return "feed";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/post/{id}")
    public String postView(@PathVariable Long id) {
        return "post";
    }

}
