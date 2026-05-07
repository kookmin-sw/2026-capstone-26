package backend.capstone.domain.mobility.dayroute.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkResponse;
import backend.capstone.domain.mobility.dayroute.facade.DayRouteFacade;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/day-routes")
public class DayRouteBookmarkController implements DayRouteBookmarkControllerSpec {

    private final DayRouteFacade dayRouteFacade;

    @Override
    @PatchMapping("/{date}/bookmark")
    public DayRouteBookmarkResponse toggleBookmark(
        @PathVariable("date") LocalDate date,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return dayRouteFacade.toggleBookmark(date, principal.userId());
    }

}
