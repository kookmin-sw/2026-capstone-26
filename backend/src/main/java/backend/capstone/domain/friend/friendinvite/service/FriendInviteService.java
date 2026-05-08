package backend.capstone.domain.friend.friendinvite.service;

import backend.capstone.domain.friend.friendinvite.dto.FriendInviteLinkResponse;
import backend.capstone.domain.friend.friendinvite.entity.FriendInvite;
import backend.capstone.domain.friend.friendinvite.entity.FriendInviteStatus;
import backend.capstone.domain.friend.friendinvite.exception.FriendInviteErrorCode;
import backend.capstone.domain.friend.friendinvite.mapper.FriendInviteMapper;
import backend.capstone.domain.friend.friendinvite.repository.FriendInviteRepository;
import backend.capstone.domain.friend.friendship.entity.Friendship;
import backend.capstone.domain.friend.friendship.repository.FriendshipRepository;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.service.UserService;
import backend.capstone.global.exception.BusinessException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendInviteService {

    private static final String FRIEND_INVITE_LINK_FORMAT =
        "https://passedpath.site/friend/invite?inviteCode=%s";
    private static final int EXPIRE_DAYS = 7;
    private static final int MAX_GENERATE_ATTEMPTS = 10;

    private final FriendInviteRepository friendInviteRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final FriendInviteCodeGenerator friendInviteCodeGenerator;

    @Transactional
    public FriendInviteLinkResponse createInviteLink(Long inviterUserId) {
        String inviteCode = createInvite(inviterUserId);
        String inviteLink = FRIEND_INVITE_LINK_FORMAT.formatted(inviteCode);
        return FriendInviteMapper.toLinkResponse(inviteLink);
    }

    @Transactional
    public String createInvite(Long inviterUserId) {
        User inviterUser = userService.findById(inviterUserId);
        String inviteCode = generateUniqueInviteCode();
        Instant expiresAt = Instant.now().plus(EXPIRE_DAYS, ChronoUnit.DAYS);

        friendInviteRepository.save(
            FriendInviteMapper.toEntity(inviterUser, inviteCode, expiresAt));

        return inviteCode;
    }

    @Transactional
    public void acceptInvite(String inviteCode, Long acceptedUserId) {
        FriendInvite friendInvite = friendInviteRepository.findByInviteCode(inviteCode)
            .orElseThrow(
                () -> new BusinessException(FriendInviteErrorCode.FRIEND_INVITE_NOT_FOUND));

        validateInvitable(friendInvite, acceptedUserId);

        User acceptedUser = userService.findById(acceptedUserId);
        Friendship friendship = Friendship.of(friendInvite.getInviterUser(), acceptedUser);

        if (friendshipRepository.existsByUserAndFriend(friendship.getUser(),
            friendship.getFriend())) {
            throw new BusinessException(FriendInviteErrorCode.FRIEND_ALREADY_EXISTS);
        }

        friendInvite.accept(acceptedUser, Instant.now());
        friendshipRepository.save(friendship);
    }

    private void validateInvitable(FriendInvite friendInvite, Long acceptedUserId) {
        if (friendInvite.getStatus() != FriendInviteStatus.PENDING) {
            throw new BusinessException(FriendInviteErrorCode.FRIEND_INVITE_ALREADY_PROCESSED);
        }

        if (friendInvite.getExpiresAt().isBefore(Instant.now())) {
            friendInvite.expire();
            throw new BusinessException(FriendInviteErrorCode.FRIEND_INVITE_EXPIRED);
        }

        if (friendInvite.getInviterUser().getId().equals(acceptedUserId)) {
            throw new BusinessException(
                FriendInviteErrorCode.FRIEND_INVITE_SELF_ACCEPT_NOT_ALLOWED);
        }
    }

    //중복되지 않는 초대 코드를 만드는 시도를 최대 10번까지 함
    private String generateUniqueInviteCode() {
        for (int attempt = 0; attempt < MAX_GENERATE_ATTEMPTS; attempt++) {
            String inviteCode = friendInviteCodeGenerator.generate();
            if (!friendInviteRepository.existsByInviteCode(inviteCode)) {
                return inviteCode;
            }
        }

        throw new BusinessException(FriendInviteErrorCode.FRIEND_INVITE_CODE_GENERATION_FAILED);
    }
}
