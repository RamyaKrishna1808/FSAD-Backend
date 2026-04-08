package com.lms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.ApiResponse;
import com.lms.dto.NotificationReadRequest;
import com.lms.model.Notification;
import com.lms.service.NotificationService;

@RestController
@RequestMapping("/api")

public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "studentId", required = false) Long studentId) {
        Long effectiveUserId = userId != null ? userId : studentId;
        if (effectiveUserId == null) {
            return ResponseEntity.ok(ApiResponse.success(null, java.util.List.of()));
        }
        List<Notification> records = notificationService.getNotifications(effectiveUserId);
        return ResponseEntity.ok(ApiResponse.success(null, records));
    }

    @PostMapping("/notifications/read")
    public ResponseEntity<ApiResponse<Notification>> markNotificationAsRead(@RequestBody NotificationReadRequest request) {
        Notification updated = notificationService.markAsRead(request);
        return ResponseEntity.ok(ApiResponse.success(null, updated));
    }
}

