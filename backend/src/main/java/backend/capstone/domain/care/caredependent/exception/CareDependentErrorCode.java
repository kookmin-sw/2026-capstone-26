package backend.capstone.domain.care.caredependent.exception;

import backend.capstone.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CareDependentErrorCode implements ErrorCode {

    CARE_DEPENDENT_USER_ACCESS_DENIED("조회 권한이 없습니다. 보호 관계 추가를 해주세요.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;
}
