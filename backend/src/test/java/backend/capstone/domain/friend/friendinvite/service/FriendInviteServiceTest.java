package backend.capstone.domain.friend.friendinvite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import backend.capstone.domain.friend.friendinvite.entity.FriendInvite;
import backend.capstone.domain.friend.friendinvite.entity.FriendInviteStatus;
import backend.capstone.domain.friend.friendinvite.exception.FriendInviteErrorCode;
import backend.capstone.domain.friend.friendinvite.repository.FriendInviteRepository;
import backend.capstone.domain.friend.friendship.repository.FriendshipRepository;
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
class FriendInviteServiceTest {

    @Mock
    private FriendInviteRepository friendInviteRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserService userService;

    @Mock
    private FriendInviteCodeGenerator friendInviteCodeGenerator;

    @InjectMocks
    private FriendInviteService friendInviteService;

    @Test
    void 초대코드를_생성한다() {
        Long inviterUserId = 1L;
        User inviterUser = createUser(1L, "inviter");

        given(userService.findById(inviterUserId)).willReturn(inviterUser);
        given(friendInviteCodeGenerator.generate()).willReturn("InviteA123");
        given(friendInviteRepository.existsByInviteCode("InviteA123")).willReturn(false);

        String inviteCode = friendInviteService.createInvite(inviterUserId);
        ArgumentCaptor<FriendInvite> captor = ArgumentCaptor.forClass(FriendInvite.class);

        verify(friendInviteRepository).save(captor.capture());
        FriendInvite savedFriendInvite = captor.getValue();

        assertThat(inviteCode).isEqualTo("InviteA123");
        assertThat(savedFriendInvite.getInviterUser()).isEqualTo(inviterUser);
        assertThat(savedFriendInvite.getStatus()).isEqualTo(FriendInviteStatus.PENDING);
        assertThat(savedFriendInvite.getExpiresAt())
            .isAfter(Instant.now().plus(6, ChronoUnit.DAYS));
    }

    @Test
    void 중복된초대코드면_다시생성한다() {
        Long inviterUserId = 1L;
        User inviterUser = createUser(1L, "inviter");

        given(userService.findById(inviterUserId)).willReturn(inviterUser);
        given(friendInviteCodeGenerator.generate()).willReturn("duplicate1", "duplicate2",
            "uniqueCode1");
        given(friendInviteRepository.existsByInviteCode("duplicate1")).willReturn(true);
        given(friendInviteRepository.existsByInviteCode("duplicate2")).willReturn(true);
        given(friendInviteRepository.existsByInviteCode("uniqueCode1")).willReturn(false);

        String inviteCode = friendInviteService.createInvite(inviterUserId);

        assertThat(inviteCode).isEqualTo("uniqueCode1");
    }

    @Test
    void 친구초대를_수락한다() {
        User inviterUser = createUser(1L, "inviter");
        User acceptedUser = createUser(2L, "accepted");
        FriendInvite friendInvite = FriendInvite.builder()
            .inviterUser(inviterUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();

        given(friendInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(friendInvite));
        given(userService.findById(acceptedUser.getId())).willReturn(acceptedUser);
        given(friendshipRepository.existsByUserAndFriend(any(), any())).willReturn(false);

        friendInviteService.acceptInvite("invite-code", acceptedUser.getId());

        assertThat(friendInvite.getStatus()).isEqualTo(FriendInviteStatus.ACCEPTED);
        assertThat(friendInvite.getAcceptedUser()).isEqualTo(acceptedUser);
        assertThat(friendInvite.getAcceptedAt()).isNotNull();
        verify(friendshipRepository).save(any());
    }

    @Test
    void 본인초대는_수락할수없다() {
        User inviterUser = createUser(1L, "inviter");
        FriendInvite friendInvite = FriendInvite.builder()
            .inviterUser(inviterUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();

        given(friendInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(friendInvite));

        assertThatThrownBy(() -> friendInviteService.acceptInvite("invite-code", 1L))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(FriendInviteErrorCode.FRIEND_INVITE_SELF_ACCEPT_NOT_ALLOWED);

        verifyNoInteractions(userService);
    }

    @Test
    void 이미친구면_수락할수없다() {
        User inviterUser = createUser(1L, "inviter");
        User acceptedUser = createUser(2L, "accepted");
        FriendInvite friendInvite = FriendInvite.builder()
            .inviterUser(inviterUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();

        given(friendInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(friendInvite));
        given(userService.findById(acceptedUser.getId())).willReturn(acceptedUser);
        given(friendshipRepository.existsByUserAndFriend(any(), any())).willReturn(true);

        assertThatThrownBy(
            () -> friendInviteService.acceptInvite("invite-code", acceptedUser.getId()))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(FriendInviteErrorCode.FRIEND_ALREADY_EXISTS);
    }

    @Test
    void 만료된초대는_수락할수없다() {
        User inviterUser = createUser(1L, "inviter");
        FriendInvite friendInvite = FriendInvite.builder()
            .inviterUser(inviterUser)
            .inviteCode("invite-code")
            .expiresAt(Instant.now().minus(1, ChronoUnit.SECONDS))
            .build();

        given(friendInviteRepository.findByInviteCode("invite-code")).willReturn(
            Optional.of(friendInvite));

        assertThatThrownBy(() -> friendInviteService.acceptInvite("invite-code", 2L))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(FriendInviteErrorCode.FRIEND_INVITE_EXPIRED);
        assertThat(friendInvite.getStatus()).isEqualTo(FriendInviteStatus.EXPIRED);
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
