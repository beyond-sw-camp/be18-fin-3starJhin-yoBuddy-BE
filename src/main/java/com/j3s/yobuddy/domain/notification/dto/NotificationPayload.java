package com.j3s.yobuddy.domain.notification.dto;

import com.j3s.yobuddy.domain.notification.entity.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationPayload {

    private Long id;
    private String type;
    private String title;
    private String message;
    private String createdAt;
    private Boolean isRead;

    public NotificationPayload(Notification n) {
        this.id = n.getNotificationId();
        this.type = n.getType().name().toLowerCase();;
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.createdAt = n.getCreatedAt().toString();
        this.isRead = n.getIsRead();
    }
}
