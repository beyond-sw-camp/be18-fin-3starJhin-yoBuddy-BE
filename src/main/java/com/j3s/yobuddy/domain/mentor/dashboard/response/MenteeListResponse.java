package com.j3s.yobuddy.domain.mentor.dashboard.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenteeListResponse {

    private final List<MenteeItem> mentees;

    @Getter
    @AllArgsConstructor
    public static class MenteeItem {
        private final Long menteeId;
        private final String name;
        private final String email;
        private final String phoneNumber;
        private final String department;
        private final String profileImageUrl;
        private final LocalDateTime joinedAt;
        private final int completedTrainings;
        private final int pendingTrainings;
    }
}