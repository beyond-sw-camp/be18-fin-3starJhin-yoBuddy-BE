package com.j3s.yobuddy.domain.notification.service;

import com.j3s.yobuddy.domain.notification.dto.NotificationPayload;
import java.util.List;

public interface NotificationQueryService {

    List<NotificationPayload> getRecentNotifications(Long userId);

    void markNotificationAsRead(Long notificationId, Long userId);

    void deleteNotification(Long notificationId, Long userId);
}