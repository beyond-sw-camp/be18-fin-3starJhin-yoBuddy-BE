package com.j3s.yobuddy.domain.notification.service;

import com.j3s.yobuddy.domain.notification.dto.NotificationPayload;
import com.j3s.yobuddy.domain.notification.entity.Notification;
import com.j3s.yobuddy.domain.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService { // 인터페이스 구현

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPayload> getRecentNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
            .findByUser_UserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
            .map(NotificationPayload::new)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Notification does not belong to user.");
        }

        notification.markAsRead();
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Notification does not belong to user.");
        }

        notificationRepository.deleteById(notificationId);
    }
}