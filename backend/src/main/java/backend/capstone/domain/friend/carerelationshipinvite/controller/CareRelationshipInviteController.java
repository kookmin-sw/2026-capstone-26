package backend.capstone.domain.friend.carerelationshipinvite.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.friend.carerelationshipinvite.dto.CareRelationshipInviteAcceptRequest;
import backend.capstone.domain.friend.carerelationshipinvite.dto.CareRelationshipInviteLinkResponse;
import backend.capstone.domain.friend.carerelationshipinvite.service.CareRelationshipInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/care-relationship-invites")
public class CareRelationshipInviteController implements CareRelationshipInviteControllerSpec {

    private final CareRelationshipInviteService careRelationshipInviteService;

    @Override
    @PostMapping("/link")
    @ResponseStatus(HttpStatus.CREATED)
    public CareRelationshipInviteLinkResponse createInviteLink(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return careRelationshipInviteService.createInviteLink(principal.userId());
    }

    @Override
    @PostMapping("/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptInvite(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody CareRelationshipInviteAcceptRequest request
    ) {
        careRelationshipInviteService.acceptInvite(request.inviteCode(), principal.userId());
    }
}
