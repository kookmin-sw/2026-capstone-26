package backend.capstone.domain.care.carerelationship.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.capstone.domain.care.carerelationship.entity.CareRelationship;
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
class CareRelationshipRepositoryTest {

    @Autowired
    private CareRelationshipRepository careRelationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 보호관계생성시보호자와보호대상자를그대로저장한다() {
        User guardianUser = saveUser("guardian");
        User dependentUser = saveUser("dependent");

        CareRelationship careRelationship =
            careRelationshipRepository.save(CareRelationship.of(guardianUser, dependentUser));

        assertThat(careRelationship.getGuardianUser().getId()).isEqualTo(guardianUser.getId());
        assertThat(careRelationship.getDependentUser().getId()).isEqualTo(dependentUser.getId());
    }

    @Test
    void 자기자신과는보호관계를생성할수없다() {
        User user = saveUser("self");

        assertThatThrownBy(() -> CareRelationship.of(user, user))
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
