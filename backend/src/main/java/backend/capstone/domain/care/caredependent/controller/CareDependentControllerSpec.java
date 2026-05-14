package backend.capstone.domain.care.caredependent.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.caredependent.dto.CareDayRouteDetailResponse;
import backend.capstone.domain.care.caredependent.dto.CareDependentDayRouteListResponse;
import backend.capstone.domain.care.caredependent.dto.CareDependentUserListResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteSummaryResponse;
import backend.capstone.domain.mobility.place.dto.PlaceListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "함께 가는 길 API")
public interface CareDependentControllerSpec {

    @Operation(
        summary = "보호 대상자 목록 및 최신 좌표 조회 API",
        description = """
            로그인한 보호자의 모든 보호대상자를 조회합니다.<br>
            각 보호대상자의 닉네임, 프로필 이미지 URL, 최신 위치 정보를 함께 반환합니다.<br>
            최신 위치 정보가 없는 보호대상자의 latestGpsPoint는 null로 반환됩니다.
            """
    )
    CareDependentUserListResponse getDependentUsers(UserPrincipal principal);

    @Operation(
        summary = "실시간 위치를 받기 위한 SSE 구독 API",
        description = """
            로그인한 보호자가 보호 대상자의 좌표 업데이트를 실시간으로 받기 위해 구독하는 api입니다.<br>
            다른 api와 다르게 한 번 받고 끝나는 1회성 응답이 아니라 연결이 유지되어 서버로부터 메시지를 지속적으로 전달받을 수 있는 sse 스트림입니다.<br>
            클라이언트가 실시간 위치 추적 화면을 닫으면 이 연결을 종료해주세요.<br>
            연결이 성공하면 아래 메시지가 전송됩니다.<br>
            ```
            event:connected
            data: {"message":"보호 대상 위치 SSE 연결이 생성되었습니다."}
            ```
            <br>
            서버는 30초마다 heartbeat 이벤트를 보내서 연결이 살아있음을 유지하는 메시지를 보냅니다. 비지니스적으로 의미있는 데이터는 아닙니다. 형식은 다음과 같습니다.
            ```
            event:heartbeat
            data: {"message":"keep-alive"}
            ```
            <br>
            보호대상자가 좌표 업로드를 갱신한 경우엔 아래 메시지가 전송됩니다.
            ```
            event:location-updated
            data:{
                "dependentUserId":2,
                "latestGpsPoint":{
                    "latitude":90.0,
                    "longitude":180.0,
                    "recordedAt":"2026-05-11T20:56:45.991+09:00"
                }
            }
            ```
            """
    )
    SseEmitter subscribeDependentLocation(UserPrincipal principal);

    @Operation(
        summary = "보호대상자 지나온길 목록 조회 API",
        description = """
            로그인한 보호자가 연결된 보호대상자의 dayRoute 목록을 조회합니다.<br>
            쿼리 파라미터로 별도의 size 값을 안넘겨주면 default로 10개가 반환됩니다.<br>
            date 내림차순 커서 페이징 방식으로 반환합니다.<br>
            hasNext가 true이면 nextCursorDate를 다음 요청의 cursorDate로 전달하면 됩니다.<br>
            맨 처음 요청 시에는 cursorDate 값을 넘주지 않으며 그다음 요청부터는 cursorDate에 응답으로 받은 nextCursorDate 값을 넘겨주세요.
            """
    )
    CareDependentDayRouteListResponse getDependentUserDayRoutes(
        Long dependentUserId,
        LocalDate cursorDate,
        @Min(1) @Max(100) int size,
        UserPrincipal principal
    );

    @Operation(
        summary = "보호대상자 지나온길 조회 API",
        description = """
            로그인한 보호자가 보호대상자 개인 별 dayRoute를 조회합니다.<br>
            경로변수로 넘겨주는 보호 대상자 목록 조회 api에서 받은 `dependentUserId`를 경로 변수로 넘겨주세요.<br>
            응답에는 날짜, 총 이동거리, GPS 좌표 목록을 반환합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.<br>
            나의 지나온길 조회와 동일하게 해당 날짜에 기록된 데이터가 없으면 404에러가 반환됩니다.
            """
    )
    CareDayRouteDetailResponse getDependentUserDayRouteDetail(
        Long dependentUserId,
        @Schema(example = "2026-01-01") LocalDate date,
        UserPrincipal principal
    );

    @Operation(
        summary = "보호대상자 방문장소 목록 조회 API",
        description = """
            로그인한 보호자가 본인과 연결된 보호대상자의 특정 날짜 방문장소 목록을 조회합니다.<br>
            경로변수로 넘겨주는 보호 대상자 목록 조회 api에서 받은 `dependentUserId`를 경로 변수로 넘겨주세요.<br>
            보호대상자가 수기로 추가한 장소와 자동 저장 체류 장소 모두 반환됩니다.<br>
            응답 필드 구성은 사용자의 방문장소 목록 조회 API와 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.<br>
            해당 날짜 dayRoute가 없으면 404 에러가 반환되고,<br>
            dayRoute는 생성되었지만 기록된 방문장소가 없으면 places는 빈 배열이 반환됩니다.
            """
    )
    PlaceListResponse getDependentUserPlaces(
        Long dependentUserId,
        @Schema(example = "2026-01-01") LocalDate date,
        UserPrincipal principal
    );

    @Operation(
        summary = "보호대상자 하루 요약 조회 API",
        description = """
            로그인한 보호자가 본인과 연결된 보호대상자의 특정 날짜 하루 요약 정보를 조회합니다.<br>
            응답 필드 구성은 사용자의 하루 요약 조회 API와 동일합니다.<br>
            연결되지 않은 보호대상자를 조회하면 권한 오류가 발생합니다.<br>
            해당 날짜 dayRoute가 없으면 404 에러가 반환됩니다.
            """
    )
    DayRouteSummaryResponse getDependentUserDayRouteSummary(
        Long dependentUserId,
        @Schema(example = "2026-01-01") LocalDate date,
        UserPrincipal principal
    );
}
