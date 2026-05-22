package backend.capstone.domain.mobility.statics.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.statics.dto.WeeklyStatisticsResponse;
import backend.capstone.domain.mobility.statics.service.WeeklyStatisticsService;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statics")
public class WeeklyStatisticsController implements WeeklyStatisticsControllerSpec {

    private final WeeklyStatisticsService weeklyStatisticsService;

    @Override
    @GetMapping("/weekly")
    public WeeklyStatisticsResponse getWeeklyStatistics(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return weeklyStatisticsService.getWeeklyStatistics(principal.userId(),
            LocalDate.now(ZoneId.of("Asia/Seoul")));
    }
}
