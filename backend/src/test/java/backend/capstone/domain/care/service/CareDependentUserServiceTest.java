package backend.capstone.domain.care.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.capstone.domain.care.caredependent.dto.CareDependentUserListResponse;
import backend.capstone.domain.care.caredependent.service.CareDependentUserService;
import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareDependentUserServiceTest {

    @Mock
    private CareRelationshipRepository careRelationshipRepository;

    @Mock
    private LatestGpsPointRepository latestGpsPointRepository;

    @InjectMocks
    private CareDependentUserService careDependentUserService;

    @Test
    void 보호대상자목록을_최신위치와_함께_조회한다() {
        User firstDependentUser = createUser(10L, "첫째", "https://example.com/1.png");
        User secondDependentUser = createUser(20L, "둘째", "https://example.com/2.png");
        LatestGpsPoint firstLatestGpsPoint = createLatestGpsPoint(
            10L, 37.1, 127.1, Instant.parse("2026-05-10T00:00:00Z"));

        given(careRelationshipRepository.findDependentUsersByGuardianUserId(1L))
            .willReturn(List.of(firstDependentUser, secondDependentUser));
        given(latestGpsPointRepository.findAllByUserIdIn(List.of(10L, 20L)))
            .willReturn(List.of(firstLatestGpsPoint));

        CareDependentUserListResponse result = careDependentUserService.getDependentUsers(1L);

        assertThat(result.dependentUserCount()).isEqualTo(2);
        assertThat(result.dependentUsers()).hasSize(2);
        assertThat(result.dependentUsers()).extracting(
                CareDependentUserListResponse.CareDependentUserItem::dependentUserId)
            .containsExactly(10L, 20L);
        assertThat(result.dependentUsers()).extracting(
                CareDependentUserListResponse.CareDependentUserItem::nickname)
            .containsExactly("첫째", "둘째");
        assertThat(result.dependentUsers()).extracting(
                CareDependentUserListResponse.CareDependentUserItem::profileImageUrl)
            .containsExactly("https://example.com/1.png", "https://example.com/2.png");
        assertThat(result.dependentUsers().get(0).latestGpsPoint()).isNotNull();
        assertThat(result.dependentUsers().get(0).latestGpsPoint().recordedAt())
            .isEqualTo(OffsetDateTime.parse("2026-05-10T09:00:00+09:00"));
        assertThat(result.dependentUsers().get(1).latestGpsPoint()).isNull();
    }

    @Test
    void 보호대상자가_없으면_빈목록을_반환한다() {
        given(careRelationshipRepository.findDependentUsersByGuardianUserId(1L))
            .willReturn(List.of());

        CareDependentUserListResponse result = careDependentUserService.getDependentUsers(1L);

        assertThat(result.dependentUserCount()).isZero();
        assertThat(result.dependentUsers()).isEmpty();
        then(latestGpsPointRepository).shouldHaveNoInteractions();
    }

    private User createUser(Long userId, String nickname, String profileImageUrl) {
        User user = User.builder()
            .provider(ProviderType.KAKAO)
            .providerId("provider-" + userId)
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
            .build();

        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("사용자 ID 설정에 실패했습니다.", e);
        }

        return user;
    }

    private LatestGpsPoint createLatestGpsPoint(Long userId, double latitude, double longitude,
        Instant recordedAt) {
        LatestGpsPoint latestGpsPoint = mock(LatestGpsPoint.class);
        when(latestGpsPoint.getUserId()).thenReturn(userId);
        when(latestGpsPoint.getLatitude()).thenReturn(latitude);
        when(latestGpsPoint.getLongitude()).thenReturn(longitude);
        when(latestGpsPoint.getRecordedAt()).thenReturn(recordedAt);
        return latestGpsPoint;
    }
}
