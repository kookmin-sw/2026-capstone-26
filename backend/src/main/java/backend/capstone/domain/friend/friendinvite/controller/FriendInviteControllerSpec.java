package backend.capstone.domain.friend.friendinvite.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.friend.friendinvite.dto.FriendInviteAcceptRequest;
import backend.capstone.domain.friend.friendinvite.dto.FriendInviteLinkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "친구 초대 API")
public interface FriendInviteControllerSpec {

    @Operation(
        summary = "친구 초대 링크 생성 API",
        description = """
            로그인한 사용자의 친구 초대 링크를 생성합니다.<br>
            응답으로 카카오톡 등에 바로 공유할 수 있는 초대 링크 문자열을 반환합니다.
            """
    )
    FriendInviteLinkResponse createInviteLink(UserPrincipal principal);

    @Operation(
        summary = "친구 초대 수락 API",
        description = """
            초대 코드를 사용해 친구 초대를 수락합니다.<br>
            본인 초대 수락, 만료된 초대, 이미 친구인 경우에는 에러 메시지가 반환됩니다.<br>
            친구 추가가 정상적으로 완료되면 204 응답이 반환됩니다.
            """
    )
    void acceptInvite(UserPrincipal principal, @Valid FriendInviteAcceptRequest request);
}
