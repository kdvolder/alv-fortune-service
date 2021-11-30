package com.github.kdvolder.fortune.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/session")
    public String getSession(HttpServletRequest req) {
    	return req.getSession().getId();
    }
}