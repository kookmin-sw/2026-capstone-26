package backend.capstone.domain.mobility.dayroute.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import backend.capstone.domain.care.caredependent.sse.service.CareLocationSseEventService;
import backend.capstone.domain.mobility.analysis.visitedregion.service.VisitedRegionService;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadResponse;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.service.DayRouteService;
import backend.capstone.domain.mobility.gpspoint.service.GpsPointService;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.service.LatestGpsPointService;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class DayRouteFacadeTest {

    @InjectMocks
    private DayRouteFacade dayRouteFacade;

    @Mock
    private DayRouteService dayRouteService;

    @Mock
    private GpsPointService gpsPointService;

    @Mock
    private LatestGpsPointService latestLocationService;

    @Mock
    private CareLocationSseEventService careLocationSseEventService;

    @Mock
    private VisitedRegionService visitedRegionService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void 좌표를_업로드하면_latest_location을_갱신하고_sse_이벤트를_발행한다() {
        LocalDate date = LocalDate.of(2026, 5, 9);
        DayRoute dayRoute = DayRoute.builder()
            .user(createUser("day-route-user"))
            .date(date)
            .build();
        LatestGpsPoint latestGpsPoint = LatestGpsPoint.of(createUser("latest-point-user"), 37.2,
            127.2, Instant.parse("2026-05-09T10:05:00Z"));
        GpsPointBatchUploadRequest request = new GpsPointBatchUploadRequest(
            120.0,
            List.of(new GpsPointRequest(Instant.parse("2026-05-09T10:05:00Z"), 37.2, 127.2))
        );

        given(dayRouteService.getOrCreate(1L, date)).willReturn(dayRoute);
        given(latestLocationService.upsertLatestLocation(1L, request.gpsPoints())).willReturn(
            latestGpsPoint);

        GpsPointBatchUploadResponse response = dayRouteFacade.uploadGpsPoint(date, 1L, request);

        assertThat(response).isNotNull();
        then(gpsPointService).should().batchInsert(dayRoute.getId(), request);
        then(latestLocationService).should().upsertLatestLocation(1L, request.gpsPoints());
        then(careLocationSseEventService).should().publishLocationUpdated(latestGpsPoint);
        then(dayRouteService).should().markHasGpsPoints(dayRoute);
        then(dayRouteService).should().updateDistance(dayRoute, 120.0);
    }

    private User createUser(String nickname) {
        return User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname(nickname)
            .profileImageUrl("https://example.com/profile.png")
            .build();
    }
}
