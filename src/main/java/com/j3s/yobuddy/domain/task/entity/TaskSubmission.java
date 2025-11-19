//package com.j3s.yobuddy.domain.task.entity;
//
//import com.j3s.yobuddy.domain.user.entity.User;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@Table(name = "task_submissions")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class TaskSubmission {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "submission_id")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_task_id")
//    private UserTask userTask;
//
//    @Column(name = "submitted_at", nullable = false)
//    private LocalDateTime submittedAt;
//
//    @Column(name = "grade")
//    private Integer grade;
//
//    @Column(name = "feedback")
//    private String feedback;
//
//    @Column(name = "evaluator")
//    private String evaluator;   // 명세서에 문자열로 명시됨
//
//    @Enumerated(EnumType.STRING)
//    private TaskSubmissionStatus status;  // SUBMITTED, RESUBMITTED, GRADED
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TaskFile> files = new ArrayList<>();
//
//    @Builder
//    public TaskSubmission(UserTask userTask,
//        LocalDateTime submittedAt,
//        Integer grade,
//        String feedback,
//        String evaluator,
//        TaskSubmissionStatus status) {
//        this.userTask = userTask;
//        this.submittedAt = submittedAt;
//        this.grade = grade;
//        this.feedback = feedback;
//        this.evaluator = evaluator;
//        this.status = status;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public void updateGrade(Integer grade, String feedback, String evaluator) {
//        this.grade = grade;
//        this.feedback = feedback;
//        this.evaluator = evaluator;
//        this.status = TaskSubmissionStatus.GRADED;
//        this.updatedAt = LocalDateTime.now();
//    }
//}
