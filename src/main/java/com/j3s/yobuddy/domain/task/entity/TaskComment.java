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
//
//@Entity
//@Getter
//@Table(name = "task_comments")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class TaskComment {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "task_comment_id")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_task_id")
//    private UserTask userTask;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @Column(columnDefinition = "TEXT")
//    private String comment;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Builder
//    public TaskComment(UserTask userTask, User user, String comment) {
//        this.userTask = userTask;
//        this.user = user;
//        this.comment = comment;
//        this.createdAt = LocalDateTime.now();
//    }
//}
