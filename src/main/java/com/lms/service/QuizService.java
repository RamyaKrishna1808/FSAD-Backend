package com.lms.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lms.model.Quiz;
import com.lms.model.QuizSubmission;
import com.lms.model.Role;
import com.lms.model.User;
import com.lms.repository.QuizRepository;
import com.lms.repository.QuizSubmissionRepository;
import com.lms.repository.UserRepository;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository, QuizSubmissionRepository quizSubmissionRepository, NotificationService notificationService, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public Quiz createQuiz(Quiz quiz) {
        if (quiz.getQuestion() == null || quiz.getQuestion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question is required");
        }
        if (quiz.getCorrectAnswer() == null || quiz.getCorrectAnswer().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correct answer is required");
        }
        
        Quiz saved = quizRepository.save(quiz);

        try {
            List<User> students = userRepository.findByRole(Role.STUDENT);
            for (User student : students) {
                notificationService.createNotification(student.getId(), "New Quiz Released!");
            }
        } catch (Exception e) {
            System.err.println("Failed to send quiz notifications: " + e.getMessage());
        }

        return saved;
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public QuizSubmission submitQuiz(QuizSubmission submission) {
        if (submission.getQuizId() == null || submission.getStudentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz ID and Student ID are required");
        }
        
        Quiz quiz = quizRepository.findById(submission.getQuizId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        
        boolean isCorrect = quiz.getCorrectAnswer().equalsIgnoreCase(submission.getSelectedAnswer());
        submission.setCorrect(isCorrect);
        
        return quizSubmissionRepository.save(submission);
    }
}
