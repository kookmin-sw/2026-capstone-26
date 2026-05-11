package backend.capstone.domain.mobility.latestgpspoint.service;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.mobility.latestgpspoint.dto.DependentLatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.mapper.LatestGpsPointMapper;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DependentLatestGpsPointService {

    private final CareRelationshipRepository careRelationshipRepository;
    private final LatestGpsPointRepository latestGpsPointRepository;

    public List<DependentLatestGpsPoint> getDependentCurrentLocations(Long guardianUserId) {
        List<Long> dependentUserIds = careRelationshipRepository.findDependentUserIdsByGuardianUserId(
            guardianUserId);
        if (dependentUserIds.isEmpty()) {
            return List.of();
        }

        List<LatestGpsPoint> latestGpsPoints = latestGpsPointRepository.findAllByUserIdIn(
            dependentUserIds);
        Map<Long, LatestGpsPoint> latestLocationByUserId = toLatestLocationMap(latestGpsPoints);

        List<DependentLatestGpsPoint> currentLocations = new ArrayList<>();
        for (Long dependentUserId : dependentUserIds) {
            LatestGpsPoint latestGpsPoint = latestLocationByUserId.get(dependentUserId);
            if (latestGpsPoint == null) {
                continue;
            }

            currentLocations.add(LatestGpsPointMapper.toLatestGpsPointResponse(latestGpsPoint));
        }

        return currentLocations;
    }

    private Map<Long, LatestGpsPoint> toLatestLocationMap(List<LatestGpsPoint> latestGpsPoints) {
        Map<Long, LatestGpsPoint> latestLocationByUserId = new HashMap<>();
        for (LatestGpsPoint latestGpsPoint : latestGpsPoints) {
            latestLocationByUserId.put(latestGpsPoint.getUserId(), latestGpsPoint);
        }
        return latestLocationByUserId;
    }
}
