package backend.capstone.domain.mobility.statics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
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

@ExtendWith(MockitoExtension.class)
class StatisticMetricServiceTest {

    @InjectMocks
    private StatisticMetricService statisticMetricService;

    @Mock
    private DayRouteRepository dayRouteRepository;

    @Test
    void 일주일_외출시각_상세_통계는_일별_7개_막대를_반환한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        User user = createUser();
        DayRoute monday = createDayRoute(user, LocalDate.of(2026, 5, 18),
            Instant.parse("2026-05-18T00:12:00Z"));
        DayRoute sunday = createDayRoute(user, LocalDate.of(2026, 5, 17),
            Instant.parse("2026-05-17T00:42:00Z"));

        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 12), LocalDate.of(2026, 5, 18)))
            .willReturn(List.of(sunday, monday));
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 11)))
            .willReturn(List.of(createDayRoute(user, LocalDate.of(2026, 5, 11),
                Instant.parse("2026-05-11T01:00:00Z"))));

        var response = statisticMetricService.getOutingTimeMetric(1L, StatisticPeriod.WEEK, today);

        assertThat(response.metricType()).isEqualTo("OUTING_TIME");
        assertThat(response.period()).isEqualTo(StatisticPeriod.WEEK);
        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 5, 12));
        assertThat(response.endDate()).isEqualTo(today);
        assertThat(response.average().value()).isEqualTo(567);
        assertThat(response.average().displayText()).isEqualTo("09:27");
        assertThat(response.average().sampleSize()).isEqualTo(2);
        assertThat(response.bars()).hasSize(7);
        assertThat(response.bars()).extracting(bar -> bar.label())
            .containsExactly("화", "수", "목", "금", "토", "일", "월");
        assertThat(response.bars().get(5).value()).isEqualTo(582);
        assertThat(response.bars().get(5).displayText()).isEqualTo("09:42");
        assertThat(response.bars().get(6).value()).isEqualTo(552);
        assertThat(response.bars().get(6).displayText()).isEqualTo("09:12");
        assertThat(response.highlight()).isNotNull();
        assertThat(response.highlight().title()).isEqualTo("이번 주 외출");
        assertThat(response.highlight().message()).isEqualTo("이번 주 평균 외출 시간이 지난주보다 빨라졌어요.");
        assertThat(response.highlight().current().label()).isEqualTo("이번 주");
        assertThat(response.highlight().current().value()).isEqualTo(567);
        assertThat(response.highlight().current().displayText()).isEqualTo("09:27");
        assertThat(response.highlight().current().sampleSize()).isEqualTo(2);
        assertThat(response.highlight().previous().label()).isEqualTo("지난주");
        assertThat(response.highlight().previous().value()).isEqualTo(600);
        assertThat(response.highlight().previous().displayText()).isEqualTo("10:00");
        assertThat(response.highlight().previous().sampleSize()).isEqualTo(1);
    }

    @Test
    void 일주일_귀가시각_상세_통계는_enterHomeTime_기준으로_계산한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        User user = createUser();
        DayRoute monday = createReturnedHomeDayRoute(user, LocalDate.of(2026, 5, 18),
            Instant.parse("2026-05-18T14:15:00Z"));
        DayRoute sunday = createReturnedHomeDayRoute(user, LocalDate.of(2026, 5, 17),
            Instant.parse("2026-05-17T14:45:00Z"));

        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 12), LocalDate.of(2026, 5, 18)))
            .willReturn(List.of(sunday, monday));
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 11)))
            .willReturn(List.of(createReturnedHomeDayRoute(user, LocalDate.of(2026, 5, 11),
                Instant.parse("2026-05-11T13:00:00Z"))));

        var response = statisticMetricService.getEnterHomeTimeMetric(1L, StatisticPeriod.WEEK,
            today);

        assertThat(response.metricType()).isEqualTo("ENTER_HOME_TIME");
        assertThat(response.period()).isEqualTo(StatisticPeriod.WEEK);
        assertThat(response.average().value()).isEqualTo(1410);
        assertThat(response.average().displayText()).isEqualTo("23:30");
        assertThat(response.average().sampleSize()).isEqualTo(2);
        assertThat(response.bars()).hasSize(7);
        assertThat(response.bars().get(5).value()).isEqualTo(1425);
        assertThat(response.bars().get(5).displayText()).isEqualTo("23:45");
        assertThat(response.bars().get(6).value()).isEqualTo(1395);
        assertThat(response.bars().get(6).displayText()).isEqualTo("23:15");
        assertThat(response.highlight().title()).isEqualTo("이번 주 귀가");
        assertThat(response.highlight().message()).isEqualTo("이번 주 평균 귀가 시각이 지난주보다 늦어졌어요.");
        assertThat(response.highlight().current().label()).isEqualTo("이번 주");
        assertThat(response.highlight().previous().label()).isEqualTo("지난주");
        assertThat(response.highlight().previous().value()).isEqualTo(1320);
        assertThat(response.highlight().previous().displayText()).isEqualTo("22:00");
    }

    @Test
    void 일주일_총외출시간_상세_통계는_totalOutingSeconds_기준으로_계산한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        User user = createUser();
        DayRoute monday = createTotalOutingSecondsDayRoute(user, LocalDate.of(2026, 5, 18),
            7200L);
        DayRoute sunday = createTotalOutingSecondsDayRoute(user, LocalDate.of(2026, 5, 17),
            10800L);

        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 12), LocalDate.of(2026, 5, 18)))
            .willReturn(List.of(sunday, monday));
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 11)))
            .willReturn(List.of(createTotalOutingSecondsDayRoute(user, LocalDate.of(2026, 5, 11),
                14400L)));

        var response = statisticMetricService.getTotalOutingSecondsMetric(1L,
            StatisticPeriod.WEEK, today);

        assertThat(response.metricType()).isEqualTo("TOTAL_OUTING_SECONDS");
        assertThat(response.period()).isEqualTo(StatisticPeriod.WEEK);
        assertThat(response.average().value()).isEqualTo(9000);
        assertThat(response.average().displayText()).isEqualTo("2시간 30분");
        assertThat(response.average().sampleSize()).isEqualTo(2);
        assertThat(response.bars()).hasSize(7);
        assertThat(response.bars().get(5).value()).isEqualTo(10800);
        assertThat(response.bars().get(5).displayText()).isEqualTo("3시간");
        assertThat(response.bars().get(6).value()).isEqualTo(7200);
        assertThat(response.bars().get(6).displayText()).isEqualTo("2시간");
        assertThat(response.highlight().title()).isEqualTo("이번 주 외출시간");
        assertThat(response.highlight().message()).isEqualTo("이번 주 평균 외출시간이 지난주보다 줄었어요.");
        assertThat(response.highlight().current().value()).isEqualTo(9000);
        assertThat(response.highlight().previous().value()).isEqualTo(14400);
        assertThat(response.highlight().previous().displayText()).isEqualTo("4시간");
    }

    @Test
    void 총외출시간은_외출기록이_없으면_평균에서_제외한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        User user = createUser();
        DayRoute monday = createDayRouteWithoutOutingRecord(user, LocalDate.of(2026, 5, 18));

        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 12), LocalDate.of(2026, 5, 18)))
            .willReturn(List.of(monday));
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 11)))
            .willReturn(List.of());

        var response = statisticMetricService.getTotalOutingSecondsMetric(1L,
            StatisticPeriod.WEEK, today);

        assertThat(response.average().value()).isNull();
        assertThat(response.average().displayText()).isNull();
        assertThat(response.average().sampleSize()).isZero();
        assertThat(response.bars().get(6).value()).isNull();
        assertThat(response.bars().get(6).displayText()).isNull();
        assertThat(response.bars().get(6).hasValue()).isFalse();
        assertThat(response.bars().get(6).sampleSize()).isZero();
        assertThat(response.highlight().current().value()).isNull();
        assertThat(response.highlight().previous().value()).isNull();
        assertThat(response.highlight().message()).isEqualTo("이번 주 평균 외출시간을 지난주와 비교할 기록이 부족해요.");
    }

    @Test
    void 일주일_외출횟수_상세_통계는_totalOutingCount_기준으로_계산한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        User user = createUser();
        DayRoute monday = createTotalOutingCountDayRoute(user, LocalDate.of(2026, 5, 18), 2);
        DayRoute sunday = createTotalOutingCountDayRoute(user, LocalDate.of(2026, 5, 17), 4);

        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 12), LocalDate.of(2026, 5, 18)))
            .willReturn(List.of(sunday, monday));
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 11)))
            .willReturn(List.of(createTotalOutingCountDayRoute(user, LocalDate.of(2026, 5, 11),
                5)));

        var response = statisticMetricService.getTotalOutingCountMetric(1L,
            StatisticPeriod.WEEK, today);

        assertThat(response.metricType()).isEqualTo("TOTAL_OUTING_COUNT");
        assertThat(response.period()).isEqualTo(StatisticPeriod.WEEK);
        assertThat(response.average().value()).isEqualTo(3.0);
        assertThat(response.average().displayText()).isEqualTo("3.0회");
        assertThat(response.average().sampleSize()).isEqualTo(2);
        assertThat(response.bars()).hasSize(7);
        assertThat(response.bars().get(5).value()).isEqualTo(4.0);
        assertThat(response.bars().get(5).displayText()).isEqualTo("4.0회");
        assertThat(response.bars().get(6).value()).isEqualTo(2.0);
        assertThat(response.bars().get(6).displayText()).isEqualTo("2.0회");
        assertThat(response.highlight().title()).isEqualTo("이번 주 외출횟수");
        assertThat(response.highlight().message()).isEqualTo("이번 주 평균 외출횟수가 지난주보다 줄었어요.");
        assertThat(response.highlight().current().value()).isEqualTo(3.0);
        assertThat(response.highlight().previous().value()).isEqualTo(5.0);
        assertThat(response.highlight().previous().displayText()).isEqualTo("5.0회");
    }

    @Test
    void 외출횟수는_외출기록이_없으면_평균에서_제외한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        User user = createUser();
        DayRoute monday = createDayRouteWithoutOutingRecord(user, LocalDate.of(2026, 5, 18));

        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 12), LocalDate.of(2026, 5, 18)))
            .willReturn(List.of(monday));
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 11)))
            .willReturn(List.of());

        var response = statisticMetricService.getTotalOutingCountMetric(1L,
            StatisticPeriod.WEEK, today);

        assertThat(response.average().value()).isNull();
        assertThat(response.average().displayText()).isNull();
        assertThat(response.average().sampleSize()).isZero();
        assertThat(response.bars().get(6).value()).isNull();
        assertThat(response.bars().get(6).displayText()).isNull();
        assertThat(response.bars().get(6).hasValue()).isFalse();
        assertThat(response.bars().get(6).sampleSize()).isZero();
        assertThat(response.highlight().current().value()).isNull();
        assertThat(response.highlight().previous().value()).isNull();
        assertThat(response.highlight().message()).isEqualTo("이번 주 평균 외출횟수를 지난주와 비교할 기록이 부족해요.");
    }

    @Test
    void 한달_외출시각_상세_통계는_일별_30개_막대를_반환한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 4, 19), today))
            .willReturn(List.of());
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2026, 3, 20), LocalDate.of(2026, 4, 18)))
            .willReturn(List.of());

        var response = statisticMetricService.getOutingTimeMetric(1L, StatisticPeriod.MONTH,
            today);

        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 4, 19));
        assertThat(response.bars()).hasSize(30);
        assertThat(response.average().value()).isNull();
        assertThat(response.average().displayText()).isNull();
        assertThat(response.average().sampleSize()).isZero();
        assertThat(response.highlight()).isNotNull();
        assertThat(response.highlight().title()).isEqualTo("이번 달 외출");
        assertThat(response.highlight().message()).isEqualTo("이번 달 평균 외출 시간을 지난달과 비교할 기록이 부족해요.");
        assertThat(response.highlight().current().label()).isEqualTo("이번 달");
        assertThat(response.highlight().previous().label()).isEqualTo("지난달");
    }

    @Test
    void 육개월과_일년_외출시각_상세_통계는_월별_막대를_반환한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2025, 12, 1), today))
            .willReturn(List.of());
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2025, 6, 1), LocalDate.of(2025, 11, 30)))
            .willReturn(List.of());
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2025, 6, 1), today))
            .willReturn(List.of());
        given(dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(1L,
            LocalDate.of(2024, 6, 1), LocalDate.of(2025, 5, 31)))
            .willReturn(List.of());

        var sixMonthsResponse = statisticMetricService.getOutingTimeMetric(1L,
            StatisticPeriod.SIX_MONTHS, today);
        var yearResponse = statisticMetricService.getOutingTimeMetric(1L, StatisticPeriod.YEAR,
            today);

        assertThat(sixMonthsResponse.bars()).hasSize(6);
        assertThat(sixMonthsResponse.startDate()).isEqualTo(LocalDate.of(2025, 12, 1));
        assertThat(sixMonthsResponse.bars()).extracting(bar -> bar.label())
            .containsExactly("12월", "1월", "2월", "3월", "4월", "5월");
        assertThat(sixMonthsResponse.highlight()).isNotNull();
        assertThat(sixMonthsResponse.highlight().title()).isEqualTo("최근 6개월 외출");
        assertThat(sixMonthsResponse.highlight().current().label()).isEqualTo("최근 6개월");
        assertThat(sixMonthsResponse.highlight().previous().label()).isEqualTo("이전 6개월");
        assertThat(yearResponse.bars()).hasSize(12);
        assertThat(yearResponse.startDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(yearResponse.highlight()).isNotNull();
        assertThat(yearResponse.highlight().title()).isEqualTo("최근 1년 외출");
        assertThat(yearResponse.highlight().current().label()).isEqualTo("최근 1년");
        assertThat(yearResponse.highlight().previous().label()).isEqualTo("이전 1년");
    }

    private User createUser() {
        return User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname("tester")
            .profileImageUrl("https://example.com/profile.png")
            .build();
    }

    private DayRoute createDayRoute(User user, LocalDate date, Instant outingTime) {
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(date)
            .build();
        dayRoute.markOuting(outingTime);
        return dayRoute;
    }

    private DayRoute createReturnedHomeDayRoute(User user, LocalDate date, Instant enterHomeTime) {
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(date)
            .build();
        dayRoute.markReturnedHome(enterHomeTime);
        return dayRoute;
    }

    private DayRoute createTotalOutingSecondsDayRoute(User user, LocalDate date,
        long totalOutingSeconds) {
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(date)
            .build();
        dayRoute.markOuting(Instant.parse("2026-05-18T00:00:00Z"));
        dayRoute.addOutingDurationSeconds(totalOutingSeconds);
        return dayRoute;
    }

    private DayRoute createTotalOutingCountDayRoute(User user, LocalDate date,
        int totalOutingCount) {
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(date)
            .build();
        for (int count = 0; count < totalOutingCount; count++) {
            dayRoute.markOuting(Instant.parse("2026-05-18T00:00:00Z"));
        }
        return dayRoute;
    }

    private DayRoute createDayRouteWithoutOutingRecord(User user, LocalDate date) {
        return DayRoute.builder()
            .user(user)
            .date(date)
            .build();
    }
}
