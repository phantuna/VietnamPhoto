package com.example.backend.service.notification;

import com.example.backend.dto.response.notification.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public final class NotificationSseService {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(String userId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        emitters.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(error -> remove(userId, emitter));

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);
                emitter.send(SseEmitter.event().name("connected").data("SSE Connected successfully"));
            } catch (Exception e) {
                remove(userId, emitter);
            }
        });

        return emitter;
    }

    public void sendToUser(String userId, NotificationResponse data) {
        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters == null || userEmitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : userEmitters) {
            try {
                synchronized (emitter) {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(data));
                }
            } catch (IOException e) {
                remove(userId, emitter);
            }
        }
    }

    private void remove(String userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters != null) {
            userEmitters.remove(emitter);

            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }
}
