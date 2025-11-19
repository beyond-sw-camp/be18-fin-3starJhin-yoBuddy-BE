package com.j3s.yobuddy.domain.task.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "onboarding_tasks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer points;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Builder
    public OnboardingTask(String title, String description, Integer points) {
        this.title = title;
        this.description = description;
        this.points = points;
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }


}
