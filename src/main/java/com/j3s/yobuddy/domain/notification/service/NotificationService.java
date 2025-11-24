package com.j3s.yobuddy.domain.notification.service;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.event.NotificationEvent;
import com.j3s.yobuddy.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ApplicationEventPublisher publisher;

    public void notify(
        User user, NotificationType type, String title, String message
    ) {
        publisher.publishEvent(
            new NotificationEvent(user, type, title, message)
        );
    }
}
