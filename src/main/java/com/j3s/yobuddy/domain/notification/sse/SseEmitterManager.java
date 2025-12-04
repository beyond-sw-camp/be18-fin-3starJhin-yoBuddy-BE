package com.j3s.yobuddy.domain.notification.sse;

import com.j3s.yobuddy.domain.notification.dto.NotificationPayload;
import com.j3s.yobuddy.domain.notification.service.RedisOfflineQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseEmitterManager {

    private final RedisOfflineQueueService offlineQueue;

    // 단일 SSE 연결 유지
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SseEmitter connect(Long userId) {

        // 기존 연결 제거
        cleanup(userId);

        SseEmitter emitter = new SseEmitter(0L); // timeout 없음
        emitters.put(userId, emitter);

        log.info("Created SSE emitter for user {}", userId);

        // heartbeat (표준 SSE ping 방식)
        ScheduledFuture<?> hb = scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(":\n\n");   // SSE ping
            } catch (IOException e) {
                log.warn("Heartbeat failed for user {}. Disconnecting.", userId);
                cleanup(userId);
            }
        }, 10, 10, TimeUnit.SECONDS);

        heartbeatTasks.put(userId, hb);

        emitter.onCompletion(() -> cleanup(userId));
        emitter.onTimeout(() -> cleanup(userId));
        emitter.onError((e) -> cleanup(userId));

        // 재연결 시 redis queue flush
        flushOfflineQueue(userId);

        return emitter;
    }


    private void flushOfflineQueue(Long userId) {
        List<Object> items = offlineQueue.popAll(userId);
        if (items == null || items.isEmpty()) return;

        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        for (Object obj : items) {
            try {
                NotificationPayload payload = (NotificationPayload) obj;

                emitter.send(
                    SseEmitter.event()
                        .name(payload.getType())
                        .data(payload)
                );
            } catch (Exception ignored) {}
        }

        log.info("Flushed {} offline notifications for user {}", items.size(), userId);
    }


    public void send(Long userId, NotificationPayload payload) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter == null) {
            log.info("User {} offline → queueing notification", userId);
            offlineQueue.push(userId, payload);
            return;
        }

        try {
            emitter.send(
                SseEmitter.event()
                    .name(payload.getType())
                    .data(payload)
            );
        } catch (Exception e) {
            log.warn("User {} SSE failed → queueing", userId);
            cleanup(userId);
            offlineQueue.push(userId, payload);
        }
    }


    private void cleanup(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            try { emitter.complete(); } catch (Exception ignored) {}
        }

        ScheduledFuture<?> hb = heartbeatTasks.remove(userId);
        if (hb != null) hb.cancel(true);

        log.info("Cleaned SSE emitter for user {}", userId);
    }

    public void disconnect(Long userId) {
        cleanup(userId);
    }
}
