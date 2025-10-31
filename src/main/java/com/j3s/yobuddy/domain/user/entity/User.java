package com.j3s.yobuddy.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK 깅

    @Column(nullable = false, unique = true)
    private String email; // 로그인 아이디 깙

    @Column(nullable = false)
    private String password; // BCrypt로 암호화 저장 깙

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // USER / ADMIN 구분 깙

    private boolean enabled = true; // 계정 활성화 여부 깙
}