package com.lms.dto;

import java.time.LocalDateTime;

public class AssignmentResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String fileType;

    public AssignmentResponse() {
    }

    public AssignmentResponse(Long id, String title, String description, LocalDateTime deadline, String fileType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.fileType = fileType;
    }

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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
