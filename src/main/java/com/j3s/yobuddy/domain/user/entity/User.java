package com.j3s.yobuddy.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK 

    @Column(nullable = false, unique = true)
    private String email; // 로그인 아이디

    @Column(nullable = false)
    private String password; // BCrypt로 암호화 저장

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // USER / ADMIN 구분

    private boolean enabled = true; // 계정 활성화 여부
}