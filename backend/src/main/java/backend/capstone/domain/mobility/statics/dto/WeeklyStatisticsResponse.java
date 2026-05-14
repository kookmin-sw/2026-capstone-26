package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record WeeklyStatisticsResponse(
    @Schema(example = "2026-05-07", description = "최근 7일 통계 시작일")
    LocalDate startDate,

    @Schema(example = "2026-05-13", description = "최근 7일 통계 종료일")
    LocalDate endDate,

    @Schema(description = "주간 외출 시각 통계. value는 KST 기준 00:00부터 지난 분입니다.")
    TimeMetricSection outingTime,

    @Schema(description = "주간 귀가 시각 통계. value는 KST 기준 00:00부터 지난 분입니다.")
    TimeMetricSection enterHomeTime,

    @Schema(description = "주간 외출 횟수 통계")
    CountMetricSection totalOutingCount,

    @Schema(description = "주간 총 외출 시간 통계. value는 초 단위입니다.")
    DurationMetricSection totalOutingSeconds,

    @Schema(description = "최근 7일 누적 체류 시간 기준 자주 방문한 지역 통계")
    VisitedRegionsSection visitedRegions
) {
}
