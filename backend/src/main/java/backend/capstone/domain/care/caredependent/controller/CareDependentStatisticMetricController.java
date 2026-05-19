package backend.capstone.domain.care.caredependent.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.caredependent.service.CareDependentUserService;
import backend.capstone.domain.mobility.statics.dto.StatisticMetricResponse;
import backend.capstone.domain.mobility.statics.dto.VisitStatisticsResponse;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/care/dependents/{dependentUserId}/statics/metrics")
public class CareDependentStatisticMetricController
    implements CareDependentStatisticMetricControllerSpec {

    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final CareDependentUserService careDependentUserService;

    @Override
    @GetMapping("/outing-time")
    public StatisticMetricResponse getDependentUserOutingTimeMetric(
        @PathVariable("dependentUserId") Long dependentUserId,
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUserOutingTimeMetric(
            principal.userId(), dependentUserId, period, LocalDate.now(KST_ZONE_ID));
    }

    @Override
    @GetMapping("/enter-home-time")
    public StatisticMetricResponse getDependentUserEnterHomeTimeMetric(
        @PathVariable("dependentUserId") Long dependentUserId,
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUserEnterHomeTimeMetric(
            principal.userId(), dependentUserId, period, LocalDate.now(KST_ZONE_ID));
    }

    @Override
    @GetMapping("/total-outing-seconds")
    public StatisticMetricResponse getDependentUserTotalOutingSecondsMetric(
        @PathVariable("dependentUserId") Long dependentUserId,
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUserTotalOutingSecondsMetric(
            principal.userId(), dependentUserId, period, LocalDate.now(KST_ZONE_ID));
    }

    @Override
    @GetMapping("/total-outing-count")
    public StatisticMetricResponse getDependentUserTotalOutingCountMetric(
        @PathVariable("dependentUserId") Long dependentUserId,
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUserTotalOutingCountMetric(
            principal.userId(), dependentUserId, period, LocalDate.now(KST_ZONE_ID));
    }

    @Override
    @GetMapping("/visits")
    public VisitStatisticsResponse getDependentUserVisitStatistics(
        @PathVariable("dependentUserId") Long dependentUserId,
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUserVisitStatistics(
            principal.userId(), dependentUserId, period, LocalDate.now(KST_ZONE_ID));
    }
}
