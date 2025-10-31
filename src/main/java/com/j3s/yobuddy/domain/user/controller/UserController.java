package com.j3s.yobuddy.domain.user.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<Void> register(@RequestBody RegisterRequest req) {
		User saved = userService.register(req);
		return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).build();
	}
}
