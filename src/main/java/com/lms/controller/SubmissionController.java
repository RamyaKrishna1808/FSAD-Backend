package com.lms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.lms.dto.ApiResponse;
import com.lms.model.Submission;
import com.lms.service.SubmissionService;

@RestController
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // ✅ SUBMIT ASSIGNMENT
    @PostMapping(value = "/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Submission>> submitAssignment(
            @RequestParam("studentId") Long studentId,
            @RequestParam("assignmentId") Long assignmentId,
            @RequestParam("file") MultipartFile file) {

        System.out.println("🔥 API HIT: POST /api/submissions");
        System.out.println("👉 studentId: " + studentId);
        System.out.println("👉 assignmentId: " + assignmentId);
        System.out.println("👉 file: " + file.getOriginalFilename());

        try {
            Submission saved = submissionService.submitAssignment(studentId, assignmentId, file);

            return ResponseEntity.ok(
                    ApiResponse.success("Submission uploaded successfully", saved)
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Submission failed: " + e.getMessage())
            );
        }
    }

    // ✅ GET SUBMISSIONS
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<List<Submission>>> getSubmissions(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long assignmentId) {

        System.out.println("🔥 API HIT: GET /api/submissions");
        System.out.println("👉 studentId: " + studentId);
        System.out.println("👉 assignmentId: " + assignmentId);

        List<Submission> submissions = submissionService.getSubmissions(studentId, assignmentId);

        System.out.println("👉 Returning " + submissions.size() + " submissions");

        return ResponseEntity.ok(
                ApiResponse.success("Submissions fetched successfully", submissions)
        );
    }

    // ✅ GRADE SUBMISSION (FINAL CORRECT VERSION)
    @PatchMapping("/submissions/{id}/grade")
    public ResponseEntity<ApiResponse<Submission>> gradeSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        System.out.println("🔥 API HIT: PATCH /api/submissions/" + id + "/grade");

        try {
            Integer marks = (Integer) body.get("marks");
            String feedback = (String) body.get("feedback");

            System.out.println("👉 marks: " + marks);
            System.out.println("👉 feedback: " + feedback);

            Submission updated = submissionService.gradeSubmission(id, marks, feedback);

            return ResponseEntity.ok(
                    ApiResponse.success("Graded successfully", updated)
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Grading failed: " + e.getMessage())
            );
        }
    }

@GetMapping("/students/{studentId}/results")
public ResponseEntity<ApiResponse<List<Submission>>> getStudentResults(
        @PathVariable Long studentId) {

    System.out.println("🔥 API HIT: GET /api/students/" + studentId + "/results");

    List<Submission> results = submissionService.getGradedSubmissionsByStudent(studentId);

    return ResponseEntity.ok(
            ApiResponse.success("Results fetched successfully", results)
    );
}
}