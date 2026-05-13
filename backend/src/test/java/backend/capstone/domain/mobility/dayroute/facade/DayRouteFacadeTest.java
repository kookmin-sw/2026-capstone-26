package backend.capstone.domain.mobility.dayroute.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import backend.capstone.domain.mobility.analysis.visitedregion.service.VisitedRegionService;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkBatchRequest;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.service.DayRouteService;
import backend.capstone.domain.mobility.gpspoint.service.GpsPointService;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
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
    private VisitedRegionService visitedRegionService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void 복수_즐겨찾기_요청은_중복_날짜를_제거하고_각_dayRoute를_토글한다() {
        LocalDate firstDate = LocalDate.of(2026, 5, 9);
        LocalDate secondDate = LocalDate.of(2026, 5, 10);
        DayRoute firstDayRoute = createDayRoute(firstDate);
        DayRoute secondDayRoute = createDayRoute(secondDate);

        given(dayRouteService.getOrCreate(1L, firstDate)).willReturn(firstDayRoute);
        given(dayRouteService.getOrCreate(1L, secondDate)).willReturn(secondDayRoute);

        dayRouteFacade.bookmarkDayRoutes(1L,
            new DayRouteBookmarkBatchRequest(List.of(firstDate, secondDate, firstDate)));

        then(dayRouteService).should().getOrCreate(1L, firstDate);
        then(dayRouteService).should().getOrCreate(1L, secondDate);
        then(dayRouteService).should().toggleBookmark(firstDayRoute);
        then(dayRouteService).should().toggleBookmark(secondDayRoute);
        then(dayRouteService).should(never()).bookmarkDayRoute(any());
    }

    private DayRoute createDayRoute(LocalDate date) {
        User user = User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname("tester")
            .profileImageUrl("https://example.com/profile.png")
            .build();
        return DayRoute.builder()
            .user(user)
            .date(date)
            .build();
    }
}
