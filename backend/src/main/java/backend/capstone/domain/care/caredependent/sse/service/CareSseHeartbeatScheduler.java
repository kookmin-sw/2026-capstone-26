package backend.capstone.domain.care.caredependent.sse.service;

import backend.capstone.domain.care.caredependent.sse.registry.CareSseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CareSseHeartbeatScheduler {

    private final CareSseEmitterRegistry careSseEmitterRegistry;

    @Scheduled(fixedDelayString = "30000", initialDelayString = "30000")
    public void sendHeartbeat() {
        careSseEmitterRegistry.publishHeartbeat();
    }
}
