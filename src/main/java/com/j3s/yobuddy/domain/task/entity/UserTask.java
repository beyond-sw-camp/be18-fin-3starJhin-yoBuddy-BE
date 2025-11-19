//package com.j3s.yobuddy.domain.task.entity;
//
//import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
//import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
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
//@Table(name = "user_tasks")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class UserTask {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_task_id")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "program_task_id")
//    private ProgramTask programTask;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @Enumerated(EnumType.STRING)
//    private UserTaskStatus status;
//
//    @Column(name = "submitted_at")
//    private LocalDateTime submittedAt;
//
//    private Integer grade;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @Column(name = "is_deleted")
//    private Boolean isDeleted;
//
//    @OneToMany(mappedBy = "userTask", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TaskComment> comments = new ArrayList<>();
//
//    @Builder
//    public UserTask(ProgramTask programTask, User user, UserTaskStatus status, LocalDateTime submittedAt, Integer grade) {
//        this.programTask = programTask;
//        this.user = user;
//        this.status = status;
//        this.submittedAt = submittedAt;
//        this.grade = grade;
//        this.createdAt = LocalDateTime.now();
//        this.isDeleted = false;
//    }
//
//    public void updateStatus(UserTaskStatus status) {
//        this.status = status;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public void delete() {
//        this.isDeleted = true;
//    }
//}
