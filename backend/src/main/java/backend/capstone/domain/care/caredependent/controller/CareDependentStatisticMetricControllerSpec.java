package backend.capstone.domain.care.caredependent.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.statics.dto.StatisticMetricResponse;
import backend.capstone.domain.mobility.statics.dto.VisitStatisticsResponse;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "보호대상자 지표별 상세 통계 API")
public interface CareDependentStatisticMetricControllerSpec {

    @Operation(
        summary = "보호대상자 외출시각 상세 통계 조회 API",
        description = """
            로그인한 보호자가 연결된 보호대상자의 외출시각 상세 통계를 조회합니다.<br>
            응답 형식과 계산 방식은 사용자의 외출시각 상세 통계 조회 API와 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.
            """
    )
    StatisticMetricResponse getDependentUserOutingTimeMetric(
        Long dependentUserId,
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );

    @Operation(
        summary = "보호대상자 귀가시각 상세 통계 조회 API",
        description = """
            로그인한 보호자가 연결된 보호대상자의 귀가시각 상세 통계를 조회합니다.<br>
            응답 형식과 계산 방식은 사용자의 귀가시각 상세 통계 조회 API와 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.
            """
    )
    StatisticMetricResponse getDependentUserEnterHomeTimeMetric(
        Long dependentUserId,
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );

    @Operation(
        summary = "보호대상자 총 외출시간 상세 통계 조회 API",
        description = """
            로그인한 보호자가 연결된 보호대상자의 총 외출시간 상세 통계를 조회합니다.<br>
            응답 형식과 계산 방식은 사용자의 총 외출시간 상세 통계 조회 API와 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.
            """
    )
    StatisticMetricResponse getDependentUserTotalOutingSecondsMetric(
        Long dependentUserId,
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );

    @Operation(
        summary = "보호대상자 외출횟수 상세 통계 조회 API",
        description = """
            로그인한 보호자가 연결된 보호대상자의 외출횟수 상세 통계를 조회합니다.<br>
            응답 형식과 계산 방식은 사용자의 외출횟수 상세 통계 조회 API와 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.
            """
    )
    StatisticMetricResponse getDependentUserTotalOutingCountMetric(
        Long dependentUserId,
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );

    @Operation(
        summary = "보호대상자 방문 동네 및 장소 통계 조회 API",
        description = """
            로그인한 보호자가 연결된 보호대상자의 방문 동네 및 장소 통계를 조회합니다.<br>
            응답 형식과 계산 방식은 사용자의 방문 동네 및 장소 통계 조회 API와 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.
            """
    )
    VisitStatisticsResponse getDependentUserVisitStatistics(
        Long dependentUserId,
        @Parameter(example = "WEEK") StatisticPeriod period,
        UserPrincipal principal
    );
}
