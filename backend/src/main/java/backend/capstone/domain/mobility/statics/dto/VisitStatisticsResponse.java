package backend.capstone.domain.mobility.statics.dto;

import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record VisitStatisticsResponse(
    @Schema(example = "WEEK", description = "조회 기간")
    StatisticPeriod period,

    @Schema(example = "2026-05-12", description = "조회 시작일")
    LocalDate startDate,

    @Schema(example = "2026-05-18", description = "조회 종료일")
    LocalDate endDate,

    @Schema(description = "방문 동네 분포")
    VisitedRegionStatisticsSection visitedRegions,

    @Schema(description = "가장 많이 방문한 장소")
    PlaceStatisticsSection places
) {
}
