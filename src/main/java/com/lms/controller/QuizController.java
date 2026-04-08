package com.lms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.ApiResponse;
import com.lms.model.Quiz;
import com.lms.model.QuizSubmission;
import com.lms.service.QuizService;



/**
 * REST controller for managing quizzes.
 */
@RestController
@RequestMapping("/api")

public class QuizController {

    private final QuizService quizService;

    /**
     * Constructor for QuizController.
     * @param quizService the quiz service
     */
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Creates a new quiz.
     * @param quiz the quiz to create
     * @return the response entity with the created quiz
     */
    @PostMapping("/quizzes")
    public ResponseEntity<ApiResponse<Quiz>> createQuiz(@RequestBody Quiz quiz) {
        Quiz saved = quizService.createQuiz(quiz);
        return ResponseEntity.ok(ApiResponse.success(null, saved));
    }

    /**
     * Gets all quizzes.
     * @return the response entity with the list of quizzes
     */
    @GetMapping("/quizzes")
    public ResponseEntity<ApiResponse<List<Quiz>>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(ApiResponse.success(null, quizzes));
    }

    /**
     * Submits a quiz answer.
     * @param submission the quiz submission
     * @return the response entity with the quiz submission result
     */
    @PostMapping("/quiz/submit")
    public ResponseEntity<ApiResponse<QuizSubmission>> submitQuiz(@RequestBody QuizSubmission submission) {
        QuizSubmission saved = quizService.submitQuiz(submission);
        return ResponseEntity.ok(ApiResponse.success(null, saved));
    }
}
