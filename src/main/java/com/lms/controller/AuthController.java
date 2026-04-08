package com.lms.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.ApiResponse;
import com.lms.dto.ForgotPasswordRequest;
import com.lms.dto.LoginRequest;
import com.lms.dto.RegisterRequest;
import com.lms.dto.ResetPasswordRequest;
import com.lms.dto.UserResponse;
import com.lms.service.UserService;




/**
 * REST controller for authentication operations.
 */
@RestController
@RequestMapping("/api")

public class AuthController {

    private final UserService userService;

    /**
     * Constructor for AuthController.
     * @param userService the user service
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     * @param request the register request
     * @return the response entity with the user response
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * Logs in a user.
     * @param request the login request
     * @return the response entity with the user response
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     * Sends forgot password request.
     * @param request the forgot password request
     * @return the response entity
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.forgotPassword(request);
    }

    /**
     * Resets user password.
     * @param request the reset password request
     * @return the response entity with the user response
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<UserResponse>> resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }
}
