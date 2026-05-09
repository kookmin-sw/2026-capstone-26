package backend.capstone.domain.care.carerelationshipinvite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.capstone.domain.care.carerelationshipinvite.entity.CareRelationshipInvite;
import backend.capstone.domain.care.carerelationshipinvite.entity.CareRelationshipInviteStatus;
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
class CareRelationshipInviteRepositoryTest {

    @Autowired
    private CareRelationshipInviteRepository careRelationshipInviteRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 보호관계초대수락정보를저장한다() {
        User guardianUser = saveUser("guardian");
        User dependentUser = saveUser("dependent");
        CareRelationshipInvite careRelationshipInvite =
            careRelationshipInviteRepository.save(CareRelationshipInvite.builder()
                .guardianUser(guardianUser)
                .inviteCode("invite-code-123")
                .expiresAt(Instant.parse("2026-05-31T14:59:00Z"))
                .build());

        Instant acceptedAt = Instant.parse("2026-05-07T05:30:00Z");
        careRelationshipInvite.accept(dependentUser, acceptedAt);

        CareRelationshipInvite savedCareRelationshipInvite =
            careRelationshipInviteRepository.findByInviteCode("invite-code-123").orElseThrow();

        assertThat(savedCareRelationshipInvite.getStatus())
            .isEqualTo(CareRelationshipInviteStatus.ACCEPTED);
        assertThat(savedCareRelationshipInvite.getDependentUser().getId())
            .isEqualTo(dependentUser.getId());
        assertThat(savedCareRelationshipInvite.getAcceptedAt()).isEqualTo(acceptedAt);
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
