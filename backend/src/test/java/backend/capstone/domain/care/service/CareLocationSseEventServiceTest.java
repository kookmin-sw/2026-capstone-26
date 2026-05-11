package backend.capstone.domain.care.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.care.sse.dto.CareSseEventType;
import backend.capstone.domain.care.sse.registry.CareSseEmitterRegistry;
import backend.capstone.domain.care.sse.service.CareLocationSseEventService;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareLocationSseEventServiceTest {

    @InjectMocks
    private CareLocationSseEventService careLocationSseEventService;

    @Mock
    private CareRelationshipRepository careRelationshipRepository;

    @Mock
    private CareSseEmitterRegistry careSseEmitterRegistry;

    @Test
    void 최신_좌표가_null이면_sse_이벤트를_발행하지_않는다() {
        careLocationSseEventService.publishLocationUpdated(null);

        then(careRelationshipRepository).shouldHaveNoInteractions();
        then(careSseEmitterRegistry).shouldHaveNoInteractions();
    }

    @Test
    void 보호자가_없으면_sse_이벤트를_발행하지_않는다() {
        LatestGpsPoint latestGpsPoint = createLatestGpsPoint(1L);
        given(careRelationshipRepository.findGuardianUserIdsByDependentUserId(1L)).willReturn(
            List.of());

        careLocationSseEventService.publishLocationUpdated(latestGpsPoint);

        then(careRelationshipRepository).should().findGuardianUserIdsByDependentUserId(1L);
        then(careSseEmitterRegistry).shouldHaveNoInteractions();
    }

    @Test
    void 보호자들에게_location_updated_이벤트를_발행한다() {
        LatestGpsPoint latestGpsPoint = createLatestGpsPoint(1L);
        given(careRelationshipRepository.findGuardianUserIdsByDependentUserId(1L)).willReturn(
            List.of(10L, 20L));

        careLocationSseEventService.publishLocationUpdated(latestGpsPoint);

        then(careRelationshipRepository).should().findGuardianUserIdsByDependentUserId(1L);
        then(careSseEmitterRegistry).should()
            .publish(eq(10L), eq(CareSseEventType.LOCATION_UPDATED),
                org.mockito.ArgumentMatchers.any());
        then(careSseEmitterRegistry).should()
            .publish(eq(20L), eq(CareSseEventType.LOCATION_UPDATED),
                org.mockito.ArgumentMatchers.any());
        then(careSseEmitterRegistry).should(never())
            .publish(eq(30L), eq(CareSseEventType.LOCATION_UPDATED),
                org.mockito.ArgumentMatchers.any());
    }

    private LatestGpsPoint createLatestGpsPoint(Long userId) {
        User user = User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname("dependent")
            .profileImageUrl("https://example.com/profile.png")
            .build();
        setUserId(user, userId);
        LatestGpsPoint latestGpsPoint = LatestGpsPoint.of(user, 37.1, 127.1,
            Instant.parse("2026-05-11T10:00:00Z"));
        setLatestGpsPointUserId(latestGpsPoint, userId);
        return latestGpsPoint;
    }

    private void setUserId(User user, Long userId) {
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("테스트용 사용자 ID 설정에 실패했습니다.", e);
        }
    }

    private void setLatestGpsPointUserId(LatestGpsPoint latestGpsPoint, Long userId) {
        try {
            java.lang.reflect.Field userIdField = LatestGpsPoint.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(latestGpsPoint, userId);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("테스트용 최신 좌표 사용자 ID 설정에 실패했습니다.", e);
        }
    }
}
