package backend.capstone.domain.care.carerelationshipinvite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CareRelationshipInvitePageController {

    @GetMapping("/care-relationship/invite")
    public String invitePage() {
        return "forward:/care-relationship/invite.html";
    }
}
