package backend.capstone.domain.friend.friendinvite.dto;

import lombok.Builder;

@Builder
public record FriendInviteLinkResponse(
    String inviteLink
) {

}
