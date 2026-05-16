package backend.capstone.domain.mobility.latestgpspoint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LatestLocationServiceTest {

    @InjectMocks
    private LatestGpsPointService latestLocationService;

    @Mock
    private LatestGpsPointRepository latestGpsPointRepository;

    @Mock
    private UserService userService;

    @Test
    void 업로드_좌표가_없으면_저장하지_않는다() {
        latestLocationService.upsertLatestLocation(1L, List.of());

        then(latestGpsPointRepository).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void 기존_latest_location이_없으면_새로_저장한다() {
        User user = createUser();
        List<GpsPointRequest> gpsPoints = List.of(
            new GpsPointRequest(Instant.parse("2026-05-10T10:00:00Z"), 37.1, 127.1),
            new GpsPointRequest(Instant.parse("2026-05-10T10:05:00Z"), 37.2, 127.2)
        );
        given(latestGpsPointRepository.findById(1L)).willReturn(Optional.empty());
        given(userService.findById(1L)).willReturn(user);

        latestLocationService.upsertLatestLocation(1L, gpsPoints);

        ArgumentCaptor<LatestGpsPoint> captor = ArgumentCaptor.forClass(LatestGpsPoint.class);
        then(latestGpsPointRepository).should().save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(captor.getValue().getLatitude()).isEqualTo(37.2);
        assertThat(captor.getValue().getLongitude()).isEqualTo(127.2);
        assertThat(captor.getValue().getRecordedAt())
            .isEqualTo(Instant.parse("2026-05-10T10:05:00Z"));
    }

    @Test
    void 기존_좌표보다_더_최신이면_갱신한다() {
        LatestGpsPoint latestGpsPoint = LatestGpsPoint.of(
            createUser(),
            37.1,
            127.1,
            Instant.parse("2026-05-10T10:00:00Z")
        );
        List<GpsPointRequest> gpsPoints = List.of(
            new GpsPointRequest(Instant.parse("2026-05-10T10:05:00Z"), 37.2, 127.2)
        );
        given(latestGpsPointRepository.findById(1L)).willReturn(Optional.of(latestGpsPoint));

        latestLocationService.upsertLatestLocation(1L, gpsPoints);

        assertThat(latestGpsPoint.getLatitude()).isEqualTo(37.2);
        assertThat(latestGpsPoint.getLongitude()).isEqualTo(127.2);
        assertThat(latestGpsPoint.getRecordedAt())
            .isEqualTo(Instant.parse("2026-05-10T10:05:00Z"));
        then(latestGpsPointRepository).should(never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 기존_좌표보다_오래된_좌표면_무시한다() {
        LatestGpsPoint latestGpsPoint = LatestGpsPoint.of(
            createUser(),
            37.2,
            127.2,
            Instant.parse("2026-05-10T10:05:00Z")
        );
        List<GpsPointRequest> gpsPoints = List.of(
            new GpsPointRequest(Instant.parse("2026-05-10T10:00:00Z"), 37.1, 127.1)
        );
        given(latestGpsPointRepository.findById(1L)).willReturn(Optional.of(latestGpsPoint));

        latestLocationService.upsertLatestLocation(1L, gpsPoints);

        assertThat(latestGpsPoint.getLatitude()).isEqualTo(37.2);
        assertThat(latestGpsPoint.getLongitude()).isEqualTo(127.2);
        assertThat(latestGpsPoint.getRecordedAt())
            .isEqualTo(Instant.parse("2026-05-10T10:05:00Z"));
        then(latestGpsPointRepository).should(never()).save(org.mockito.ArgumentMatchers.any());
    }

    private User createUser() {
        return User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname("tester")
            .profileImageUrl("https://example.com/profile.png")
            .build();
    }
}
