package com.j3s.yobuddy.domain.user.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.j3s.yobuddy.domain.department.entity.Department;

@Entity
@Table(name = "users") // 테이블 이름 명시 (대소문자 구분 피하려면 소문자 추천)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // 유니크 사용자 ID

    @Column(nullable = false, length = 100)
    private String name; // 이름

    @Column(nullable = false, unique = true, length = 255)
    private String email; // 로그인/연락용 이메일

    @Column(nullable = false)
    private String password; // 해시된 비밀번호

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber; // 핸드폰 번호

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role; // HR / USER / BUDDY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department; // 소속 부서 (FK)

    @Column(name = "joined_at")
    private LocalDateTime joinedAt; // 입사일

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 계정 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 계정 수정일

    @Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // 데이터 삭제 여부

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.joinedAt == null) {
            this.joinedAt = now;
        }
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.isDeleted);
    }

    public void softDelete() {
        if (!isDeleted()) {
            this.isDeleted = true;
            this.updatedAt = LocalDateTime.now();
        }
    }
}
