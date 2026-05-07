package backend.capstone.domain.friend.friendinvite.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record FriendInviteCreateResponse(
    Long friendInviteId,
    String inviteCode,
    Instant expiresAt
) {

}
