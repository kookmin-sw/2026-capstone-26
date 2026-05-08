package backend.capstone.domain.friend.friendinvite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FriendInvitePageController {

    @GetMapping("/friend/invite")
    public String invitePage() {
        return "forward:/friend/invite.html";
    }
}
