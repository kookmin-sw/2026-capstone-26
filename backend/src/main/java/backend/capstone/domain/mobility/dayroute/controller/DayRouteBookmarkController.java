package backend.capstone.domain.mobility.dayroute.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkBatchRequest;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkListResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkResponse;
import backend.capstone.domain.mobility.dayroute.facade.DayRouteFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/day-routes")
public class DayRouteBookmarkController implements DayRouteBookmarkControllerSpec {

    private final DayRouteFacade dayRouteFacade;

    @Override
    @GetMapping("/bookmarks")
    public DayRouteBookmarkListResponse getBookmarkedDayRoutes(
        @RequestParam(value = "cursorDate", required = false) LocalDate cursorDate,
        @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) int size,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return dayRouteFacade.getBookmarkedDayRoutes(principal.userId(), cursorDate, size);
    }

    @Override
    @PatchMapping("/{date}/bookmark")
    public DayRouteBookmarkResponse toggleBookmark(
        @PathVariable("date") LocalDate date,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return dayRouteFacade.toggleBookmark(date, principal.userId());
    }

    @Override
    @PatchMapping("/bookmarks:batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bookmarkDayRoutes(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody DayRouteBookmarkBatchRequest request
    ) {
        dayRouteFacade.bookmarkDayRoutes(principal.userId(), request);
    }

}
