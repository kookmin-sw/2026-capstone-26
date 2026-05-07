package backend.capstone.domain.friend.friendinvite.service;

import backend.capstone.domain.friend.friendinvite.dto.FriendInviteCreateResponse;
import backend.capstone.domain.friend.friendinvite.entity.FriendInvite;
import backend.capstone.domain.friend.friendinvite.exception.FriendInviteErrorCode;
import backend.capstone.domain.friend.friendinvite.mapper.FriendInviteMapper;
import backend.capstone.domain.friend.friendinvite.repository.FriendInviteRepository;
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

    private static final int EXPIRE_DAYS = 7;
    private static final int MAX_GENERATE_ATTEMPTS = 10;

    private final FriendInviteRepository friendInviteRepository;
    private final UserService userService;
    private final FriendInviteCodeGenerator friendInviteCodeGenerator;

    @Transactional
    public FriendInviteCreateResponse createInvite(Long inviterUserId) {
        User inviterUser = userService.findById(inviterUserId);
        String inviteCode = generateUniqueInviteCode();
        Instant expiresAt = Instant.now().plus(EXPIRE_DAYS, ChronoUnit.DAYS);

        FriendInvite friendInvite = friendInviteRepository.save(
            FriendInviteMapper.toEntity(inviterUser, inviteCode, expiresAt));

        return FriendInviteMapper.toCreateResponse(friendInvite);
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
