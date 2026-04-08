package com.lms.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lms.dto.GradeRequest;
import com.lms.model.Assignment;
import com.lms.model.Grade;
import com.lms.model.Submission;
import com.lms.model.User;
import com.lms.repository.GradeRepository;
import com.lms.repository.SubmissionRepository;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private final AssignmentService assignmentService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public GradeService(
            GradeRepository gradeRepository,
            SubmissionRepository submissionRepository,
            SubmissionService submissionService,
            AssignmentService assignmentService,
            UserService userService,
            NotificationService notificationService,
            EmailService emailService) {
        this.gradeRepository = gradeRepository;
        this.submissionRepository = submissionRepository;
        this.submissionService = submissionService;
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    public Grade gradeSubmission(GradeRequest request) {
        validateGradeRequest(request);
        Submission submission = submissionService.getSubmission(request.getSubmissionId());
        Assignment assignment = assignmentService.getAssignment(submission.getAssignmentId());
        User student = userService.getUser(submission.getStudentId());

        Grade grade = gradeRepository.findBySubmissionId(request.getSubmissionId())
                .orElseGet(Grade::new);
        grade.setSubmissionId(request.getSubmissionId());
        grade.setMarks(request.getMarks());
        grade.setFeedback(request.getFeedback());

        Grade savedGrade = gradeRepository.save(grade);
        String message = "Your assignment '" + assignment.getTitle() + "' has been graded. Marks: " + request.getMarks();
        notificationService.createNotification(student.getId(), message);
        emailService.sendEmail(student.getEmail(), "Assignment graded", message + "\nFeedback: " + request.getFeedback());

        return savedGrade;
    }

    public List<Grade> getResults(Long studentId) {
        if (studentId == null) {
            return gradeRepository.findAll();
        }

        List<Long> submissionIds = submissionRepository.findByStudentId(studentId)
                .stream()
                .map(Submission::getId)
                .toList();

        return gradeRepository.findAll()
                .stream()
                .filter(grade -> submissionIds.contains(grade.getSubmissionId()))
                .toList();
    }

    private void validateGradeRequest(GradeRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grade request is required");
        }
        if (request.getSubmissionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submission id is required");
        }
        if (request.getMarks() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marks are required");
        }
        if (request.getMarks() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marks cannot be negative");
        }
    }
}
