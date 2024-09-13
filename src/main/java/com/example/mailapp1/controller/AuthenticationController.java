package com.example.mailapp1.controller;

import com.example.mailapp1.entity.AuthenticationResponse;
import com.example.mailapp1.entity.User;
import com.example.mailapp1.service.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register( @Valid @RequestBody User request) {
        logger.info("Received register request: {}", request);
        try {
            AuthenticationResponse response = service.register(request);
            logger.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during registration: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login( @RequestBody User request) {
        logger.info("Received login request: {}", request);
        try {
            AuthenticationResponse response = service.authenticate(request);
            logger.info("User authenticated successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during authentication: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

