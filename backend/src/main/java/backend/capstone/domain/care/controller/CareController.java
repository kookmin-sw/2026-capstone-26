package backend.capstone.domain.care.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.dto.CareDependentUserListResponse;
import backend.capstone.domain.care.service.CareDependentUserService;
import backend.capstone.domain.care.service.CareSseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/care")
public class CareController implements CareControllerSpec {

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
}
