package backend.capstone.domain.friend.friendinvite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FriendInviteLinkResponse(
    @Schema(example = "https://passedpath.site/friend/invite?inviteCode=T5rfCFFy9j")
    String inviteLink
) {

}
