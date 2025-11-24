package com.j3s.yobuddy.domain.task.entity;

import com.j3s.yobuddy.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_tasks")
public class UserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_task_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserTaskStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    private Integer grade;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_task_id", nullable = false)
    private ProgramTask programTask;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Builder
    private UserTask(User user, ProgramTask programTask) {
        this.user = user;
        this.programTask = programTask;
        this.status = UserTaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 💡 도메인 메서드 (행위)
    public void submit(String feedback) {
        this.status = UserTaskStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        this.feedback = feedback;
        this.grade = null;
    }

    public void grade(Integer grade, String feedback) {
        this.grade = grade;
        this.feedback = feedback;
        this.status = UserTaskStatus.GRADED;
        this.updatedAt = LocalDateTime.now();
    }

}
