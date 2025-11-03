package com.j3s.yobuddy.domain.buddies.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buddies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Buddies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buddyId; // PK

    @Column(nullable = false)
    private Long userId; // 사용자 ID

    @Column(length = 100, nullable = false)
    private String position; // 직책 (예: Newbie, Buddy 등)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정 시간

    @Column(nullable = false)
    private boolean isDeleted; // 삭제 여부

//    @ManyToMany
//    @JoinTable(
//        joinColumns = @JoinColumn(name = "user_id"),
//        inverseJoinColumns = @JoinColumn(name = "buddy_id")
//    )
//    private Set<Buddies> buddies = new HashSet<>();
}