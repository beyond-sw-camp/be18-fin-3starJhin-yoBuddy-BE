package com.j3s.yobuddy.domain.buddies.controller;

import com.j3s.yobuddy.domain.buddies.dto.BuddiesRequest;
import com.j3s.yobuddy.domain.buddies.dto.BuddiesResponse;
import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import com.j3s.yobuddy.domain.buddies.service.BuddiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/buddies")
@RequiredArgsConstructor
public class BuddiesController {

    private final BuddiesService buddiesService;

    // 버디 생성
    @PostMapping
    public BuddiesResponse createBuddy(@RequestBody BuddiesRequest.Create request) {
        Buddies saved = buddiesService.createBuddy(request.getUserId(), request.getPosition());
        return BuddiesResponse.fromEntity(saved);
    }

    // 버디 목록 조회
    @GetMapping
    public List<BuddiesResponse> getBuddies(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String position
    ) {
        return buddiesService.getBuddies(userId, position)
            .stream()
            .map(BuddiesResponse::fromEntity)
            .collect(Collectors.toList());
    }

    // 버디 상세 조회
    @GetMapping("/{buddyId}")
    public BuddiesResponse getBuddyById(@PathVariable Long buddyId) {
        return BuddiesResponse.fromEntity(buddiesService.getBuddyById(buddyId));
    }

    // 버디 수정
    @PatchMapping("/{buddyId}")
    public BuddiesResponse updateBuddy(
        @PathVariable Long buddyId,
        @RequestBody BuddiesRequest.Update request
    ) {
        Buddies updated = buddiesService.updateBuddy(buddyId, request.getPosition());
        return BuddiesResponse.fromEntity(updated);
    }

    // 버디 삭제
    @DeleteMapping("/{buddyId}")
    public void deleteBuddy(@PathVariable Long buddyId) {
        buddiesService.deleteBuddy(buddyId);
    }
}
