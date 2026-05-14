package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record TimeMetricSection(
    @Schema(description = "기록된 dayRoute 기준 평균 시각")
    TimeMetricAverage average,

    @Schema(description = "최근 7일 일자별 시각 값. dayRoute가 없는 날짜도 null 값으로 포함됩니다.")
    List<TimeMetricDailyItem> sevenDays
) {

    public record TimeMetricAverage(
        @Schema(example = "552", description = "평균 시각을 KST 00:00부터 지난 분으로 환산한 값")
        Integer value,

        @Schema(example = "09:12", description = "평균 시각 표시 문자열")
        String displayText,

        @Schema(example = "5", description = "평균 계산에 사용된 기록 일수")
        int sampleSize
    ) {

    }

    public record TimeMetricDailyItem(
        @Schema(example = "2026-05-07", description = "일자")
        LocalDate date,

        @Schema(example = "true", description = "해당 날짜 dayRoute 존재 여부")
        boolean hasDayRoute,

        @Schema(example = "540", description = "시각을 KST 00:00부터 지난 분으로 환산한 값")
        Integer value,

        @Schema(example = "09:00", description = "시각 표시 문자열")
        String displayText
    ) {

    }
}
