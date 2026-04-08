package com.lms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.GradeRequest;
import com.lms.model.Grade;
import com.lms.service.GradeService;

/**
 * REST controller for grading operations.
 */
@RestController
@RequestMapping("/api")

public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping("/grade")
    public ResponseEntity<Grade> gradeSubmission(@RequestBody GradeRequest request) {
        return ResponseEntity.ok(gradeService.gradeSubmission(request));
    }

    @GetMapping("/results")
    public ResponseEntity<List<Grade>> getResults(@RequestParam(required = false) Long studentId) {
        return ResponseEntity.ok(gradeService.getResults(studentId));
    }
}
