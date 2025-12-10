package com.j3s.yobuddy.domain.notification.listener;

import com.j3s.yobuddy.common.mail.EmailService;
import com.j3s.yobuddy.domain.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListenerEmail {

    private final EmailService email;

    @Async
    @EventListener
    public void handle(NotificationEvent e) {

        try {
            email.send(
                e.getUser().getEmail(),
                e.getTitle(),
                e.getMessage()
            );
            log.info("Email sent to user {}", e.getUser().getUserId());

        } catch (Exception ex) {
            log.error("Email sending failed for user {}. reason={}",
                e.getUser().getUserId(), ex.getMessage(), ex);
        }
    }
}

