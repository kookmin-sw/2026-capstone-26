package backend.capstone.domain.mobility.dayroute.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkListResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;

@Tag(name = "나의 지나온길 즐겨찾기 API")
public interface DayRouteBookmarkControllerSpec {

    @Operation(
        summary = "즐겨찾기 토글 API"
    )
    DayRouteBookmarkResponse toggleBookmark(
        @Parameter(name = "date", example = "2026-01-01") LocalDate date,
        UserPrincipal principal
    );

    @Operation(
        summary = "북마크한 dayRoute 목록 조회 API"
    )
    DayRouteBookmarkListResponse getBookmarkedDayRoutes(UserPrincipal principal);
}
