package com.j3s.yobuddy.domain.task.entity;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Builder
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

    public ProgramTask update(ProgramTaskUpdateRequest req) {
        return ProgramTask.builder()
            .id(this.id)
            .onboardingProgram(this.onboardingProgram)
            .onboardingTask(this.onboardingTask)
            .dueDate(req.getDueDate() != null ? req.getDueDate() : this.dueDate)
            .assignedAt(this.assignedAt)
            .build();
    }

    public void updateDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}