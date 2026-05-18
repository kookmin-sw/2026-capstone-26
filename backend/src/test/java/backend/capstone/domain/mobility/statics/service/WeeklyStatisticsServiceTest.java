package backend.capstone.domain.mobility.statics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import backend.capstone.domain.mobility.analysis.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.mobility.analysis.visitedregion.repository.VisitedRegionRepository;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.region.entity.Region;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeeklyStatisticsServiceTest {

    @Mock
    private DayRouteRepository dayRouteRepository;

    @Mock
    private VisitedRegionRepository visitedRegionRepository;

    @Test
    void 최근_7일_통계를_집계한다() {
        WeeklyStatisticsService weeklyStatisticsService = new WeeklyStatisticsService(
            dayRouteRepository, visitedRegionRepository);
        User user = createUser();
        DayRoute first = createDayRoute(user, LocalDate.of(2026, 5, 7),
            Instant.parse("2026-05-07T00:00:00Z"), Instant.parse("2026-05-07T14:00:00Z"), 1,
            18_000);
        DayRoute second = createDayRoute(user, LocalDate.of(2026, 5, 9),
            Instant.parse("2026-05-09T00:30:00Z"), Instant.parse("2026-05-09T14:30:00Z"), 2,
            21_600);
        DayRoute third = createDayRoute(user, LocalDate.of(2026, 5, 13), null, null, 0, 0);

        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 7), LocalDate.of(2026, 5, 13)))
            .willReturn(List.of(first, second, third));
        given(visitedRegionRepository.findByDayRouteInOrderByTotalStaySecondsDesc(List.of(first,
            second, third)))
            .willReturn(List.of(
                VisitedRegion.builder().dayRoute(first).region(createRegion("성북구"))
                    .totalStaySeconds(7200).build(),
                VisitedRegion.builder().dayRoute(second).region(createRegion("성북구"))
                    .totalStaySeconds(3600).build(),
                VisitedRegion.builder().dayRoute(second).region(createRegion("강북구"))
                    .totalStaySeconds(1800).build()
            ));

        var response = weeklyStatisticsService.getWeeklyStatistics(1L,
            LocalDate.of(2026, 5, 13));

        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 5, 7));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 5, 13));
        assertThat(response.outingTime().average().displayText()).isEqualTo("09:15");
        assertThat(response.enterHomeTime().average().displayText()).isEqualTo("23:15");
        assertThat(response.totalOutingCount().average().displayText()).isEqualTo("1회");
        assertThat(response.totalOutingSeconds().average().displayText()).isEqualTo("3시간 40분");
        assertThat(response.visitedRegions().topRegions()).hasSize(2);
        assertThat(response.visitedRegions().topRegions().get(0).regionName()).isEqualTo("성북구");
    }

    private User createUser() {
        return User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname("tester")
            .profileImageUrl("https://example.com/profile.png")
            .build();
    }

    private DayRoute createDayRoute(User user, LocalDate date, Instant outingTime,
        Instant enterHomeTime, int totalOutingCount, long totalOutingSeconds) {
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(date)
            .build();
        if (outingTime != null) {
            dayRoute.markOuting(outingTime);
            for (int count = 1; count < totalOutingCount; count++) {
                dayRoute.markReturnedHome(enterHomeTime == null ? outingTime : enterHomeTime);
                dayRoute.markOuting(outingTime.plusSeconds(count));
            }
        }
        if (enterHomeTime != null) {
            dayRoute.markReturnedHome(enterHomeTime);
        }
        dayRoute.addOutingDurationSeconds(totalOutingSeconds);
        return dayRoute;
    }

    private Region createRegion(String dongName) {
        return Region.builder()
            .dongName(dongName)
            .build();
    }
}
