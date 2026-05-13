package backend.capstone.domain.bookmarkplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceCreateRequest;
import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceCreateResponse;
import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceUpdateRequest;
import backend.capstone.domain.bookmarkplace.dto.BookmarkPlaceUpdateResponse;
import backend.capstone.domain.bookmarkplace.entity.BookmarkPlace;
import backend.capstone.domain.bookmarkplace.entity.BookmarkPlaceType;
import backend.capstone.domain.bookmarkplace.exception.BookmarkPlaceErrorCode;
import backend.capstone.domain.bookmarkplace.repository.BookmarkPlaceRepository;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.service.UserService;
import backend.capstone.global.exception.BusinessException;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkPlaceServiceTest {

    @Mock
    private BookmarkPlaceRepository bookmarkPlaceRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookmarkPlaceService bookmarkPlaceService;

    @Test
    void 집_즐겨찾기가_이미_있으면_HOME_타입을_추가_등록할_수_없다() {
        BookmarkPlaceCreateRequest request = new BookmarkPlaceCreateRequest(
            BookmarkPlaceType.HOME, "집", "서울시", 37.1, 127.1);

        given(bookmarkPlaceRepository.existsByUserIdAndType(1L, BookmarkPlaceType.HOME))
            .willReturn(true);

        assertThatThrownBy(() -> bookmarkPlaceService.createBookmarkPlace(1L, request))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(BookmarkPlaceErrorCode.HOME_BOOKMARK_ALREADY_EXISTS);
    }

    @Test
    void 다른_HOME_즐겨찾기가_있으면_HOME_타입으로_수정할_수_없다() {
        BookmarkPlace bookmarkPlace = createBookmarkPlace(10L, BookmarkPlaceType.COMPANY);
        BookmarkPlaceUpdateRequest request = new BookmarkPlaceUpdateRequest(
            BookmarkPlaceType.HOME, "집", "서울시", 37.1, 127.1);

        given(bookmarkPlaceRepository.findByIdAndUserId(10L, 1L)).willReturn(
            Optional.of(bookmarkPlace));
        given(bookmarkPlaceRepository.existsByUserIdAndTypeAndIdNot(1L, BookmarkPlaceType.HOME,
            10L)).willReturn(true);

        assertThatThrownBy(() -> bookmarkPlaceService.updateBookmarkPlace(1L, 10L, request))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(BookmarkPlaceErrorCode.HOME_BOOKMARK_ALREADY_EXISTS);
    }

    @Test
    void 기존_HOME_즐겨찾기는_같은_HOME_타입으로_수정할_수_있다() {
        BookmarkPlace bookmarkPlace = createBookmarkPlace(10L, BookmarkPlaceType.HOME);
        BookmarkPlaceUpdateRequest request = new BookmarkPlaceUpdateRequest(
            BookmarkPlaceType.HOME, "우리집", "서울시 강남구", 37.2, 127.2);

        given(bookmarkPlaceRepository.findByIdAndUserId(10L, 1L)).willReturn(
            Optional.of(bookmarkPlace));
        given(bookmarkPlaceRepository.existsByUserIdAndTypeAndIdNot(1L, BookmarkPlaceType.HOME,
            10L)).willReturn(false);

        BookmarkPlaceUpdateResponse response = bookmarkPlaceService.updateBookmarkPlace(1L, 10L,
            request);

        assertThat(bookmarkPlace.getType()).isEqualTo(BookmarkPlaceType.HOME);
        assertThat(bookmarkPlace.getName()).isEqualTo("우리집");
        assertThat(bookmarkPlace.getRoadAddress()).isEqualTo("서울시 강남구");
        assertThat(bookmarkPlace.getLatitude()).isEqualTo(37.2);
        assertThat(bookmarkPlace.getLongitude()).isEqualTo(127.2);
        assertThat(response.type()).isEqualTo(BookmarkPlaceType.HOME);
    }

    @Test
    void HOME_타입이_없으면_정상적으로_등록할_수_있다() {
        User user = User.builder()
            .provider(ProviderType.KAKAO)
            .providerId("provider-1")
            .nickname("tester")
            .profileImageUrl("https://example.com/profile.png")
            .build();
        BookmarkPlaceCreateRequest request = new BookmarkPlaceCreateRequest(
            BookmarkPlaceType.HOME, "집", "서울시", 37.1, 127.1);

        given(bookmarkPlaceRepository.existsByUserIdAndType(1L, BookmarkPlaceType.HOME))
            .willReturn(false);
        given(userService.findById(1L)).willReturn(user);
        given(bookmarkPlaceRepository.save(org.mockito.ArgumentMatchers.any(BookmarkPlace.class)))
            .willAnswer(invocation -> {
                BookmarkPlace savedBookmarkPlace = invocation.getArgument(0);
                setBookmarkPlaceId(savedBookmarkPlace, 100L);
                return savedBookmarkPlace;
            });

        BookmarkPlaceCreateResponse response = bookmarkPlaceService.createBookmarkPlace(1L,
            request);
        ArgumentCaptor<BookmarkPlace> captor = ArgumentCaptor.forClass(BookmarkPlace.class);

        verify(bookmarkPlaceRepository).save(captor.capture());

        assertThat(captor.getValue().getType()).isEqualTo(BookmarkPlaceType.HOME);
        assertThat(response.bookmarkPlaceId()).isEqualTo(100L);
        assertThat(response.type()).isEqualTo(BookmarkPlaceType.HOME);
    }

    private BookmarkPlace createBookmarkPlace(Long id, BookmarkPlaceType type) {
        BookmarkPlace bookmarkPlace = BookmarkPlace.builder()
            .user(User.builder()
                .provider(ProviderType.KAKAO)
                .providerId("provider-" + id)
                .nickname("tester")
                .profileImageUrl("https://example.com/profile.png")
                .build())
            .type(type)
            .name("기존 장소")
            .roadAddress("기존 주소")
            .latitude(37.0)
            .longitude(127.0)
            .build();
        setBookmarkPlaceId(bookmarkPlace, id);
        return bookmarkPlace;
    }

    private void setBookmarkPlaceId(BookmarkPlace bookmarkPlace, Long id) {
        try {
            Field idField = BookmarkPlace.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(bookmarkPlace, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("북마크 장소 ID 설정에 실패했습니다.", e);
        }
    }
}
