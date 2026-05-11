package backend.capstone.domain.care.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// "지금 누가 sse 연결을 열어두었는지"를 서버 메모리에 보관하는 클래스
@Slf4j
@Component
public class CareSseEmitterRegistry {

    //TODO: 타임아웃 후 재연결
    private static final long SSE_TIMEOUT_MILLIS = 60L * 60L * 1000L; //1시간

    private final Map<Long, Map<String, SseEmitter>> emittersByGuardianUserId =
        new ConcurrentHashMap<>();

    public SseEmitter register(Long guardianUserId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
        String emitterId = UUID.randomUUID().toString(); //멀티 디바이스 고려

        emittersByGuardianUserId.computeIfAbsent(guardianUserId,
                ignored -> new ConcurrentHashMap<>())
            .put(emitterId, emitter);

        emitter.onCompletion(() -> remove(guardianUserId, emitterId));
        emitter.onTimeout(() -> remove(guardianUserId, emitterId));
        emitter.onError(ignored -> remove(guardianUserId, emitterId));

        sendConnectedEvent(guardianUserId, emitterId, emitter);
        return emitter;
    }

    public List<SseEmitter> getEmitters(Long guardianUserId) {
        Map<String, SseEmitter> emitters = emittersByGuardianUserId.get(guardianUserId);
        if (emitters == null || emitters.isEmpty()) {
            return List.of();
        }
        return List.copyOf(emitters.values());
    }

    public void remove(Long guardianUserId, String emitterId) {
        Map<String, SseEmitter> emitters = emittersByGuardianUserId.get(guardianUserId);
        if (emitters == null) {
            return;
        }

        emitters.remove(emitterId);
        if (emitters.isEmpty()) {
            emittersByGuardianUserId.remove(guardianUserId);
        }
    }

    public int count(Long guardianUserId) {
        Map<String, SseEmitter> emitters = emittersByGuardianUserId.get(guardianUserId);
        return emitters == null ? 0 : emitters.size();
    }

    private void sendConnectedEvent(Long guardianUserId, String emitterId, SseEmitter emitter) {
        try {
            emitter.send(
                SseEmitter.event()
                    .name("connected")
                    .id(emitterId)
                    .data("보호 대상 위치 SSE 연결이 생성되었습니다.")
            );
        } catch (IOException e) {
            log.warn("SSE 연결 초기 이벤트 전송에 실패했습니다. guardianUserId={}", guardianUserId, e);
            remove(guardianUserId, emitterId);
        }
    }
}
