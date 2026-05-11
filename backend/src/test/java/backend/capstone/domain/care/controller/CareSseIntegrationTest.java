package backend.capstone.domain.care.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.capstone.auth.jwt.service.JwtTokenProvider;
import backend.capstone.domain.care.carerelationship.entity.CareRelationship;
import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.care.service.CareSseEmitterRegistry;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CareSseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareRelationshipRepository careRelationshipRepository;

    @Autowired
    private CareSseEmitterRegistry careSseEmitterRegistry;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 보호자_sse_구독_후_connected와_위치_업데이트_이벤트를_응답에서_확인한다() throws Exception {
        User guardian = createUser("guardian");
        User dependent = createUser("dependent");
        careRelationshipRepository.save(CareRelationship.of(guardian, dependent));
        String accessToken = jwtTokenProvider.createAccessToken(guardian.getId());

        MvcResult result = mockMvc.perform(get("/api/care/dependents/stream")
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
            .andReturn();

        waitUntil(() -> careSseEmitterRegistry.count(guardian.getId()) == 1);
        waitUntil(() -> result.getResponse().getContentAsString().contains("event:connected"));

        careSseEmitterRegistry.publish(guardian.getId(), "location-updated",
            "{\"dependentUserId\":%d,\"latitude\":37.1,\"longitude\":127.1}".formatted(
                dependent.getId()));

        waitUntil(
            () -> result.getResponse().getContentAsString().contains("event:location-updated"));

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("event:connected");
        assertThat(responseBody).contains("data:");
        assertThat(responseBody).contains("event:location-updated");
        assertThat(responseBody).contains("\"dependentUserId\":%d".formatted(dependent.getId()));
    }

    private User createUser(String nickname) {
        return userRepository.save(User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname(nickname)
            .profileImageUrl("https://example.com/profile.png")
            .build());
    }

    private void waitUntil(CheckedBooleanSupplier condition) throws Exception {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(5));
        while (Instant.now().isBefore(deadline)) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(50);
        }
        throw new AssertionError("조건이 5초 안에 만족되지 않았습니다.");
    }

    @FunctionalInterface
    private interface CheckedBooleanSupplier {

        boolean getAsBoolean() throws Exception;
    }
}
