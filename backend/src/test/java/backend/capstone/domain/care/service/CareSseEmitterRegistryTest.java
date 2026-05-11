package backend.capstone.domain.care.service;

import static org.assertj.core.api.Assertions.assertThat;

import backend.capstone.domain.care.sse.registry.CareSseEmitterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class CareSseEmitterRegistryTest {

    private final CareSseEmitterRegistry registry = new CareSseEmitterRegistry();

    @Test
    void 보호자별로_sse_emitter를_등록한다() {
        SseEmitter emitter = registry.register(1L);

        assertThat(emitter).isNotNull();
        assertThat(registry.count(1L)).isEqualTo(1);
        assertThat(registry.getEmitters(1L)).hasSize(1);
    }

    @Test
    void 보호자별_emitter가_없으면_빈_목록을_반환한다() {
        assertThat(registry.getEmitters(1L)).isEmpty();
        assertThat(registry.count(1L)).isZero();
    }

    @Test
    void heartbeat_발행_대상이_없으면_예외_없이_종료된다() {
        registry.publishHeartbeat();

        assertThat(registry.count(1L)).isZero();
    }
}
