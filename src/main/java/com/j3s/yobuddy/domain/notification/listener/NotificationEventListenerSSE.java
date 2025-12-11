package com.j3s.yobuddy.domain.notification.listener;

import com.j3s.yobuddy.domain.notification.dto.NotificationPayload;
import com.j3s.yobuddy.domain.notification.entity.Notification;
import com.j3s.yobuddy.domain.notification.event.NotificationEvent;
import com.j3s.yobuddy.domain.notification.repository.NotificationRepository;
import com.j3s.yobuddy.domain.notification.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListenerSSE {

    private final NotificationRepository repo;
    private final SseEmitterManager sse;

    @EventListener
    public void handle(NotificationEvent e) {

        log.info("SSE Listener => user {}, type {}", e.getUser().getUserId(), e.getType());

        Notification n = repo.save(
            Notification.builder()
                .user(e.getUser())
                .type(e.getType())
                .title(e.getTitle())
                .message(e.getMessage())
                .build()
        );

        sse.send(e.getUser().getUserId(), new NotificationPayload(n));
    }
}

