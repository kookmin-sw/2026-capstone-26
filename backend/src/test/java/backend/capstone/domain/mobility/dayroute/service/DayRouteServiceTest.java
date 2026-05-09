package backend.capstone.domain.mobility.dayroute.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.mobility.place.repository.PlaceRepository;
import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DayRouteServiceTest {

    @InjectMocks
    private DayRouteService dayRouteService;

    @Mock
    private DayRouteRepository dayRouteRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private UserService userService;

    @Test
    void 북마크한_dayRoute만_날짜_내림차순으로_조회한다() {
        User user = createUser();
        DayRoute recentBookmarkedDayRoute = createBookmarkedDayRoute(user, LocalDate.of(2026, 5, 3));
        DayRoute oldBookmarkedDayRoute = createBookmarkedDayRoute(user, LocalDate.of(2026, 5, 1));

        given(dayRouteRepository.findBookmarkedByUserIdAndCursorDateOrderByDateDesc(1L, null,
            PageRequest.of(0, 20)))
            .willReturn(List.of(recentBookmarkedDayRoute, oldBookmarkedDayRoute));

        List<DayRoute> dayRoutes = dayRouteService.getBookmarkedDayRoutes(1L, null, 20);

        assertThat(dayRoutes).hasSize(2);
        assertThat(dayRoutes).extracting(DayRoute::getDate)
            .containsExactly(LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 1));
        assertThat(dayRoutes).allMatch(DayRoute::isBookmarked);
    }

    @Test
    void dayRoute를_북마크하면_isBookmarked를_true로_설정한다() {
        User user = createUser();
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(LocalDate.of(2026, 5, 9))
            .build();

        dayRouteService.bookmarkDayRoute(dayRoute);

        assertThat(dayRoute.isBookmarked()).isTrue();
    }

    private User createUser() {
        return User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(UUID.randomUUID().toString())
            .nickname("tester")
            .profileImageUrl("https://example.com/profile.png")
            .build();
    }

    private DayRoute createBookmarkedDayRoute(User user, LocalDate date) {
        DayRoute dayRoute = DayRoute.builder()
            .user(user)
            .date(date)
            .build();
        dayRoute.toggleBookmarked();
        return dayRoute;
    }
}
