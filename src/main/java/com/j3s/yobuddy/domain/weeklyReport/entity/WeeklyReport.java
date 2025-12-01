package com.j3s.yobuddy.domain.weeklyReport.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "weekly_reports")
public class WeeklyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_report_id")
    private Long weeklyReportId;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Lob
    private String accomplishments;

    @Lob
    private String challenges;

    @Lob
    private String learnings;

    @Lob
    @Column(name = "mentor_feedback")
    private String mentorFeedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeeklyReportStatus status;

    public enum WeeklyReportStatus {
        DRAFT,
        SUBMITTED,
        REVIEWED,
        OVERDUE,
        FEEDBACK_OVERDUE
    }

    @Column(name = "mentor_id")
    private Long mentorId;

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = WeeklyReportStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private WeeklyReport(
        Integer weekNumber,
        LocalDate startDate,
        LocalDate endDate,
        String accomplishments,
        String challenges,
        String learnings,
        String mentorFeedback,
        WeeklyReportStatus status,
        Long mentorId,
        Long menteeId
    ) {
        this.weekNumber = weekNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.accomplishments = accomplishments;
        this.challenges = challenges;
        this.learnings = learnings;
        this.mentorFeedback = mentorFeedback;
        this.status = status;
        this.mentorId = mentorId;
        this.menteeId = menteeId;
    }

    public void updateContent(String accomplishments,
        String challenges,
        String learnings,
        WeeklyReportStatus status) {
        this.accomplishments = accomplishments;
        this.challenges = challenges;
        this.learnings = learnings;
        this.status = status;
    }

    public void updateMentorFeedback(String mentorFeedback, WeeklyReportStatus status) {
        this.mentorFeedback = mentorFeedback;
        if (status != null) {
            this.status = status;
        }
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public void markFeedbackOverdue() {
        this.status = WeeklyReportStatus.FEEDBACK_OVERDUE;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(WeeklyReportStatus status) {
        this.status = status;
    }
}
