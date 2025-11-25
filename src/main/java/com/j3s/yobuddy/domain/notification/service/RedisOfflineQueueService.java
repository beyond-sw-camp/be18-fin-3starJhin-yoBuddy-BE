package com.j3s.yobuddy.domain.notification.service;

import com.j3s.yobuddy.domain.notification.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisOfflineQueueService {

    private final RedisTemplate<String, Object> redis;

    private String key(Long userId) {
        return "notifications:offline:" + userId;
    }

    // SSE 실패 시 Redis Queue에 저장
    public void push(Long userId, NotificationPayload payload) {
        redis.opsForList().rightPush(key(userId), payload);
    }

    // 재연결 시 모든 미전송 Payload flush
    public List<Object> popAll(Long userId) {
        String k = key(userId);
        List<Object> list = redis.opsForList().range(k, 0, -1);
        redis.delete(k);
        return list;
    }
}