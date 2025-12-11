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

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final Map<Long, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();

    private final Map<Long, Integer> heartbeatFails = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SseEmitter connect(Long userId) {

        forceCleanup(userId);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        log.info("SSE connected for user {}", userId);

        ScheduledFuture<?> hb = scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
                heartbeatFails.put(userId, 0); // reset fail count

            } catch (IOException e) {

                int count = heartbeatFails.getOrDefault(userId, 0) + 1;
                heartbeatFails.put(userId, count);

                log.warn("Heartbeat failed {} times for user {}", count, userId);

                if (count >= 3) {  // 3회 실패 시 끊기
                    log.warn("Heartbeat threshold reached → disconnecting user {}", userId);
                    forceCleanup(userId);
                }
            }
        }, 10, 10, TimeUnit.SECONDS);

        heartbeatTasks.put(userId, hb);

        emitter.onCompletion(() -> forceCleanup(userId));
        emitter.onTimeout(() -> forceCleanup(userId));
        emitter.onError(e -> forceCleanup(userId));

        flushOfflineQueue(userId);

        return emitter;
    }

    private void forceCleanup(Long userId) {

        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            try { emitter.complete(); } catch (Exception ignored) {}
        }

        ScheduledFuture<?> hb = heartbeatTasks.remove(userId);
        if (hb != null) hb.cancel(true);

        heartbeatFails.remove(userId);

        log.info("[CLEANUP] SSE emitter cleared for user {}", userId);
    }

    private void flushOfflineQueue(Long userId) {
        List<Object> list = offlineQueue.popAll(userId);
        if (list == null || list.isEmpty()) return;

        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        for (Object obj : list) {
            try {
                NotificationPayload payload = (NotificationPayload) obj;

                emitter.send(
                    SseEmitter.event()
                        .name(payload.getType())
                        .data(payload)
                );

            } catch (Exception ignored) {}
        }

        log.info("Flushed {} offline notifications for user {}", list.size(), userId);
    }

    public void send(Long userId, NotificationPayload payload) {

        SseEmitter emitter = emitters.get(userId);

        if (emitter == null) {
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
            log.warn("Send failed for user {} → retry", userId);

            try {
                emitter.send(
                    SseEmitter.event()
                        .name(payload.getType())
                        .data(payload)
                );
                return;

            } catch (Exception ex) {
                log.warn("Retry failed → offline store & cleanup for user {}", userId);
                forceCleanup(userId);
                offlineQueue.push(userId, payload);
            }
        }
    }

    public void disconnect(Long userId) {
        forceCleanup(userId);
    }
}

