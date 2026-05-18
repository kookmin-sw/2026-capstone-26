package backend.capstone.domain.mobility.statics.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.statics.dto.WeeklyStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주간 요약 API")
public interface WeeklyStatisticsControllerSpec {

    @Operation(
        summary = "최근 7일 주간 요약 조회 API",
        description = """
            오늘 날짜를 기준으로 최근 7일간의 통계가 날짜 오름차순으로 반환합니다.<br>
            최근 7일간 생성된 dayRoute가 없다면 평균을 계산할 수치가 없어 각 필드의 `average`의 `value`와 `displayText`는 nulll이 됩니다. (이때 nulll이 아니라 0이나 "-" 같은 문자열을 받고 싶으면 얘기 바람)
                        
            - `hasDayRoute`: 해당 날짜에 데이터가 존재하는지 여부<br>
                - 만약 `hasDayRoute`가 false라면 해당 날짜에 데이터가 존재하지 않아 dayRoute 엔티티가 생성되지 않은 것입니다.<br>
                따라서 해당 날짜의 `value`와 `displayText`도 null이 반환되며 해당 날짜는 평균 계산에 사용되지 않습니다.<br><br>
            - `sampleSize`: 평균 계산에 사용된 기록 일수 (최근 7일 간 dayRoute가 생성된 날짜 수)<br><br>
            - `value`: 평균을 정량적인 값으로 환산한 값으로 막대 그래프를 표시할 때 정량적으로 판단할 수 있는 값입니다.<br><br>
              - `outingTime`과 `enterHomeTime`의 `value`는 시간을 분으로 환산한 값<br>
                `totalOutingSeconds.value`: 해당 날짜의 총 외출 시간을 초 단위로 계산한 값<br>
                `totalOutingCount.value`: 외출횟수를 소수점 한자리까지 계산한 값<br><br>
            - `topRegions`: 방문한 동네가 없으면 빈배열이 반환되며 최대 2개까지 반환됩니다.
            """
    )
    WeeklyStatisticsResponse getWeeklyStatistics(UserPrincipal principal);
}
