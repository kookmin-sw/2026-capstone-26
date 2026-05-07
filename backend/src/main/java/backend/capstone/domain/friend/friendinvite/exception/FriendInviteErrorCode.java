package backend.capstone.domain.friend.friendinvite.exception;

import backend.capstone.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FriendInviteErrorCode implements ErrorCode {

    FRIEND_INVITE_CODE_GENERATION_FAILED(
        "중복되지 않는 친구 초대 코드를 생성하지 못했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),
    FRIEND_INVITE_NOT_FOUND(
        "친구 초대를 찾을 수 없습니다.",
        HttpStatus.NOT_FOUND
    ),
    FRIEND_INVITE_ALREADY_PROCESSED(
        "이미 처리된 친구 초대입니다.",
        HttpStatus.BAD_REQUEST
    ),
    FRIEND_INVITE_EXPIRED(
        "만료된 친구 초대입니다.",
        HttpStatus.BAD_REQUEST
    ),
    FRIEND_INVITE_SELF_ACCEPT_NOT_ALLOWED(
        "본인이 생성한 친구 초대는 직접 수락할 수 없습니다.",
        HttpStatus.BAD_REQUEST
    ),
    FRIEND_ALREADY_EXISTS(
        "이미 친구 관계가 존재합니다.",
        HttpStatus.CONFLICT
    );

    private final String message;
    private final HttpStatus status;
}
