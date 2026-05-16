package backend.capstone.domain.care.carerelationshipinvite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.care.carerelationshipinvite.entity.CareRelationshipInvite;
import backend.capstone.domain.care.carerelationshipinvite.entity.CareRelationshipInviteStatus;
import backend.capstone.domain.care.carerelationshipinvite.exception.CareRelationshipInviteErrorCode;
import backend.capstone.domain.care.carerelationshipinvite.repository.CareRelationshipInviteRepository;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.service.UserService;
import backend.capstone.global.exception.BusinessException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareRelationshipInviteServiceTest {

    @Mock
    private CareRelationshipInviteRepository careRelationshipInviteRepository;

    @Mock
    private CareRelationshipRepository careRelationshipRepository;

    @Mock
    private UserService userService;

    @Mock
    private CareRelationshipInviteCodeGenerator careRelationshipInviteCodeGenerator;

    @InjectMocks
    private CareRelationshipInviteService careRelationshipInviteService;

    @Test
    void 보호관계초대코드를생성한다() {
        Long guardianUserId = 1L;
        User guardianUser = createUser(1L, "guardian");

        given(userService.findById(guardianUserId)).willReturn(guardianUser);
        given(careRelationshipInviteCodeGenerator.generate()).willReturn("InviteA123");
        given(careRelationshipInviteRepository.existsByInviteCode("InviteA123")).willReturn(false);

        String inviteCode = careRelationshipInviteService.createInvite(guardianUserId);
        ArgumentCaptor<CareRelationshipInvite> captor = ArgumentCaptor.forClass(
            CareRelationshipInvite.class);

        verify(careRelationshipInviteRepository).save(captor.capture());
        CareRelationshipInvite savedCareRelationshipInvite = captor.getValue();

        assertThat(inviteCode).isEqualTo("InviteA123");
        assertThat(savedCareRelationshipInvite.getGuardianUser()).isEqualTo(guardianUser);
        assertThat(savedCareRelationshipInvite.getStatus()).isEqualTo(
            CareRelationshipInviteStatus.PENDING);
        assertThat(savedCareRelationshipInvite.getExpiresAt())
            .isAfter(Instant.now().plus(6, ChronoUnit.DAYS));
    }

    @Test
    void 중복된코드면다시생성한다() {
        Long guardianUserId = 1L;
        User guardianUser = createUser(1L, "guardian");

        given(userService.findById(guardianUserId)).willReturn(guardianUser);
        given(careRelationshipInviteCodeGenerator.generate()).willReturn(
            "duplicate1",
            "duplicate2",
            "uniqueCode1"
        );
        given(careRelationshipInviteRepository.existsByInviteCode("duplicate1")).willReturn(true);
        given(careRelationshipInviteRepository.existsByInviteCode("duplicate2")).willReturn(true);
        given(careRelationshipInviteRepository.existsByInviteCode("uniqueCode1"))
            .willReturn(false);

        String inviteCode = careRelationshipInviteService.createInvite(guardianUserId);

        assertThat(inviteCode).isEqualTo("uniqueCode1");
    }

    @Test
    void 보호관계초대를수락한다() {
        User guardianUser = createUser(1L, "guardian");
        User dependentUser = createUser(2L, "dependent");
        CareRelationshipInvite careRelationshipInvite = CareRelationshipInvite.builder()
            .guardianUser(guardianUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();

        given(careRelationshipInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(careRelationshipInvite));
        given(userService.findById(dependentUser.getId())).willReturn(dependentUser);
        given(careRelationshipRepository.existsByGuardianUserAndDependentUser(any(), any()))
            .willReturn(false);

        careRelationshipInviteService.acceptInvite("invite-code", dependentUser.getId());

        assertThat(careRelationshipInvite.getStatus()).isEqualTo(
            CareRelationshipInviteStatus.ACCEPTED);
        assertThat(careRelationshipInvite.getDependentUser()).isEqualTo(dependentUser);
        assertThat(careRelationshipInvite.getAcceptedAt()).isNotNull();
        verify(careRelationshipRepository).save(any());
    }

    @Test
    void 본인초대는수락할수없다() {
        User guardianUser = createUser(1L, "guardian");
        CareRelationshipInvite careRelationshipInvite = CareRelationshipInvite.builder()
            .guardianUser(guardianUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();

        given(careRelationshipInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(careRelationshipInvite));

        assertThatThrownBy(() -> careRelationshipInviteService.acceptInvite("invite-code", 1L))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(
                CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_INVITE_SELF_ACCEPT_NOT_ALLOWED);

        verifyNoInteractions(userService);
    }

    @Test
    void 이미보호관계면수락할수없다() {
        User guardianUser = createUser(1L, "guardian");
        User dependentUser = createUser(2L, "dependent");
        CareRelationshipInvite careRelationshipInvite = CareRelationshipInvite.builder()
            .guardianUser(guardianUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();

        given(careRelationshipInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(careRelationshipInvite));
        given(userService.findById(dependentUser.getId())).willReturn(dependentUser);
        given(careRelationshipRepository.existsByGuardianUserAndDependentUser(any(), any()))
            .willReturn(true);

        assertThatThrownBy(
            () -> careRelationshipInviteService.acceptInvite("invite-code", dependentUser.getId()))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_ALREADY_EXISTS);
    }

    @Test
    void 만료된초대는수락할수없다() {
        User guardianUser = createUser(1L, "guardian");
        CareRelationshipInvite careRelationshipInvite = CareRelationshipInvite.builder()
            .guardianUser(guardianUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().minus(1, ChronoUnit.SECONDS))
            .build();

        given(careRelationshipInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(careRelationshipInvite));

        assertThatThrownBy(() -> careRelationshipInviteService.acceptInvite("invite-code", 2L))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_INVITE_EXPIRED);
        assertThat(careRelationshipInvite.getStatus()).isEqualTo(
            CareRelationshipInviteStatus.EXPIRED);
    }

    private User createUser(Long userId, String nickname) {
        User user = User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(nickname + "-provider")
            .nickname(nickname)
            .profileImageUrl("https://example.com/profile.png")
            .build();

        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }

        return user;
    }
}
