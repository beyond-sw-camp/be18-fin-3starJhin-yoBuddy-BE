package com.j3s.yobuddy.domain.buddies.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "buddies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Buddies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buddyId;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 100, nullable = false)
    private String position;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    // 정적 팩토리 메서드 (생성 전용)
    public static Buddies create(Long userId, String position) {
        return Buddies.builder()
            .userId(userId)
            .position(position)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    // 직급 수정
    public void updatePosition(String position) {
        this.position = position;
        this.updatedAt = LocalDateTime.now();
    }

    // 삭제 처리 (soft delete)
    public void markDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}


//    @ManyToMany
//    @JoinTable(
//        joinColumns = @JoinColumn(name = "user_id"),
//        inverseJoinColumns = @JoinColumn(name = "buddy_id")
//    )
//    private Set<Buddies> buddies = new HashSet<>();
