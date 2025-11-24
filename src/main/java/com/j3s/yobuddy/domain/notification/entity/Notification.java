package com.j3s.yobuddy.domain.notification.entity;

import com.j3s.yobuddy.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean isRead;
    private Boolean isEmailSent;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.isEmailSent = false;
        // isDeleted 필드는 제거됨
    }

    public void markEmailSent() {
        this.isEmailSent = true;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}

