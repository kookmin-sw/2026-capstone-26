package backend.capstone.domain.friend.friendinvite.mapper;

import backend.capstone.domain.friend.friendinvite.dto.FriendInviteLinkResponse;
import backend.capstone.domain.friend.friendinvite.entity.FriendInvite;
import backend.capstone.domain.user.entity.User;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendInviteMapper {

    public static FriendInvite toEntity(User inviterUser, String inviteCode, Instant expiresAt) {
        return FriendInvite.builder()
            .inviterUser(inviterUser)
            .inviteCode(inviteCode)
            .expiresAt(expiresAt)
            .build();
    }

    public static FriendInviteLinkResponse toLinkResponse(String inviteLink) {
        return FriendInviteLinkResponse.builder()
            .inviteLink(inviteLink)
            .build();
    }

}
