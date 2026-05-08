package backend.capstone.domain.friend.friendinvite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record FriendInviteAcceptRequest(
    @NotBlank @Schema(example = "T5rfCFFy9j")
    String inviteCode
) {

}
