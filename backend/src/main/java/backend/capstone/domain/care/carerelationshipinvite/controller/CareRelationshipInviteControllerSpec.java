package backend.capstone.domain.care.carerelationshipinvite.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.care.carerelationshipinvite.dto.CareRelationshipInviteAcceptRequest;
import backend.capstone.domain.care.carerelationshipinvite.dto.CareRelationshipInviteLinkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "함께 가는 길 초대 API")
public interface CareRelationshipInviteControllerSpec {

    @Operation(
        summary = "초대 링크 생성 API",
        description = """
            초대 링크를 생성하여 반환합니다.
            """
    )
    CareRelationshipInviteLinkResponse createInviteLink(UserPrincipal principal);

    @Operation(
        summary = "초대 수락 API",
        description = """
            초대 링크에서 inviteCode를 추출해 호출하는 API입니다.<br>
            초대 링크에 접속한 사용자는 초대를 수락합니다.<br>
            본인 초대 수락, 만료된 초대, 이미 연결된 관계인 경우에는 에러 메시지를 반환합니다.<br>
            정상적으로 초대 수락이 완료되면 204 응답을 반환합니다.
            """
    )
    void acceptInvite(UserPrincipal principal, @Valid CareRelationshipInviteAcceptRequest request);
}
