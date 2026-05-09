package backend.capstone.domain.care.carerelationshipinvite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CareRelationshipInviteLinkResponse(
    @Schema(example = "https://passedpath.site/care-relationship/invite?inviteCode=T5rfCFFy9j")
    String inviteLink
) {

}
