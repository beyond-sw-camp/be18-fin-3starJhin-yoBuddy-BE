package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.domain.user.dto.request.UpdateProfileRequest;
import com.j3s.yobuddy.domain.user.dto.request.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.dto.response.UserProfileResponse;
import com.j3s.yobuddy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping(
        value = "/me",
        consumes = {"multipart/form-data"}
    )
    public ResponseEntity<?> updateMyProfile(
        Authentication authentication,
        @RequestPart(value = "data") UpdateProfileRequest req,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        userService.updateMyAccount(userId, req, profileImage);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/profile-image")
    public ResponseEntity<?> deleteProfileImage(Authentication authentication) {

        Long userId = Long.valueOf((String) authentication.getPrincipal());
        userService.deleteProfileImage(userId);

        return ResponseEntity.noContent().build();
    }
}