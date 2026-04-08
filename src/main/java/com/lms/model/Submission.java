package com.lms.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "submission")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long assignmentId;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // 🔥 NEW FIELD: MARKS
    @Column
    private Integer marks;

    // 🔥 NEW FIELD: FEEDBACK
    @Column(length = 1000)
    private String feedback;

    public Submission() {
    }

    public Submission(Long id, Long studentId, Long assignmentId, String fileUrl, LocalDateTime submittedAt) {
        this.id = id;
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.fileUrl = fileUrl;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Integer getMarks() {
        return marks;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}