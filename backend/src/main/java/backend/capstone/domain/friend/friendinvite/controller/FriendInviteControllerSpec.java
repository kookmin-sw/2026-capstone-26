package backend.capstone.domain.friend.friendinvite.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.friend.friendinvite.dto.FriendInviteLinkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}
