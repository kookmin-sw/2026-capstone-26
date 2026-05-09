package backend.capstone.domain.friend.carerelationshipinvite.exception;

import backend.capstone.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CareRelationshipInviteErrorCode implements ErrorCode {

    CARE_RELATIONSHIP_INVITE_CODE_GENERATION_FAILED(
        "중복되지 않는 보호 관계 초대 코드를 생성하지 못했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),
    CARE_RELATIONSHIP_INVITE_NOT_FOUND(
        "보호 관계 초대를 찾을 수 없습니다.",
        HttpStatus.NOT_FOUND
    ),
    CARE_RELATIONSHIP_INVITE_ALREADY_PROCESSED(
        "이미 처리된 보호 관계 초대입니다.",
        HttpStatus.BAD_REQUEST
    ),
    CARE_RELATIONSHIP_INVITE_EXPIRED(
        "만료된 보호 관계 초대입니다.",
        HttpStatus.BAD_REQUEST
    ),
    CARE_RELATIONSHIP_INVITE_SELF_ACCEPT_NOT_ALLOWED(
        "본인이 생성한 보호 관계 초대를 직접 수락할 수 없습니다.",
        HttpStatus.BAD_REQUEST
    ),
    CARE_RELATIONSHIP_ALREADY_EXISTS(
        "이미 보호 관계가 존재합니다.",
        HttpStatus.CONFLICT
    );

    private final String message;
    private final HttpStatus status;
}
