package backend.capstone.domain.care.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.dto.CareDependentUserListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "보호 관계 API")
public interface CareControllerSpec {

    @Operation(
        summary = "보호대상자 목록 및 최신 좌표 조회 API",
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
            로그인한 보호자가 보호 대상 위치 변경 이벤트를 실시간으로 구독합니다.<br>
            다른 api와 다르게 한 번 받고 끝나는 1회성 응답이 아니라 연결이 유지되어 서버로부터 실시간으로 응답이 오는 sse 스트림입니다.<br>
            클라이언트가 실시간 위치 추적 화면을 닫으면 이 연결을 종료해주세요.<br>
            연결이 성공하면 아래 메시지가 이벤트 스트림으로 전송됩니다.<br>
            ```
            event:connected
            data:보호 대상 위치 SSE 연결이 생성되었습니다.
            ```
            <br>
            서버는 30초마다 heartbeat 이벤트를 보내서 연결이 살아있음을 유지하는 신호를 보냅니다. 비지니스적으로 의미있는 데이터는 아닙니다. 형식은 다음과 같습니다.
            ```
            event:heartbeat
            data:keep-alive
            ```
            <br>
            보호대상자가 좌표 업로드를 갱신한 경우엔 아래 이벤트가 발행됩니다.
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
}
