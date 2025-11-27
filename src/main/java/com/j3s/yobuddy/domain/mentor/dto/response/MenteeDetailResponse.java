package com.j3s.yobuddy.domain.mentor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenteeDetailResponse {
    private final Long menteeId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String department;
    private final String joinedAt;
    private final String profileImageUrl;
}
