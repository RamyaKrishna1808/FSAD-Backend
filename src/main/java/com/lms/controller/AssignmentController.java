package com.lms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.ApiResponse;
import com.lms.dto.AssignmentRequest;
import com.lms.dto.AssignmentResponse;
import com.lms.model.Assignment;
import com.lms.service.AssignmentService;

/**
 * REST controller for managing assignments.
 */
@RestController
@RequestMapping("/api")

public class AssignmentController {

    private final AssignmentService assignmentService;

    /**
     * Constructor for AssignmentController.
     * @param assignmentService the assignment service
     */
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * Creates a new assignment.
     * @param request the assignment request
     * @return the response entity with the created assignment
     */
    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<Assignment>> createAssignment(@RequestBody AssignmentRequest request) {
        System.out.println("API HIT: POST /api/assignments");
        Assignment assignment = assignmentService.createAssignment(request);
        return ResponseEntity.ok(ApiResponse.success("Assignment created successfully", assignment));
    }

    /**
     * Gets all assignments.
     * @return the response entity with the list of assignments
     */
    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignments() {
        System.out.println("API HIT: GET /api/assignments");
        List<AssignmentResponse> assignments = assignmentService.getAssignments().stream()
                .map(assignment -> new AssignmentResponse(
                        assignment.getId(),
                        assignment.getTitle(),
                        assignment.getDescription(),
                        assignment.getDeadline(),
                        assignment.getAllowedFileTypes() == null || assignment.getAllowedFileTypes().isEmpty()
                                ? null
                                : assignment.getAllowedFileTypes().get(0)
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(assignments);
    }
}
