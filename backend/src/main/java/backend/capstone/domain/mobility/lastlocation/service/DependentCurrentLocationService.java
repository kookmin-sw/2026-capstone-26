package backend.capstone.domain.mobility.lastlocation.service;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.mobility.lastlocation.dto.DependentCurrentLocation;
import backend.capstone.domain.mobility.lastlocation.entity.LatestLocation;
import backend.capstone.domain.mobility.lastlocation.repository.LatestLocationRepository;
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
public class DependentCurrentLocationService {

    private final CareRelationshipRepository careRelationshipRepository;
    private final LatestLocationRepository latestLocationRepository;

    public List<DependentCurrentLocation> getDependentCurrentLocations(Long guardianUserId) {
        List<Long> dependentUserIds = careRelationshipRepository.findDependentUserIdsByGuardianUserId(
            guardianUserId);
        if (dependentUserIds.isEmpty()) {
            return List.of();
        }

        List<LatestLocation> latestLocations = latestLocationRepository.findAllByUserIdIn(
            dependentUserIds);
        Map<Long, LatestLocation> latestLocationByUserId = toLatestLocationMap(latestLocations);

        List<DependentCurrentLocation> currentLocations = new ArrayList<>();
        for (Long dependentUserId : dependentUserIds) {
            LatestLocation latestLocation = latestLocationByUserId.get(dependentUserId);
            if (latestLocation == null) {
                continue;
            }

            currentLocations.add(DependentCurrentLocation.from(latestLocation));
        }

        return currentLocations;
    }

    private Map<Long, LatestLocation> toLatestLocationMap(List<LatestLocation> latestLocations) {
        Map<Long, LatestLocation> latestLocationByUserId = new HashMap<>();
        for (LatestLocation latestLocation : latestLocations) {
            latestLocationByUserId.put(latestLocation.getUserId(), latestLocation);
        }
        return latestLocationByUserId;
    }
}
