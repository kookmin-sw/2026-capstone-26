package backend.capstone.domain.mobility.dayroute.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import backend.capstone.domain.mobility.analysis.visitedregion.service.VisitedRegionService;
import backend.capstone.domain.mobility.currentlocation.exception.CurrentLocationCacheException;
import backend.capstone.domain.mobility.currentlocation.service.CurrentLocationCacheService;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadResponse;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.service.DayRouteService;
import backend.capstone.domain.mobility.gpspoint.service.GpsPointService;
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
    private CurrentLocationCacheService currentLocationCacheService;

    @Mock
    private VisitedRegionService visitedRegionService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void redis_저장에_실패해도_좌표_업로드는_성공한다() {
        LocalDate date = LocalDate.of(2026, 5, 9);
        DayRoute dayRoute = DayRoute.builder()
            .user(createUser())
            .date(date)
            .build();
        GpsPointBatchUploadRequest request = new GpsPointBatchUploadRequest(
            120.0,
            List.of(new GpsPointRequest(Instant.parse("2026-05-09T10:05:00Z"), 37.2, 127.2))
        );

        given(dayRouteService.getOrCreate(1L, date)).willReturn(dayRoute);
        org.mockito.BDDMockito.willThrow(
            new CurrentLocationCacheException("Failed to save current location cache.", new RuntimeException())
        ).given(currentLocationCacheService).saveLatestLocation(1L, request.gpsPoints());

        GpsPointBatchUploadResponse response = dayRouteFacade.uploadGpsPoint(date, 1L, request);

        assertThat(response).isNotNull();
        then(gpsPointService).should().batchInsert(dayRoute.getId(), request);
        then(dayRouteService).should().markHasGpsPoints(dayRoute);
        then(dayRouteService).should().updateDistance(dayRoute, 120.0);
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
