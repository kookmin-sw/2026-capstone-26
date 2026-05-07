package backend.capstone.domain.friend.friendship.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.capstone.domain.friend.friendship.entity.Friendship;
import backend.capstone.domain.friend.friendship.repository.FriendshipRepository;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FriendshipRepositoryTest {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 친구관계생성시작은사용자아이디가앞에저장된다() {
        User firstUser = saveUser("first");
        User secondUser = saveUser("second");

        Friendship friendship = friendshipRepository.save(Friendship.of(secondUser, firstUser));

        assertThat(friendship.getUser().getId()).isEqualTo(firstUser.getId());
        assertThat(friendship.getFriend().getId()).isEqualTo(secondUser.getId());
    }

    @Test
    void 자기자신과친구관계를생성할수없다() {
        User user = saveUser("self");

        assertThatThrownBy(() -> Friendship.of(user, user))
            .isInstanceOf(IllegalArgumentException.class);
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
