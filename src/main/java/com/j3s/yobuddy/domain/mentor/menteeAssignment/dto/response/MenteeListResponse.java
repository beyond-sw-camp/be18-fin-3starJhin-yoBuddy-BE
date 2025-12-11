package com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenteeListResponse {
    private final Long menteeId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String department;
    private final String profileImageUrl;
}
