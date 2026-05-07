package backend.capstone.domain.friend.friendinvite.controller;

import backend.capstone.auth.dto.UserPrincipal;
import backend.capstone.domain.friend.friendinvite.dto.FriendInviteLinkResponse;
import backend.capstone.domain.friend.friendinvite.service.FriendInviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}
