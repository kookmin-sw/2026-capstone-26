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
    );

    private final String message;
    private final HttpStatus status;
}
