package backend.capstone.domain.mobility.dayroute.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkBatchRequest;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkListResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

@Tag(name = "나의 지나온길 즐겨찾기 API")
public interface DayRouteBookmarkControllerSpec {

    @Operation(
        summary = "즐찾 토글 API"
    )
    DayRouteBookmarkResponse toggleBookmark(
        @Parameter(name = "date", example = "2026-01-01") LocalDate date,
        UserPrincipal principal
    );

    @Operation(
        summary = "즐찾 목록 조회 API",
        description = """
            북마크한 dayRoute가 date 내림차순으로 정렬되어 반환됩니다.<br>
            방문 지역 목록은 체류 시간 내림차순으로 정렬되어 반환됩니다.<br>
            제목이 없으면 null, 방문 동네가 없으면 빈 배열이 반환됩니다.<br>
            방문 지역 목록은 체류 시간 내림차순으로 정렬되어 반환됩니다.<br><br>
            dayRouteCount는 현재 응답에 포함된 dayRoute 개수입니다.<br>
            hasNext가 true이면 다음 페이지가 존재한다는 뜻이며, nextCursorDate를 다음 요청의 cursorDate로 전달하면 됩니다.<br>
            hasNext가 false이면 마지막 페이지이고 nextCursorDate는 null로 반환됩니다.<br>
            """
    )
    DayRouteBookmarkListResponse getBookmarkedDayRoutes(
        @Parameter(name = "cursorDate") LocalDate cursorDate,
        @Parameter(name = "size") @Min(1) @Max(100) int size,
        UserPrincipal principal
    );

    @Operation(
        summary = "복수 즐찾 API",
        description = """
            요청된 날짜들의 dayRoute를 isBookmarked를 모두 true로 설정합니다.<br>
            만약 요청된 날짜의 dayRoute가 아직 생성되지 않았다면 생성 후 즐찾 설정을 합니다.
            이미 북마크된 dayRoute는 그대로 유지됩니다.<br>
            요청 바디에 빈 배열을 보내면 에러 메시지가 반환됩니다.<br>
            """
    )
    void bookmarkDayRoutes(UserPrincipal principal, @Valid DayRouteBookmarkBatchRequest request);
}
