package com.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.model.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findBySubmissionId(Long submissionId);
}
