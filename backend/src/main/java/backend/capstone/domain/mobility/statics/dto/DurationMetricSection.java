package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record DurationMetricSection(
    @Schema(description = "기록된 dayRoute 기준 평균 외출 시간")
    DurationMetricAverage average,

    @Schema(description = "최근 7일 일자별 총 외출 시간. dayRoute가 없는 날짜도 null 값으로 포함됩니다.")
    List<DurationMetricDailyItem> sevenDays
) {

    public record DurationMetricAverage(
        @Schema(example = "19800.5", description = "평균 외출 시간(초), 소수점 한 자리까지 반환")
        Double value,

        @Schema(example = "5시간 30분", description = "평균 외출 시간 표시 문자열")
        String displayText,

        @Schema(example = "7", description = "평균 계산에 사용된 기록 일수")
        int sampleSize
    ) {

    }

    public record DurationMetricDailyItem(
        @Schema(example = "2026-05-07", description = "일자")
        LocalDate date,

        @Schema(example = "true", description = "해당 날짜 dayRoute 존재 여부")
        boolean hasDayRoute,

        @Schema(example = "19800", description = "해당 날짜 총 외출 시간(초)")
        Long value,

        @Schema(example = "5시간 30분", description = "외출 시간 표시 문자열")
        String displayText
    ) {

    }
}
