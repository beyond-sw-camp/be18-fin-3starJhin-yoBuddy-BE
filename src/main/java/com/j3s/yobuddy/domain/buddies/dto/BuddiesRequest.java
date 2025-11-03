package com.j3s.yobuddy.domain.buddies.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class BuddiesRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private Long userId;
        private String position;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String position;
    }
}
