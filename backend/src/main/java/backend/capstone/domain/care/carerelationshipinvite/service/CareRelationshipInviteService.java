package backend.capstone.domain.care.carerelationshipinvite.service;

import backend.capstone.domain.care.carerelationship.entity.CareRelationship;
import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.care.carerelationshipinvite.dto.CareRelationshipInviteLinkResponse;
import backend.capstone.domain.care.carerelationshipinvite.entity.CareRelationshipInvite;
import backend.capstone.domain.care.carerelationshipinvite.entity.CareRelationshipInviteStatus;
import backend.capstone.domain.care.carerelationshipinvite.exception.CareRelationshipInviteErrorCode;
import backend.capstone.domain.care.carerelationshipinvite.mapper.CareRelationshipInviteMapper;
import backend.capstone.domain.care.carerelationshipinvite.repository.CareRelationshipInviteRepository;
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
public class CareRelationshipInviteService {

    private static final String CARE_RELATIONSHIP_INVITE_LINK_FORMAT =
        "https://passedpath.site/care-relationship/invite?inviteCode=%s";
    private static final int EXPIRE_DAYS = 7;
    private static final int MAX_GENERATE_ATTEMPTS = 10;

    private final CareRelationshipInviteRepository careRelationshipInviteRepository;
    private final CareRelationshipRepository careRelationshipRepository;
    private final UserService userService;
    private final CareRelationshipInviteCodeGenerator careRelationshipInviteCodeGenerator;

    @Transactional
    public CareRelationshipInviteLinkResponse createInviteLink(Long guardianUserId) {
        String inviteCode = createInvite(guardianUserId);
        String inviteLink = CARE_RELATIONSHIP_INVITE_LINK_FORMAT.formatted(inviteCode);
        return CareRelationshipInviteMapper.toLinkResponse(inviteLink);
    }

    @Transactional
    public String createInvite(Long guardianUserId) {
        User guardianUser = userService.findById(guardianUserId);
        String inviteCode = generateUniqueInviteCode();
        Instant expiresAt = Instant.now().plus(EXPIRE_DAYS, ChronoUnit.DAYS);

        careRelationshipInviteRepository.save(
            CareRelationshipInviteMapper.toEntity(guardianUser, inviteCode, expiresAt));

        return inviteCode;
    }

    @Transactional
    public void acceptInvite(String inviteCode, Long dependentUserId) {
        CareRelationshipInvite careRelationshipInvite =
            careRelationshipInviteRepository.findByInviteCode(inviteCode)
                .orElseThrow(
                    () -> new BusinessException(
                        CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_INVITE_NOT_FOUND));

        validateInvitable(careRelationshipInvite, dependentUserId);

        User dependentUser = userService.findById(dependentUserId);
        CareRelationship careRelationship = CareRelationship.of(
            careRelationshipInvite.getGuardianUser(),
            dependentUser
        );

        if (careRelationshipRepository.existsByGuardianUserAndDependentUser(
            careRelationship.getGuardianUser(),
            careRelationship.getDependentUser()
        )) {
            throw new BusinessException(
                CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_ALREADY_EXISTS);
        }

        careRelationshipInvite.accept(dependentUser, Instant.now());
        careRelationshipRepository.save(careRelationship);
    }

    private void validateInvitable(
        CareRelationshipInvite careRelationshipInvite,
        Long dependentUserId
    ) {
        if (careRelationshipInvite.getStatus() != CareRelationshipInviteStatus.PENDING) {
            throw new BusinessException(
                CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_INVITE_ALREADY_PROCESSED);
        }

        if (careRelationshipInvite.getExpiresAt().isBefore(Instant.now())) {
            careRelationshipInvite.expire();
            throw new BusinessException(
                CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_INVITE_EXPIRED);
        }

        if (careRelationshipInvite.getGuardianUser().getId().equals(dependentUserId)) {
            throw new BusinessException(
                CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_INVITE_SELF_ACCEPT_NOT_ALLOWED);
        }
    }

    //중복되지 않는 초대 코드를 만드는 시도를 최대 10번까지 함
    private String generateUniqueInviteCode() {
        for (int attempt = 0; attempt < MAX_GENERATE_ATTEMPTS; attempt++) {
            String inviteCode = careRelationshipInviteCodeGenerator.generate();
            if (!careRelationshipInviteRepository.existsByInviteCode(inviteCode)) {
                return inviteCode;
            }
        }

        throw new BusinessException(
            CareRelationshipInviteErrorCode.CARE_RELATIONSHIP_INVITE_CODE_GENERATION_FAILED);
    }
}
