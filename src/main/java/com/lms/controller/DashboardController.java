package com.lms.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.ApiResponse;

@RestController
@RequestMapping("/api")

public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping("/dashboard/teacher")
    public ResponseEntity<ApiResponse<Map<String, String>>> getTeacherDashboard() {
        log.info("Received GET request to /api/dashboard/teacher");
        return ResponseEntity.ok(ApiResponse.success("Teacher dashboard available", Map.of("status", "ok")));
    }

    @PostMapping("/dashboard/teacher")
    public ResponseEntity<ApiResponse<Map<String, String>>> postTeacherDashboard() {
        log.info("Received POST request to /api/dashboard/teacher");
        return ResponseEntity.ok(ApiResponse.success("Teacher dashboard endpoint available", Map.of("status", "ok")));
    }
}
