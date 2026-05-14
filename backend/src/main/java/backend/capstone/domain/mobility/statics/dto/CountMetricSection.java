package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record CountMetricSection(
    @Schema(description = "기록된 dayRoute 기준 평균 외출 횟수")
    CountMetricAverage average,

    @Schema(description = "최근 7일 일자별 외출 횟수. dayRoute가 없는 날짜도 null 값으로 포함됩니다.")
    List<CountMetricDailyItem> sevenDays
) {

    public record CountMetricAverage(
        @Schema(example = "1.3", description = "평균 외출 횟수")
        Double value,

        @Schema(example = "1.3회", description = "평균 외출 횟수 표시 문자열")
        String displayText,

        @Schema(example = "7", description = "평균 계산에 사용된 기록 일수")
        int sampleSize
    ) {

    }

    public record CountMetricDailyItem(
        @Schema(example = "2026-05-07", description = "일자")
        LocalDate date,

        @Schema(example = "true", description = "해당 날짜 dayRoute 존재 여부")
        boolean hasDayRoute,

        @Schema(example = "1", description = "해당 날짜 외출 횟수")
        Integer value,

        @Schema(example = "1회", description = "외출 횟수 표시 문자열")
        String displayText
    ) {

    }
}
