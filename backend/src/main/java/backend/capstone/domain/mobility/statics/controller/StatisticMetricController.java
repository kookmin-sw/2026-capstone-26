package backend.capstone.domain.mobility.statics.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.statics.dto.StatisticMetricResponse;
import backend.capstone.domain.mobility.statics.service.StatisticMetricService;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statics/metrics")
public class StatisticMetricController implements StatisticMetricControllerSpec {

    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final StatisticMetricService statisticMetricService;

    @Override
    @GetMapping("/outing-time")
    public StatisticMetricResponse getOutingTimeMetric(
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return statisticMetricService.getOutingTimeMetric(
            principal.userId(), period, LocalDate.now(KST_ZONE_ID));
    }

    @Override
    @GetMapping("/enter-home-time")
    public StatisticMetricResponse getEnterHomeTimeMetric(
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return statisticMetricService.getEnterHomeTimeMetric(
            principal.userId(), period, LocalDate.now(KST_ZONE_ID));
    }

    @Override
    @GetMapping("/total-outing-seconds")
    public StatisticMetricResponse getTotalOutingSecondsMetric(
        @RequestParam(value = "period", defaultValue = "WEEK") StatisticPeriod period,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return statisticMetricService.getTotalOutingSecondsMetric(
            principal.userId(), period, LocalDate.now(KST_ZONE_ID));
    }
}
