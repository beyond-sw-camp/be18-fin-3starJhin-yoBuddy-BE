//package com.j3s.yobuddy.domain.task.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class TaskFile {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // 파일 이름 (client friendly)
//    private String fileName;
//
//    // 실제 저장된 경로
//    private String filePath;
//
//    // 원본 파일명 (선택)
//    private String originalName;
//
//    // 관리자가 등록한 파일인지 사용자 제출 파일인지 구분
//    @Enumerated(EnumType.STRING)
//    private TaskFileType fileType;
//
//    /**
//     * 제출 파일이면 UserTaskSubmission과 연결됨
//     * 관리자가 등록한 파일이면 OnboardingTask와 연결됨
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "task_id")
//    private OnboardingTask onboardingTask;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "submission_id")
//    private TaskSubmission taskSubmission;
//}
