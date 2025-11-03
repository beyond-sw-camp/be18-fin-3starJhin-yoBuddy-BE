package com.j3s.yobuddy.domain.buddies.controller;

import com.j3s.yobuddy.domain.buddies.dto.BuddiesRequest;
import com.j3s.yobuddy.domain.buddies.dto.BuddiesResponse;
import com.j3s.yobuddy.domain.buddies.entity.Buddies;
import com.j3s.yobuddy.domain.buddies.service.BuddiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BuddiesResponse> createBuddy(@RequestBody BuddiesRequest request) {
        Buddies buddy = buddiesService.createBuddy(request.getUserId(), request.getPosition());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(BuddiesResponse.fromEntity(buddy));
    }

    // 버디 목록 조회
    @GetMapping
    public ResponseEntity<List<BuddiesResponse>> getBuddies(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String position
    ) {
        List<BuddiesResponse> response = buddiesService.getBuddies(userId, position)
            .stream()
            .map(BuddiesResponse::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 버디 상세 조회
    @GetMapping("/{buddyId}")
    public ResponseEntity<BuddiesResponse> getBuddy(@PathVariable Long buddyId) {
        Buddies buddy = buddiesService.getBuddyById(buddyId);
        return ResponseEntity.ok(BuddiesResponse.fromEntity(buddy));
    }

    // 버디 수정
    @PatchMapping("/{buddyId}")
    public ResponseEntity<BuddiesResponse> updateBuddy(
        @PathVariable Long buddyId,
        @RequestBody BuddiesRequest request
    ) {
        Buddies buddy = buddiesService.updateBuddy(buddyId, request.getPosition());
        return ResponseEntity.ok(BuddiesResponse.fromEntity(buddy));
    }

    // 버디 삭제
    @DeleteMapping("/{buddyId}")
    public ResponseEntity<Void> deleteBuddy(@PathVariable Long buddyId) {
        buddiesService.deleteBuddy(buddyId);
        return ResponseEntity.noContent().build(); // 204 반환
    }
}