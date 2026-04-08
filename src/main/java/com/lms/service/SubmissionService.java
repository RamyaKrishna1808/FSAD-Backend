package com.lms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.lms.model.Assignment;
import com.lms.model.Submission;
import com.lms.repository.SubmissionRepository;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentService assignmentService;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;

    public SubmissionService(SubmissionRepository submissionRepository,
                             AssignmentService assignmentService,
                             NotificationService notificationService,
                             FileStorageService fileStorageService) {
        this.submissionRepository = submissionRepository;
        this.assignmentService = assignmentService;
        this.notificationService = notificationService;
        this.fileStorageService = fileStorageService;
    }

    // ✅ SUBMIT ASSIGNMENT
    public Submission submitAssignment(Long studentId, Long assignmentId, MultipartFile file) {

        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student id is required");
        }

        if (assignmentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment id is required");
        }

        Assignment assignment = assignmentService.getAssignment(assignmentId);

        String fileUrl = fileStorageService.storeFile(file, assignment.getAllowedFileTypes());

        Submission submission = new Submission();
        submission.setStudentId(studentId);
        submission.setAssignmentId(assignmentId);
        submission.setFileUrl(fileUrl);
        submission.setSubmittedAt(LocalDateTime.now());

        Submission savedSubmission = submissionRepository.save(submission);
        System.out.println("Submission saved in DB for assignmentId: " + assignmentId);

        try {
            notificationService.createNotification(
                    assignment.getTeacherId(),
                    "New submission received for assignment: " + assignment.getTitle()
            );
        } catch (Exception e) {
            System.err.println("Failed to send submission notification: " + e.getMessage());
        }

        return savedSubmission;
    }

    // ✅ GET SUBMISSIONS
    public List<Submission> getSubmissions(Long studentId, Long assignmentId) {

        if (studentId != null) {
            return submissionRepository.findByStudentId(studentId);
        }

        if (assignmentId != null) {
            return submissionRepository.findByAssignmentId(assignmentId);
        }

        return submissionRepository.findAll();
    }

    // ✅ GET SINGLE SUBMISSION
    public Submission getSubmission(Long id) {

        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submission id is required");
        }

        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));
    }

    // 🔥 NEW: GRADE SUBMISSION (THIS IS THE FIX)
    public Submission gradeSubmission(Long id, Integer marks, String feedback) {

        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submission id is required");
        }

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        submission.setMarks(marks);
        submission.setFeedback(feedback);

        Submission updated = submissionRepository.save(submission);

        System.out.println("✅ Submission graded: ID = " + id + ", Marks = " + marks);

        return updated;
    }


public List<Submission> getGradedSubmissionsByStudent(Long studentId) {

    if (studentId == null) {
        throw new RuntimeException("Student id is required");
    }

    // ✅ ONLY RETURN GRADED
    return submissionRepository.findByStudentId(studentId)
            .stream()
            .filter(s -> s.getMarks() != null)
            .toList();
}
}