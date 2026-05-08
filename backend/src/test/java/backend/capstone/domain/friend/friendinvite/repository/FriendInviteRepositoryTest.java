package backend.capstone.domain.friend.friendinvite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.capstone.domain.friend.friendinvite.entity.FriendInvite;
import backend.capstone.domain.friend.friendinvite.entity.FriendInviteStatus;
import backend.capstone.domain.friend.friendinvite.repository.FriendInviteRepository;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FriendInviteRepositoryTest {

    @Autowired
    private FriendInviteRepository friendInviteRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 친구초대수락정보를_저장한다() {
        User inviter = saveUser("inviter");
        User acceptedUser = saveUser("accepted");
        FriendInvite friendInvite = friendInviteRepository.save(FriendInvite.builder()
            .inviterUser(inviter)
            .inviteCode("invite-code-123")
            .expiresAt(Instant.parse("2026-05-31T14:59:00Z"))
            .build());

        Instant acceptedAt = Instant.parse("2026-05-07T05:30:00Z");
        friendInvite.accept(acceptedUser, acceptedAt);

        FriendInvite savedFriendInvite = friendInviteRepository.findByInviteCode("invite-code-123")
            .orElseThrow();

        assertThat(savedFriendInvite.getStatus()).isEqualTo(FriendInviteStatus.ACCEPTED);
        assertThat(savedFriendInvite.getAcceptedUser().getId()).isEqualTo(acceptedUser.getId());
        assertThat(savedFriendInvite.getAcceptedAt()).isEqualTo(acceptedAt);
    }

    private User saveUser(String nickname) {
        return userRepository.save(User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname(nickname)
            .profileImageUrl("https://example.com/profile.png")
            .build());
    }
}
