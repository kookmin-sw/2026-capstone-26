package backend.capstone.domain.friend.carerelationshipinvite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CareRelationshipInviteAcceptRequest(
    @NotBlank @Schema(example = "T5rfCFFy9j")
    String inviteCode
) {

}
