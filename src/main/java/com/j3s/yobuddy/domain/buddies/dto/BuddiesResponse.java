package com.j3s.yobuddy.domain.buddies.dto;

import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BuddiesResponse {

    private Long buddyId;
    private Long userId;
    private String position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BuddiesResponse fromEntity(Buddies entity) {
        return BuddiesResponse.builder()
            .buddyId(entity.getBuddyId())
            .userId(entity.getUserId())
            .position(entity.getPosition())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
