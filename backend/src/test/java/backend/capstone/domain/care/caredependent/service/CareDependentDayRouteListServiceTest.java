package backend.capstone.domain.care.caredependent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.facade.DayRouteFacade;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import backend.capstone.domain.mobility.place.facade.PlaceFacade;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CareDependentDayRouteListServiceTest {

    @InjectMocks
    private CareDependentUserService careDependentUserService;

    @Mock
    private CareRelationshipRepository careRelationshipRepository;

    @Mock
    private LatestGpsPointRepository latestGpsPointRepository;

    @Mock
    private DayRouteRepository dayRouteRepository;

    @Mock
    private DayRouteFacade dayRouteFacade;

    @Mock
    private PlaceFacade placeFacade;

    @Test
    void 보호대상자_dayRoute_목록을_날짜_내림차순_커서_페이징으로_조회한다() {
        User dependentUser = createUser();
        DayRoute recentDayRoute = createDayRoute(dependentUser, LocalDate.of(2026, 5, 13),
            Instant.parse("2026-05-13T00:00:00Z"), Instant.parse("2026-05-13T14:00:00Z"), 2);
        DayRoute middleDayRoute = createDayRoute(dependentUser, LocalDate.of(2026, 5, 12),
            Instant.parse("2026-05-12T01:00:00Z"), Instant.parse("2026-05-12T13:00:00Z"), 1);
        DayRoute oldDayRoute = createDayRoute(dependentUser, LocalDate.of(2026, 5, 11),
            null, null, 0);

        given(careRelationshipRepository.existsByGuardianUserIdAndDependentUserId(1L, 20L))
            .willReturn(true);
        given(dayRouteRepository.findByUserIdAndCursorDateOrderByDateDesc(20L, null,
            PageRequest.of(0, 3)))
            .willReturn(List.of(recentDayRoute, middleDayRoute, oldDayRoute));

        var result = careDependentUserService.getDependentUserDayRoutes(1L, 20L, null, 2);

        assertThat(result.dayRouteCount()).isEqualTo(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursorDate()).isEqualTo(LocalDate.of(2026, 5, 12));
        assertThat(result.dayRoutes()).hasSize(2);
        assertThat(result.dayRoutes())
            .extracting(item -> item.date())
            .containsExactly(LocalDate.of(2026, 5, 13), LocalDate.of(2026, 5, 12));
        assertThat(result.dayRoutes().get(0).outingTime())
            .isEqualTo(OffsetDateTime.parse("2026-05-13T09:00:00+09:00"));
        assertThat(result.dayRoutes().get(0).enterHomeTime())
            .isEqualTo(OffsetDateTime.parse("2026-05-13T23:00:00+09:00"));
        assertThat(result.dayRoutes().get(0).totalOutingCount()).isEqualTo(2);
    }

    private User createUser() {
        return User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname("dependent")
            .profileImageUrl("https://example.com/dependent.png")
            .build();
    }

    private DayRoute createDayRoute(User user, LocalDate date, Instant outingTime,
        Instant enterHomeTime, int totalOutingCount) {
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(date)
            .build();
        if (outingTime != null) {
            dayRoute.markOuting(outingTime);
            for (int count = 1; count < totalOutingCount; count++) {
                dayRoute.markOuting(outingTime.plusSeconds(count));
            }
        }
        if (enterHomeTime != null) {
            dayRoute.markReturnedHome(enterHomeTime);
        }
        return dayRoute;
    }
}
