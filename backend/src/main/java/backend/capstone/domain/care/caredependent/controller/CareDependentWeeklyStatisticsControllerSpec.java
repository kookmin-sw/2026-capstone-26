package backend.capstone.domain.care.caredependent.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.statics.dto.WeeklyStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "보호 대상자 주간 요약 API")
public interface CareDependentWeeklyStatisticsControllerSpec {

    @Operation(
        summary = "보호대상자 최근 7일 주간 요약 조회 API",
        description = """
            로그인한 보호자가 연결된 보호대상자의 최근 7일 주간 요약 통계를 조회합니다.<br>
            개인 사용자의 최근 7일 주간 요약 조회 API와 응답 형식이 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.<br>
            오늘 날짜는 서버의 Asia/Seoul 기준으로 계산합니다.
            """
    )
    WeeklyStatisticsResponse getDependentUserWeeklyStatistics(
        Long dependentUserId,
        UserPrincipal principal
    );
}
