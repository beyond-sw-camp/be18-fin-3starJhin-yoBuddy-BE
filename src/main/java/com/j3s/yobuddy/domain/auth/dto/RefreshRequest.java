package com.j3s.yobuddy.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {
    private String refreshToken;
}
