package backend.capstone.domain.care.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.dto.CareDependentUserListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}
