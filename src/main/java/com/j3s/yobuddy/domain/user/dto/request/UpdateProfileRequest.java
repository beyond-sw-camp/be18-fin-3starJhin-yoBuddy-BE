package com.j3s.yobuddy.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateProfileRequest {

    private final String phoneNumber;
    private final String currentPassword;
    private final String newPassword;
}