package com.j3s.yobuddy.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileResponse {

    private final Long userId;
    private final String name;
    private final String email;
    private final String role;

    private final Long departmentId;
    private final String departmentName;

    private final String joinedAt;
    private final String createdAt;
    private final String updatedAt;

    private final String profileImageUrl;
}
