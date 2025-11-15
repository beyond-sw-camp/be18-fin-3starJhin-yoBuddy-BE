package com.j3s.yobuddy.api.mentor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.user.dto.UpdateUserRequest;
import com.j3s.yobuddy.domain.user.dto.UserResponse;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentor/users")
public class MentorUserController {

	private final UserService userService;

	@GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(UserResponse.from(user));
    }

	@PatchMapping("/{userId}")
	public ResponseEntity<Map<String, Object>> update(@PathVariable Long userId, @RequestBody UpdateUserRequest req) {
		userService.update(userId, req);
		return ResponseEntity.ok(successResponse(HttpStatus.OK, "사용자 정보가 성공적으로 수정되었습니다."));
	}


	private Map<String, Object> successResponse(HttpStatus status, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("status", status.value());
		body.put("message", message);
		return body;
	}
}
