package backend.capstone.domain.bookmarkplace.service;

import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceCreateRequest;
import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceCreateResponse;
import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceListResponse;
import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceUpdateRequest;
import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceUpdateResponse;
import backend.capstone.domain.bookmarkplace.entity.BookmarkPlace;
import backend.capstone.domain.bookmarkplace.entity.BookmarkPlaceType;
import backend.capstone.domain.bookmarkplace.exception.BookmarkPlaceErrorCode;
import backend.capstone.domain.bookmarkplace.mapper.BookmarkPlaceMapper;
import backend.capstone.domain.bookmarkplace.repository.BookmarkPlaceRepository;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.service.UserService;
import backend.capstone.global.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkPlaceService {

    private final BookmarkPlaceRepository bookmarkPlaceRepository;
    private final UserService userService;

    @Transactional
    public BookmarkPlaceCreateResponse createBookmarkPlace(Long userId,
        BookmarkPlaceCreateRequest request) {
        validateDefaultBookmarkPlace(request.type(), request.isDefault());
        updateExistingDefaultHomeBookmarkPlace(userId, null, request.type(), request.isDefault());

        User user = userService.findById(userId);
        BookmarkPlace bookmarkPlace = BookmarkPlaceMapper.toEntity(user, request);
        BookmarkPlace savedBookmarkPlace = bookmarkPlaceRepository.save(bookmarkPlace);

        return BookmarkPlaceMapper.toCreateResponse(savedBookmarkPlace);
    }

    @Transactional
    public BookmarkPlaceUpdateResponse updateBookmarkPlace(Long userId, Long bookmarkPlaceId,
        BookmarkPlaceUpdateRequest request) {
        BookmarkPlace bookmarkPlace = bookmarkPlaceRepository.findByIdAndUserId(bookmarkPlaceId,
                userId)
            .orElseThrow(
                () -> new BusinessException(BookmarkPlaceErrorCode.BOOKMARK_PLACE_NOT_FOUND));

        validateDefaultBookmarkPlace(request.type(), request.isDefault());
        updateExistingDefaultHomeBookmarkPlace(userId, bookmarkPlace.getId(), request.type(),
            request.isDefault());

        bookmarkPlace.update(request.type(), request.placeName(), request.roadAddress(),
            request.latitude(), request.longitude(), request.isDefault()
        );

        return BookmarkPlaceMapper.toUpdateResponse(bookmarkPlace);
    }

    @Transactional
    public void deleteBookmarkPlace(Long userId, Long bookmarkPlaceId) {
        BookmarkPlace bookmarkPlace = bookmarkPlaceRepository.findByIdAndUserId(bookmarkPlaceId,
                userId)
            .orElseThrow(
                () -> new BusinessException(BookmarkPlaceErrorCode.BOOKMARK_PLACE_NOT_FOUND));

        bookmarkPlaceRepository.delete(bookmarkPlace);
    }

    @Transactional(readOnly = true)
    public BookmarkPlaceListResponse getBookmarkPlaces(Long userId) {
        List<BookmarkPlace> bookmarkPlaces = bookmarkPlaceRepository.findByUserIdOrderByIdAsc(
            userId);

        return BookmarkPlaceMapper.toListResponse(bookmarkPlaces);
    }

    @Transactional(readOnly = true)
    public List<BookmarkPlace> getBookmarkPlaceByUserId(Long userId) {
        return bookmarkPlaceRepository.findByUserIdOrderByIdAsc(userId);
    }

    private void validateDefaultBookmarkPlace(BookmarkPlaceType type, boolean isDefault) {
        if (isDefault && type != BookmarkPlaceType.HOME) {
            throw new BusinessException(BookmarkPlaceErrorCode.INVALID_DEFAULT_BOOKMARK_PLACE);
        }
    }

    private void updateExistingDefaultHomeBookmarkPlace(Long userId, Long bookmarkPlaceId,
        BookmarkPlaceType type, boolean isDefault) {
        if (type != BookmarkPlaceType.HOME || !isDefault) {
            return;
        }

        //- 새 기본 HOME 생성: 기존 기본 HOME이 있으면 해제
        //- 다른 HOME을 기본 HOME으로 수정: 기존 기본 HOME이 있으면 해제
        //- 이미 기본 HOME인 같은 북마크를 다시 기본으로 수정: 아무 것도 안 함
        bookmarkPlaceRepository.findDefaultByUserIdAndType(userId, BookmarkPlaceType.HOME)
            .filter(bookmarkPlace -> !bookmarkPlace.getId().equals(bookmarkPlaceId))
            .ifPresent(bookmarkPlace -> bookmarkPlace.changeDefault(false));
    }
}
