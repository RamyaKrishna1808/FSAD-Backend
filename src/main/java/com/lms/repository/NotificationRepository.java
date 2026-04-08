package com.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndReadFalse(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}
