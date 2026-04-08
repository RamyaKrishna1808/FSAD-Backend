package com.lms.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lms.dto.AssignmentRequest;
import com.lms.model.Assignment;
import com.lms.model.Role;
import com.lms.model.User;
import com.lms.repository.AssignmentRepository;
import com.lms.repository.UserRepository;

@Service
public class AssignmentService {

    private static final Set<String> SUPPORTED_FILE_TYPES = Set.of("pdf", "jpg", "jpeg", "png");

    private final AssignmentRepository assignmentRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, UserService userService, NotificationService notificationService, UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public Assignment createAssignment(AssignmentRequest request) {
        System.out.println("Received request: " + request);
        validateAssignmentRequest(request);
        User teacher = userService.getUser(request.getTeacherId());
        if (teacher.getRole() != Role.ADMIN_TEACHER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only teacher/admin users can create assignments");
        }

        validateAllowedFileTypes(request.getAllowedFileTypes());

        Assignment assignment = new Assignment(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getDeadline(),
                request.getAllowedFileTypes(),
                request.getTeacherId());

        Assignment saved = assignmentRepository.save(assignment);
        
        // Create notification for students
        try {
            List<User> students = userRepository.findByRole(Role.STUDENT);
            for (User student : students) {
                notificationService.createNotification(student.getId(), "New assignment created: " + request.getTitle());
            }
        } catch (Exception e) {
            System.err.println("Failed to send assignment notification: " + e.getMessage());
        }

        return saved;
    }

    public List<Assignment> getAssignments() {
        return assignmentRepository.findAll();
    }

    public Assignment getAssignment(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment id is required");
        }
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
    }

    private void validateAssignmentRequest(AssignmentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment request is required");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment title is required");
        }
        if (request.getDeadline() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment deadline is required");
        }
        if (request.getTeacherId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher id is required");
        }
    }

    private void validateAllowedFileTypes(List<String> allowedFileTypes) {
        if (allowedFileTypes == null || allowedFileTypes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one file type is required");
        }

        for (String type : allowedFileTypes) {
            String normalized = type.trim().toLowerCase();
            if (!SUPPORTED_FILE_TYPES.contains(normalized)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Allowed file types are pdf, jpg, jpeg, png");
            }
        }
    }
}
