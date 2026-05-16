package backend.capstone.domain.mobility.analysis.visitedregion.service;

import backend.capstone.domain.mobility.analysis.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.mobility.analysis.visitedregion.mapper.VisitedRegionMapper;
import backend.capstone.domain.mobility.analysis.visitedregion.repository.VisitedRegionRepository;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.place.entity.Place;
import backend.capstone.domain.region.entity.Region;
import backend.capstone.domain.region.repository.RegionRepository;
import backend.capstone.integration.kakao.local.dto.LegalDongRegion;
import backend.capstone.integration.kakao.local.service.KakaoSearchByCoordService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
            .orElseGet(() -> VisitedRegionMapper.toEntity(dayRoute, region.get()));

        visitedRegion.addStaySeconds(staySeconds);
        visitedRegionRepository.save(visitedRegion);
    }

    @Transactional(readOnly = true)
    public List<String> getVisitedRegionDongNames(DayRoute dayRoute) {
        return visitedRegionRepository.findByDayRouteOrderByTotalStaySecondsDesc(dayRoute).stream()
            .map(visitedRegion -> visitedRegion.getRegion().getDongName())
            .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getVisitedRegionDongNames(List<DayRoute> dayRoutes) {
        Map<Long, List<String>> visitedRegionMap = new LinkedHashMap<>();
        // LinkedHashMap을 써서 map의 key 순서는 입력 dayRoutes 순서(date 내림차순)를 그대로 유지한다.
        for (DayRoute dayRoute : dayRoutes) {
            visitedRegionMap.put(dayRoute.getId(), new ArrayList<>());
        }

        if (dayRoutes.isEmpty()) {
            return visitedRegionMap;
        }

        // 각 dayRoute 내부의 dong 순서는 totalStaySeconds 내림차순이다.
        visitedRegionRepository.findByDayRouteInOrderByTotalStaySecondsDesc(dayRoutes)
            .forEach(visitedRegion -> visitedRegionMap.get(visitedRegion.getDayRoute().getId())
                .add(visitedRegion.getRegion().getDongName()));

        return visitedRegionMap;
    }

    private long calculateStaySeconds(Instant startTime, Instant endTime) {
        if (startTime == null || endTime == null || endTime.isBefore(startTime)) {
            return 0;
        }
        return Duration.between(startTime, endTime).getSeconds();
    }
}
