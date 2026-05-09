package backend.capstone.domain.friend.carerelationshipinvite.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class CareRelationshipInviteCodeGenerator {

    private static final String BASE62 =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int INVITE_CODE_LENGTH = 10;

    private final SecureRandom secureRandom = new SecureRandom();

    //무작위한 문자 or 숫자를 10개를 뽑아서 이어붙임
    public String generate() {
        StringBuilder inviteCode = new StringBuilder(INVITE_CODE_LENGTH);

        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            inviteCode.append(BASE62.charAt(secureRandom.nextInt(BASE62.length())));
        }

        return inviteCode.toString();
    }
}
