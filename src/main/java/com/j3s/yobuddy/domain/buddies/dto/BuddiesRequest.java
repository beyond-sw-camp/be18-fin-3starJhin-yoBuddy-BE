package com.j3s.yobuddy.domain.buddies.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BuddiesRequest {
    private Long userId;
    private String position;
}
