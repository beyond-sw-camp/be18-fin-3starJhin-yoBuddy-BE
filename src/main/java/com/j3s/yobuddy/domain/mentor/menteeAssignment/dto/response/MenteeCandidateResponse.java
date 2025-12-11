package com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenteeCandidateResponse {
    private final Long userId;
    private final String name;
    private final String email;
    private final String department;
}
