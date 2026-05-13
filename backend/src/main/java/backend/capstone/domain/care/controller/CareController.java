package backend.capstone.domain.care.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.dto.CareDayRouteDetailResponse;
import backend.capstone.domain.care.dto.CareDependentUserListResponse;
import backend.capstone.domain.care.service.CareDayRouteService;
import backend.capstone.domain.care.service.CareDependentUserService;
import java.time.LocalDate;
import backend.capstone.domain.care.sse.registry.CareSseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/care")
public class CareController implements CareControllerSpec {

    private final CareDayRouteService careDayRouteService;
    private final CareDependentUserService careDependentUserService;
    private final CareSseEmitterRegistry careSseEmitterRegistry;

    @Override
    @GetMapping("/dependents")
    public CareDependentUserListResponse getDependentUsers(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUsers(principal.userId());
    }

    @Override
    @GetMapping(value = "/dependents/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeDependentLocation(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careSseEmitterRegistry.register(principal.userId());
    }

    @Override
    @GetMapping("/dependents/{dependentUserId}/day-routes/{date}")
    public CareDayRouteDetailResponse getDependentUserDayRouteDetail(
        @PathVariable("dependentUserId") Long dependentUserId,
        @PathVariable("date") LocalDate date,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDayRouteService.getDependentUserDayRouteDetail(
            principal.userId(), dependentUserId, date);
    }
}
