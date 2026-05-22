package backend.capstone.domain.mobility.statics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import backend.capstone.domain.mobility.analysis.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.mobility.analysis.visitedregion.repository.VisitedRegionRepository;
import backend.capstone.domain.mobility.place.entity.Place;
import backend.capstone.domain.mobility.place.entity.PlaceSource;
import backend.capstone.domain.mobility.place.repository.PlaceRepository;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import backend.capstone.domain.region.entity.Region;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VisitStatisticsServiceTest {

    @InjectMocks
    private VisitStatisticsService visitStatisticsService;

    @Mock
    private VisitedRegionRepository visitedRegionRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Test
    void 방문통계는_방문동네_상위3개와_그외_그리고_장소_상위5개를_반환한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        given(visitedRegionRepository.findByUserIdAndDateBetween(
            1L, LocalDate.of(2026, 5, 12), today))
            .willReturn(List.of(
                createVisitedRegion("수유동"),
                createVisitedRegion("수유동"),
                createVisitedRegion("수유동"),
                createVisitedRegion("정릉동"),
                createVisitedRegion("정릉동"),
                createVisitedRegion("성북동"),
                createVisitedRegion("돈암동")
            ));
        given(placeRepository.findByUserIdAndDateBetween(1L, LocalDate.of(2026, 5, 12), today))
            .willReturn(List.of(
                createPlace("스타벅스 수유역점", "서울특별시 성북구 정릉로 77"),
                createPlace("스타벅스 수유역점", "서울특별시 성북구 정릉로 77"),
                createPlace("도서관", "서울특별시 성북구 보문로 1")
            ));

        var response = visitStatisticsService.getVisitStatistics(1L, StatisticPeriod.WEEK, today);

        assertThat(response.period()).isEqualTo(StatisticPeriod.WEEK);
        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 5, 12));
        assertThat(response.endDate()).isEqualTo(today);
        assertThat(response.visitedRegions().totalVisitCount()).isEqualTo(7);
        assertThat(response.visitedRegions().items()).hasSize(4);
        assertThat(response.visitedRegions().items().get(0).regionName()).isEqualTo("수유동");
        assertThat(response.visitedRegions().items().get(0).visitCount()).isEqualTo(3);
        assertThat(response.visitedRegions().items().get(0).ratio()).isEqualTo(42.9);
        assertThat(response.visitedRegions().items().get(0).displayRatio()).isEqualTo("43%");
        assertThat(response.visitedRegions().items().get(3).regionName()).isEqualTo("그 외");
        assertThat(response.visitedRegions().items().get(3).visitCount()).isEqualTo(1);
        assertThat(response.places().totalVisitCount()).isEqualTo(3);
        assertThat(response.places().items()).hasSize(2);
        assertThat(response.places().items().get(0).placeName()).isEqualTo("스타벅스 수유역점");
        assertThat(response.places().items().get(0).visitCount()).isEqualTo(2);
        assertThat(response.places().items().get(0).displayVisitCount()).isEqualTo("2회");
    }

    @Test
    void 방문통계는_방문기록이_없으면_빈목록을_반환한다() {
        LocalDate today = LocalDate.of(2026, 5, 18);
        given(visitedRegionRepository.findByUserIdAndDateBetween(
            1L, LocalDate.of(2026, 4, 19), today))
            .willReturn(List.of());
        given(placeRepository.findByUserIdAndDateBetween(1L, LocalDate.of(2026, 4, 19), today))
            .willReturn(List.of());

        var response = visitStatisticsService.getVisitStatistics(1L, StatisticPeriod.MONTH, today);

        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 4, 19));
        assertThat(response.visitedRegions().totalVisitCount()).isZero();
        assertThat(response.visitedRegions().items()).isEmpty();
        assertThat(response.places().totalVisitCount()).isZero();
        assertThat(response.places().items()).isEmpty();
    }

    private VisitedRegion createVisitedRegion(String dongName) {
        return VisitedRegion.builder()
            .region(Region.builder()
                .legalDongCode(dongName)
                .sidoName("서울특별시")
                .sigunguName("성북구")
                .dongName(dongName)
                .build())
            .totalStaySeconds(600L)
            .build();
    }

    private Place createPlace(String name, String roadAddress) {
        return Place.builder()
            .name(name)
            .roadAddress(roadAddress)
            .latitude(37.0)
            .longitude(127.0)
            .orderIndex(1)
            .source(PlaceSource.AUTO)
            .build();
    }
}
