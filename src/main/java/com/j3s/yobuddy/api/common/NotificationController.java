package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.common.security.JwtTokenProvider;
import com.j3s.yobuddy.domain.notification.dto.NotificationPayload;
import com.j3s.yobuddy.domain.notification.service.NotificationQueryService;
import com.j3s.yobuddy.domain.notification.sse.SseEmitterManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final SseEmitterManager sseManager;
    private final NotificationQueryService queryService;
    private final JwtTokenProvider jwtTokenProvider;

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null)
            throw new SecurityException("Unauthenticated");

        return Long.valueOf(auth.getPrincipal().toString());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(HttpServletRequest request, HttpServletResponse response) {

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        return sseManager.connect(userId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationPayload>> getNotifications(Authentication auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(queryService.getRecentNotifications(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication auth) {
        Long userId = extractUserId(auth);
        queryService.markNotificationAsRead(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        Long userId = extractUserId(auth);
        queryService.deleteNotification(id, userId);
        return ResponseEntity.noContent().build();
    }
}
