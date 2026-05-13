package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record WeeklyStatisticsResponse(
    @Schema(example = "2026-05-07", description = "최근 7일 통계 시작일")
    LocalDate startDate,

    @Schema(example = "2026-05-13", description = "최근 7일 통계 종료일")
    LocalDate endDate,

    TimeMetricSection outingTime,
    TimeMetricSection enterHomeTime,
    CountMetricSection totalOutingCount,
    DurationMetricSection totalOutingSeconds,
    VisitedRegionsSection visitedRegions
) {
}
