package backend.capstone.domain.mobility.currentlocation.service;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.mobility.currentlocation.dto.CurrentLocationCacheValue;
import backend.capstone.domain.mobility.currentlocation.dto.DependentCurrentLocation;
import backend.capstone.domain.mobility.currentlocation.exception.CurrentLocationCacheException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DependentCurrentLocationService {

    private final CareRelationshipRepository careRelationshipRepository;
    private final CurrentLocationCacheService currentLocationCacheService;

    public List<DependentCurrentLocation> getDependentCurrentLocations(Long guardianUserId) {
        List<Long> dependentUserIds = careRelationshipRepository.findDependentUserIdsByGuardianUserId(
            guardianUserId);
        if (dependentUserIds.isEmpty()) {
            return List.of();
        }

        Map<Long, CurrentLocationCacheValue> cachedLocations = loadCachedLocations(
            dependentUserIds);

        List<DependentCurrentLocation> currentLocations = new ArrayList<>();
        for (Long dependentUserId : dependentUserIds) {
            CurrentLocationCacheValue cachedLocation = cachedLocations.get(dependentUserId);
            //redis에 최신 좌표가 없으면 최종 반환 리스트에 추가안함
            if (cachedLocation == null) {
                continue;
            }

            currentLocations.add(DependentCurrentLocation.from(dependentUserId, cachedLocation));
        }

        return currentLocations;
    }

    private Map<Long, CurrentLocationCacheValue> loadCachedLocations(List<Long> dependentUserIds) {
        try {
            return currentLocationCacheService.getLatestLocations(dependentUserIds);
        } catch (CurrentLocationCacheException e) {
            log.warn("Redis에서 보호 대상 최신 위치를 조회하지 못했습니다. dependentUserCount={}",
                dependentUserIds.size(), e);
            return Map.of();
        }
    }
}
