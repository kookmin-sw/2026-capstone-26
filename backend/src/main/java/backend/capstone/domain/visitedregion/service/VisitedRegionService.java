package backend.capstone.domain.visitedregion.service;

import backend.capstone.domain.dayroute.entity.DayRoute;
import backend.capstone.domain.kakaoplace.dto.LegalDongRegion;
import backend.capstone.domain.kakaoplace.service.KakaoSearchByCoordService;
import backend.capstone.domain.place.entity.Place;
import backend.capstone.domain.region.entity.Region;
import backend.capstone.domain.region.repository.RegionRepository;
import backend.capstone.domain.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.visitedregion.repository.VisitedRegionRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitedRegionService {

    private final KakaoSearchByCoordService kakaoSearchByCoordService;
    private final RegionRepository regionRepository;
    private final VisitedRegionRepository visitedRegionRepository;

    //승격된 AUTO Place 1개를 받아서, 그 장소가 속한 법정동 기준으로 visitedRegion 생성 및 누적 갱신
    @Transactional
    public void recordAutoVisit(DayRoute dayRoute, Place place) {
        long staySeconds = calculateStaySeconds(place.getStartTime(), place.getEndTime());
        if (staySeconds <= 0) {
            log.debug("visited region 저장을 건너뜁니다. staySeconds={}, placeId={}",
                staySeconds, place.getId());
            return;
        }

        //place 위경도로 법정동 코드 조회
        Optional<LegalDongRegion> legalDongRegion = kakaoSearchByCoordService.searchLegalDongRegion(
            place.getLatitude(), place.getLongitude());

        if (legalDongRegion.isEmpty()) {
            log.warn("법정동 코드 조회에 실패하여 visited region 저장을 건너뜁니다. placeId={}, lat={}, lon={}",
                place.getId(), place.getLatitude(), place.getLongitude());
            return;
        }

        String legalDongCode = legalDongRegion.get().legalDongCode();
        Optional<Region> region = regionRepository.findByLegalDongCode(
            legalDongCode); //region 마스터 조회

        if (region.isEmpty()) {
            log.warn(
                "region seed에 없는 법정동 코드입니다. visited region 저장을 건너뜁니다. placeId={}, legalDongCode={}",
                place.getId(), legalDongCode);
            return;
        }

        VisitedRegion visitedRegion = visitedRegionRepository.findByDayRouteAndRegion(dayRoute,
                region.get())
            .orElseGet(() -> VisitedRegion.builder()
                .dayRoute(dayRoute)
                .region(region.get())
                .firstVisitedAt(place.getStartTime())
                .totalStaySeconds(0)
                .build());

        visitedRegion.updateFirstVisitedAt(place.getStartTime());
        visitedRegion.addStaySeconds(staySeconds);
        visitedRegionRepository.save(visitedRegion);
    }

    private long calculateStaySeconds(Instant startTime, Instant endTime) {
        if (startTime == null || endTime == null || endTime.isBefore(startTime)) {
            return 0;
        }
        return Duration.between(startTime, endTime).getSeconds();
    }
}
