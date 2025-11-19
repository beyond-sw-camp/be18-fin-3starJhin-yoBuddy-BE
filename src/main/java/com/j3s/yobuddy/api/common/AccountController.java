package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.domain.user.dto.request.UpdateProfileRequest;
import com.j3s.yobuddy.domain.user.dto.request.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.dto.response.UserProfileResponse;
import com.j3s.yobuddy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {

        Long userId = Long.valueOf((String) authentication.getPrincipal());

        UserProfileResponse profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMyProfile(
        Authentication authentication,
        @RequestBody UpdateProfileRequest req
    ) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());

        userService.updateMyAccount(userId, req);

        return ResponseEntity.noContent().build();
    }
}