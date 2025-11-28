package com.j3s.yobuddy.domain.user.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentorResponse {

    private final Long MentorId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String department;
    private final String profileImageUrl;

}
