package backend.capstone.domain.friend.carerelationshipinvite.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.friend.carerelationshipinvite.dto.CareRelationshipInviteAcceptRequest;
import backend.capstone.domain.friend.carerelationshipinvite.dto.CareRelationshipInviteLinkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "보호 관계 초대 API")
public interface CareRelationshipInviteControllerSpec {

    @Operation(
        summary = "보호 관계 초대 링크 생성 API",
        description = """
            로그인한 보호자의 보호 관계 초대 링크를 생성합니다.<br>
            응답으로 바로 공유할 수 있는 초대 링크 문자열을 반환합니다.
            """
    )
    CareRelationshipInviteLinkResponse createInviteLink(UserPrincipal principal);

    @Operation(
        summary = "보호 관계 초대 수락 API",
        description = """
            초대 링크에서 inviteCode를 추출해 호출하는 API입니다.<br>
            보호 대상자가 초대 코드를 사용해 보호 관계 초대를 수락합니다.<br>
            본인 초대 수락, 만료된 초대, 이미 연결된 관계인 경우에는 에러 메시지를 반환합니다.<br>
            보호 관계 생성이 완료되면 204 응답을 반환합니다.
            """
    )
    void acceptInvite(UserPrincipal principal, @Valid CareRelationshipInviteAcceptRequest request);
}
