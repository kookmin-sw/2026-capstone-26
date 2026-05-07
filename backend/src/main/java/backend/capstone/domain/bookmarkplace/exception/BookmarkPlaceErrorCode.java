package backend.capstone.domain.bookmarkplace.exception;

import backend.capstone.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookmarkPlaceErrorCode implements ErrorCode {

    BOOKMARK_PLACE_NOT_FOUND("즐겨찾기 장소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_DEFAULT_BOOKMARK_PLACE(
        "isDefault가 true인 즐겨찾기 장소는 HOME 타입만 가능합니다.",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final HttpStatus status;
}
