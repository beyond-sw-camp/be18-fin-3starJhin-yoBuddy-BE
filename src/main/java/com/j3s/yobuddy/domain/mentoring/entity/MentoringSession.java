package com.j3s.yobuddy.domain.mentoring.entity;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentoring_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MentoringSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentoring_session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_user_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_user_id", nullable = false)
    private User mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private OnboardingProgram program;

    private LocalDateTime scheduledAt;

    private Boolean attended;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public void softDelete() {
        this.deleted = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.attended = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(LocalDateTime scheduledAt, Boolean attended, String feedback) {
        if (scheduledAt != null) this.scheduledAt = scheduledAt;
        if (attended != null) this.attended = attended;
        if (feedback != null) this.feedback = feedback;
    }
}
