package com.lms.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lms.dto.NotificationReadRequest;
import com.lms.model.Notification;
import com.lms.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Long userId, String message) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification user id is required");
        }
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification message is required");
        }

        Notification notification = new Notification(null, userId, message, false);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

    public Notification markAsRead(NotificationReadRequest request) {
        if (request == null || request.getNotificationId() == null || request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification id and user id are required");
        }

        Notification notification = notificationRepository.findByIdAndUserId(
                        request.getNotificationId(),
                        request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
