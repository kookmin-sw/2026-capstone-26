package backend.capstone.domain.care.caredependent.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.caredependent.service.CareDependentUserService;
import backend.capstone.domain.mobility.statics.dto.WeeklyStatisticsResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/care/dependents/{dependentUserId}/statics")
public class CareDependentWeeklyStatisticsController
    implements CareDependentWeeklyStatisticsControllerSpec {

    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final CareDependentUserService careDependentUserService;

    @Override
    @GetMapping("/weekly")
    public WeeklyStatisticsResponse getDependentUserWeeklyStatistics(
        @PathVariable("dependentUserId") Long dependentUserId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUserWeeklyStatistics(
            principal.userId(), dependentUserId, LocalDate.now(KST_ZONE_ID));
    }
}
