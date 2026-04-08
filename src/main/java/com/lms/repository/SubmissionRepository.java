package com.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.model.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByStudentId(Long studentId);

    List<Submission> findByAssignmentId(Long assignmentId);
}