package com.j3s.yobuddy.domain.training.entity;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.training.dto.request.ProgramTrainingUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_trainings",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"program_id", "training_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProgramTraining {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_training_id")
    private Long programTrainingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private OnboardingProgram program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    public ProgramTraining update(ProgramTrainingUpdateRequest req) {
        return ProgramTraining.builder()
            .programTrainingId(this.programTrainingId)  // PK 유지
            .program(this.program)                      // FK 유지
            .training(this.training)                    // FK 유지
            .scheduledAt(req.getScheduledAt() != null ? req.getScheduledAt() : this.scheduledAt)
            .startDate(req.getStartDate() != null ? req.getStartDate() : this.startDate)
            .endDate(req.getEndDate() != null ? req.getEndDate() : this.endDate)
            .assignedAt(this.assignedAt)
            .build();
    }
}