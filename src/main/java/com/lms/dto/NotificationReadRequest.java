package com.lms.dto;

import lombok.Data;

@Data
public class NotificationReadRequest {
    private Long notificationId;
    private Long userId;

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
