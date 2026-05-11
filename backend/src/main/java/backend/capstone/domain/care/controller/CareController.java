package backend.capstone.domain.care.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.dto.CareDependentUserListResponse;
import backend.capstone.domain.care.service.CareDependentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/care")
public class CareController implements CareControllerSpec {

    private final CareDependentUserService careDependentUserService;

    @Override
    @GetMapping("/dependents")
    public CareDependentUserListResponse getDependentUsers(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careDependentUserService.getDependentUsers(principal.userId());
    }
}
