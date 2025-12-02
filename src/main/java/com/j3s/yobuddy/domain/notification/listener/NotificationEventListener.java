package com.j3s.yobuddy.domain.notification.listener;

import com.j3s.yobuddy.common.mail.EmailService;
import com.j3s.yobuddy.domain.notification.dto.NotificationPayload;
import com.j3s.yobuddy.domain.notification.entity.Notification;
import com.j3s.yobuddy.domain.notification.event.NotificationEvent;
import com.j3s.yobuddy.domain.notification.repository.NotificationRepository;
import com.j3s.yobuddy.domain.notification.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository repo;
    private final EmailService email;
    private final SseEmitterManager sse;

    @Async
    @EventListener
    public void handle(NotificationEvent e) {

        log.info("NotificationEvent => user {}, type {}", e.getUser().getUserId(), e.getType());

        // 1) 알림 저장 (단 1회)
        Notification n = repo.save(
            Notification.builder()
                .user(e.getUser())
                .type(e.getType())
                .title(e.getTitle())
                .message(e.getMessage())
                .build()
        );

        // 2) 이메일은 비동기 전송, 실패해도 SSE 영향 없음
        try {
            email.send(
                e.getUser().getEmail(),
                e.getTitle(),
                e.getMessage()
            );
        } catch (Exception ex) {
            log.error("Email sending failed for user {}. reason={}",
                e.getUser().getUserId(), ex.getMessage(), ex);
        }

        // 3) SSE 전송 시도
        sse.send(e.getUser().getUserId(), new NotificationPayload(n));
    }
}
