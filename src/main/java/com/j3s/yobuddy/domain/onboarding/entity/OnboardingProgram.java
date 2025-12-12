package com.j3s.yobuddy.domain.onboarding.entity;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.onboarding.exception.ProgramStatusChangeNotAllowedException;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "onboarding_programs")
public class OnboardingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Long programId;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgramStatus status;

    public enum ProgramStatus {
        UPCOMING, ACTIVE, COMPLETED
    }

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY)
    private List<ProgramEnrollment> enrollments = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = ProgramStatus.UPCOMING;
        }
    }

    public void update(
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        ProgramStatus status
    ) {
        if (name != null && !name.isBlank()) this.name = name;
        if (description != null) this.description = description;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;

        if (status != null && status != this.status) {
            validateStatusChange(status);
            this.status = status;
        }

        this.updatedAt = LocalDateTime.now();
    }

    private void validateStatusChange(ProgramStatus newStatus) {
        if (this.status == ProgramStatus.COMPLETED) {
            throw new ProgramStatusChangeNotAllowedException(this.programId);
        }
    }

    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
