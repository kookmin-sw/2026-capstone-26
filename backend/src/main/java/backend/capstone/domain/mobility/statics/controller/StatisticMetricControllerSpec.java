package backend.capstone.domain.mobility.statics.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.statics.dto.StatisticMetricResponse;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "지표 상세 통계 API")
public interface StatisticMetricControllerSpec {

    @Operation(
        summary = "외출시각 상세 통계 조회 API",
        description = """
            외출시각 지표를 기간별 막대 그래프 데이터로 반환합니다.<br>
            <br>
            <b>기간별 막대 개수</b><br>
            - WEEK: 오늘 포함 최근 7일을 일별 7개 막대로 반환합니다.<br>
            - MONTH: 오늘 포함 최근 30일을 일별 30개 막대로 반환합니다.<br>
            - SIX_MONTHS: 현재 월 포함 최근 6개월을 월별 6개 막대로 반환합니다.<br>
            - YEAR: 현재 월 포함 최근 12개월을 월별 12개 막대로 반환합니다.<br>
            <br>
            
            <b>value/displayText 의미</b><br>
            - value는 평균 외출시각을 KST 00:00부터 지난 분으로 환산한 값입니다. 예: 09:12 = 552<br>
            - displayText는 value를 HH:mm 형식으로 변환한 값입니다.<br>
            - 평균은 outingTime이 기록된 dayRoute만 대상으로 계산합니다. outingTime이 null인 dayRoute는 제외됩니다.<br>
            - 평균 계산 결과는 반올림된 정수 분 단위입니다.<br>
            - sampleSize는 평균 계산에 실제 사용된 outingTime 기록 개수입니다.<br>
            <br>
            
            <b>null 응답 조건</b><br>
            - 조회 기간 전체에 평균 계산 가능한 outingTime이 없으면 average.value와 average.displayText는 null이고 sampleSize는 0입니다.<br>
            - 특정 막대 구간에 평균 계산 가능한 outingTime이 없으면 해당 bar.value와 bar.displayText는 null, hasValue는 false, sampleSize는 0입니다.<br>
            - highlight 객체는 모든 period에서 응답합니다.<br>
            - 다만 highlight.current 또는 highlight.previous에 평균 계산 가능한 outingTime이 없으면 해당 value와 displayText는 null이고 sampleSize는 0입니다.<br>
            <br>
            
            <b>하이라이트 비교 범위</b><br>
            - WEEK: 이번 주(최근 7일)와 지난주(직전 7일)를 비교합니다.<br>
            - MONTH: 이번 달(최근 30일)과 지난달(직전 30일)을 비교합니다.<br>
            - SIX_MONTHS: 최근 6개월과 이전 6개월을 비교합니다.<br>
            - YEAR: 최근 1년과 이전 1년을 비교합니다.<br>
            <br>
            
            <b>하이라이트 문구 규칙</b><br>
            - 현재 기간 또는 직전 기간 중 하나라도 평균 계산 가능한 outingTime이 없으면 "비교할 기록이 부족해요." 문구를 반환합니다.<br>
            - 현재 기간 평균 value가 직전 기간 평균 value보다 작으면 더 이른 시간대이므로 "빨라졌어요." 문구를 반환합니다.<br>
            - 현재 기간 평균 value가 직전 기간 평균 value보다 크면 더 늦은 시간대이므로 "늦어졌어요." 문구를 반환합니다.<br>
            - 두 평균 value가 같으면 "같아요." 문구를 반환합니다.
            """
    )
    StatisticMetricResponse getOutingTimeMetric(
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );

    @Operation(
        summary = "귀가시각 상세 통계 조회 API",
        description = """
            귀가시각 지표를 기간별 막대 그래프 데이터로 반환합니다.<br>
            기간별 막대 개수, 평균 계산 방식, null 응답 조건, 하이라이트 비교 범위는 외출시각 상세 통계와 동일합니다.<br>
            평균은 enterHomeTime이 기록된 dayRoute만 대상으로 계산합니다. enterHomeTime이 null인 dayRoute는 제외됩니다.<br>
            value는 평균 귀가시각을 KST 00:00부터 지난 분으로 환산한 값입니다. 예: 23:15 = 1395<br>
            displayText는 value를 HH:mm 형식으로 변환한 값입니다.<br>
            하이라이트 문구는 현재 기간과 직전 기간의 평균 귀가시각을 비교해 기록 부족, 빨라짐, 늦어짐, 같음 중 하나로 반환합니다.
            """
    )
    StatisticMetricResponse getEnterHomeTimeMetric(
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );

    @Operation(
        summary = "총 외출시간 상세 통계 조회 API",
        description = """
            총 외출시간 지표를 기간별 막대 그래프 데이터로 반환합니다.<br>
            기간별 막대 개수와 하이라이트 비교 범위는 외출시각 상세 통계와 동일합니다.<br>
            평균은 dayRoute가 생성된 날짜의 totalOutingSeconds를 대상으로 계산합니다.<br>
            totalOutingSeconds가 0이어도 dayRoute가 있으면 평균 계산에 포함됩니다.<br>
            value는 평균 총 외출시간을 초 단위로 환산한 값입니다. 예: 12600 = 3시간 30분<br>
            displayText는 value를 "n시간 n분" 형식으로 변환한 값입니다.<br>
            조회 기간 또는 특정 막대 구간에 dayRoute가 없으면 value와 displayText는 null이고 sampleSize는 0입니다.<br>
            하이라이트 문구는 현재 기간과 직전 기간의 평균 총 외출시간을 비교해 기록 부족, 줄어듦, 늘어남, 같음 중 하나로 반환합니다.
            """
    )
    StatisticMetricResponse getTotalOutingSecondsMetric(
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );
}
