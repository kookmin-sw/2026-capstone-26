package backend.capstone.domain.mobility.dayroute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record DayRouteBookmarkListResponse(
    @Schema(example = "2", description = "북마크한 dayRoute 개수")
    int dayRouteCount,

    @Schema(description = "북마크한 dayRoute 목록")
    List<DayRouteBookmarkItem> dayRoutes
) {

    @Builder
    public record DayRouteBookmarkItem(
        @Schema(example = "2026-05-07")
        LocalDate date,

        @Schema(example = "한강 산책")
        String title,

        @Schema(description = "방문 지역 목록")
        List<String> visitedRegions
    ) {

    }
}
