package com.j3s.yobuddy.domain.task.entity;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "program_tasks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_program_task",
            columnNames = {"program_id", "task_id"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProgramTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "program_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_ProgramTasks_Program")
    )
    private OnboardingProgram onboardingProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "task_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_ProgramTasks_Task")
    )
    private OnboardingTask onboardingTask;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Builder
    public ProgramTask(
        OnboardingProgram onboardingProgram,
        OnboardingTask onboardingTask,
        LocalDateTime dueDate
    ) {
        this.onboardingProgram = onboardingProgram;
        this.onboardingTask = onboardingTask;
        this.dueDate = dueDate;
        this.assignedAt = LocalDateTime.now();
    }

    public void updateDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}