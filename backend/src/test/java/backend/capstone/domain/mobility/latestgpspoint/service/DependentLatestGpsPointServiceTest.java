package backend.capstone.domain.mobility.latestgpspoint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.mobility.latestgpspoint.dto.DependentLatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DependentLatestGpsPointServiceTest {

    @InjectMocks
    private DependentLatestGpsPointService dependentCurrentLocationService;

    @Mock
    private CareRelationshipRepository careRelationshipRepository;

    @Mock
    private LatestGpsPointRepository latestGpsPointRepository;

    @Test
    void latest_location이_있으면_보호대상_최신_좌표를_반환한다() {
        LatestGpsPoint firstLocation = createLatestLocation(
            10L, 37.1, 127.1, Instant.parse("2026-05-10T00:00:00Z"));
        LatestGpsPoint secondLocation = createLatestLocation(
            20L, 37.2, 127.2, Instant.parse("2026-05-10T01:00:00Z"));

        given(careRelationshipRepository.findDependentUserIdsByGuardianUserId(1L))
            .willReturn(List.of(10L, 20L));
        given(latestGpsPointRepository.findAllByUserIdIn(List.of(10L, 20L)))
            .willReturn(List.of(firstLocation, secondLocation));

        List<DependentLatestGpsPoint> result =
            dependentCurrentLocationService.getDependentCurrentLocations(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DependentLatestGpsPoint::dependentUserId)
            .containsExactly(10L, 20L);
    }

    @Test
    void latest_location이_없는_사용자는_결과에서_제외한다() {
        LatestGpsPoint firstLocation = createLatestLocation(
            10L, 37.1, 127.1, Instant.parse("2026-05-10T00:00:00Z"));

        given(careRelationshipRepository.findDependentUserIdsByGuardianUserId(1L))
            .willReturn(List.of(10L, 20L));
        given(latestGpsPointRepository.findAllByUserIdIn(List.of(10L, 20L)))
            .willReturn(List.of(firstLocation));

        List<DependentLatestGpsPoint> result =
            dependentCurrentLocationService.getDependentCurrentLocations(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).dependentUserId()).isEqualTo(10L);
    }

    @Test
    void 보호대상자가_없으면_빈_목록을_반환한다() {
        given(careRelationshipRepository.findDependentUserIdsByGuardianUserId(1L))
            .willReturn(List.of());

        List<DependentLatestGpsPoint> result =
            dependentCurrentLocationService.getDependentCurrentLocations(1L);

        assertThat(result).isEmpty();
        then(latestGpsPointRepository).shouldHaveNoInteractions();
    }

    private LatestGpsPoint createLatestLocation(Long userId, double latitude, double longitude,
        Instant recordedAt) {
        LatestGpsPoint latestGpsPoint = mock(LatestGpsPoint.class);
        when(latestGpsPoint.getUserId()).thenReturn(userId);
        when(latestGpsPoint.getLatitude()).thenReturn(latitude);
        when(latestGpsPoint.getLongitude()).thenReturn(longitude);
        when(latestGpsPoint.getRecordedAt()).thenReturn(recordedAt);
        return latestGpsPoint;
    }
}
