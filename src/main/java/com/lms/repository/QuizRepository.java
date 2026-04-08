package com.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.model.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
