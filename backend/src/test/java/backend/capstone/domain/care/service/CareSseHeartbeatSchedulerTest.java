package backend.capstone.domain.care.service;

import static org.mockito.Mockito.verify;

import backend.capstone.domain.care.sse.registry.CareSseEmitterRegistry;
import backend.capstone.domain.care.sse.service.CareSseHeartbeatScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareSseHeartbeatSchedulerTest {

    @Mock
    private CareSseEmitterRegistry careSseEmitterRegistry;

    @InjectMocks
    private CareSseHeartbeatScheduler careSseHeartbeatScheduler;

    @Test
    void heartbeat_스케줄러가_레지스트리에_heartbeat_발행을_위임한다() {
        careSseHeartbeatScheduler.sendHeartbeat();

        verify(careSseEmitterRegistry).publishHeartbeat();
    }
}
