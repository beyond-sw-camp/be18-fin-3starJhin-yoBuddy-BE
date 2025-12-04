package com.j3s.yobuddy.domain.notification.event;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationEvent {
    private final User user;
    private final NotificationType type;
    private final String title;
    private final String message;
}
