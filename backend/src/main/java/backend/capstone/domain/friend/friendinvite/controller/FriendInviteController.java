package backend.capstone.domain.friend.friendinvite.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.friend.friendinvite.dto.FriendInviteAcceptRequest;
import backend.capstone.domain.friend.friendinvite.dto.FriendInviteLinkResponse;
import backend.capstone.domain.friend.friendinvite.service.FriendInviteService;
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
@RequestMapping("/api/friend-invites")
public class FriendInviteController implements FriendInviteControllerSpec {

    private final FriendInviteService friendInviteService;

    @Override
    @PostMapping("/link")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendInviteLinkResponse createInviteLink(
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return friendInviteService.createInviteLink(principal.userId());
    }

    @Override
    @PostMapping("/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptInvite(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody FriendInviteAcceptRequest request
    ) {
        friendInviteService.acceptInvite(request.inviteCode(), principal.userId());
    }
}
