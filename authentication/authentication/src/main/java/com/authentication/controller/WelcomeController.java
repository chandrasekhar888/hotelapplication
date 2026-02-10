
//---------------------------------
package com.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/welcome")
public class WelcomeController {
    
    @GetMapping("/message")
    public String welcomeMessage() {
        return "Welcome to the Authentication Service!";
    }
}
//---------------------------------