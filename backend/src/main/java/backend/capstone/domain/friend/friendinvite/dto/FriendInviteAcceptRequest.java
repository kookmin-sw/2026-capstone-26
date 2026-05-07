package backend.capstone.domain.friend.friendinvite.dto;

import jakarta.validation.constraints.NotBlank;

public record FriendInviteAcceptRequest(
    @NotBlank String inviteCode
) {

}
