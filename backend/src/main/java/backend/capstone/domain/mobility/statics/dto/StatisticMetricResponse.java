package backend.capstone.domain.mobility.statics.dto;

import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record StatisticMetricResponse(
    @Schema(example = "OUTING_TIME", description = "통계 지표 타입")
    String metricType,

    @Schema(example = "WEEK", description = "조회 기간")
    StatisticPeriod period,

    @Schema(example = "2026-05-12", description = "조회 시작일")
    LocalDate startDate,

    @Schema(example = "2026-05-18", description = "조회 종료일")
    LocalDate endDate,

    @Schema(description = "조회 기간 전체 평균")
    StatisticMetricAverage average,

    @Schema(description = "막대 그래프 표시용 구간별 값")
    List<StatisticMetricBarItem> bars,

    @Schema(description = "현재 기간과 직전 기간 비교 하이라이트")
    StatisticMetricHighlight highlight
) {

    public record StatisticMetricAverage(
        @Schema(example = "552", description = "평균 외출시각을 KST 00:00부터 지난 분으로 환산한 값")
        Integer value,

        @Schema(example = "09:12", description = "평균 외출시각 표시 문자열")
        String displayText,

        @Schema(example = "5", description = "평균 계산에 사용된 기록 개수")
        int sampleSize
    ) {

    }

    public record StatisticMetricBarItem(
        @Schema(example = "월", description = "프론트 막대 그래프 라벨")
        String label,

        @Schema(example = "2026-05-12", description = "구간 시작일")
        LocalDate startDate,

        @Schema(example = "2026-05-12", description = "구간 종료일")
        LocalDate endDate,

        @Schema(example = "540", description = "구간 평균 외출시각을 KST 00:00부터 지난 분으로 환산한 값")
        Integer value,

        @Schema(example = "09:00", description = "구간 평균 외출시각 표시 문자열")
        String displayText,

        @Schema(example = "true", description = "구간에 평균 계산 가능한 값이 존재하는지 여부")
        boolean hasValue,

        @Schema(example = "1", description = "구간 평균 계산에 사용된 기록 개수")
        int sampleSize
    ) {

    }

    public record StatisticMetricHighlight(
        @Schema(example = "이번 주 외출", description = "하이라이트 제목")
        String title,

        @Schema(example = "이번 주 평균 외출 시간이 지난주보다 빨라졌어요.", description = "하이라이트 문구")
        String message,

        @Schema(description = "현재 기간 평균")
        HighlightMetricValue current,

        @Schema(description = "직전 기간 평균")
        HighlightMetricValue previous
    ) {

    }

    public record HighlightMetricValue(
        @Schema(example = "이번 주", description = "비교 항목 라벨")
        String label,

        @Schema(example = "510", description = "평균 외출시각을 KST 00:00부터 지난 분으로 환산한 값")
        Integer value,

        @Schema(example = "08:30", description = "평균 외출시각 표시 문자열")
        String displayText,

        @Schema(example = "5", description = "평균 계산에 사용된 기록 개수")
        int sampleSize
    ) {

    }
}
