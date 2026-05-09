package backend.capstone.domain.mobility.dayroute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record DayRouteBookmarkListResponse(
    @Schema(
        example = "2",
        description = "현재 응답에 포함된 북마크 dayRoute 개수입니다."
    )
    int dayRouteCount,

    @Schema(
        example = "true",
        description = "다음 페이지가 존재하면 true, 마지막 페이지면 false입니다."
    )
    boolean hasNext,

    @Schema(
        example = "2026-05-07",
        description = "다음 페이지 조회용 커서 날짜입니다. hasNext가 false이면 null입니다."
    )
    LocalDate nextCursorDate,

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
