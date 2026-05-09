package backend.capstone.domain.friend.carerelationshipinvite.mapper;

import backend.capstone.domain.friend.carerelationshipinvite.dto.CareRelationshipInviteLinkResponse;
import backend.capstone.domain.friend.carerelationshipinvite.entity.CareRelationshipInvite;
import backend.capstone.domain.user.entity.User;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CareRelationshipInviteMapper {

    public static CareRelationshipInvite toEntity(
        User guardianUser,
        String inviteCode,
        Instant expiresAt
    ) {
        return CareRelationshipInvite.builder()
            .guardianUser(guardianUser)
            .inviteCode(inviteCode)
            .expiresAt(expiresAt)
            .build();
    }

    public static CareRelationshipInviteLinkResponse toLinkResponse(String inviteLink) {
        return CareRelationshipInviteLinkResponse.builder()
            .inviteLink(inviteLink)
            .build();
    }

}
