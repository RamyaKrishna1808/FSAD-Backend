package com.lms.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "assignment")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime deadline;

    // ✅ IMPORTANT FIX: ensure proper mapping + fetching
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "assignment_allowed_file_types",
        joinColumns = @JoinColumn(name = "assignment_id")
    )
    @Column(name = "file_type", nullable = false)
    private List<String> allowedFileTypes;

    @Column(nullable = false)
    private Long teacherId;

    // ✅ Default constructor
    public Assignment() {}

    // ✅ All args constructor
    public Assignment(Long id, String title, String description,
                      LocalDateTime deadline, List<String> allowedFileTypes,
                      Long teacherId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.allowedFileTypes = allowedFileTypes;
        this.teacherId = teacherId;
    }

    // ✅ GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(List<String> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}