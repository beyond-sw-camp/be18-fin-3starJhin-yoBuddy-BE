package com.j3s.yobuddy.domain.auth.entity;

import java.time.Instant;

import com.j3s.yobuddy.domain.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
public class RefreshToken {
  @Id @GeneratedValue private Long id;
  @Column(nullable=false, unique=true) private String tokenId; // JWT jti 깅
  @ManyToOne(fetch = FetchType.LAZY) private Users user;
  private Instant expiresAt;
  private boolean revoked = false;
}