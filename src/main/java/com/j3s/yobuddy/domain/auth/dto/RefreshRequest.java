package com.j3s.yobuddy.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RefreshRequest {
    private final String refreshToken;
}
