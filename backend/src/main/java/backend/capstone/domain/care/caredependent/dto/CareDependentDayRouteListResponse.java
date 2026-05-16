package backend.capstone.domain.care.caredependent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CareDependentDayRouteListResponse(
    @Schema(example = "10", description = "현재 응답에 포함된 dayRoute 개수")
    int dayRouteCount,

    @Schema(example = "true", description = "다음 페이지가 존재하면 true")
    boolean hasNext,

    @Schema(example = "2026-05-07", description = "다음 페이지 조회용 커서 날짜. hasNext가 false이면 null")
    LocalDate nextCursorDate,

    @Schema(description = "보호대상자 dayRoute 목록")
    List<CareDependentDayRouteItem> dayRoutes
) {

    @Builder
    public record CareDependentDayRouteItem(
        @Schema(example = "2026-05-13", description = "dayRoute 날짜")
        LocalDate date,

        @Schema(example = "2026-05-13T09:12:00+09:00", description = "첫 외출 시각")
        OffsetDateTime outingTime,

        @Schema(example = "2026-05-13T23:15:00+09:00", description = "마지막 귀가 시각")
        OffsetDateTime enterHomeTime,

        @Schema(example = "2", description = "총 외출 횟수")
        int totalOutingCount
    ) {

    }
}
