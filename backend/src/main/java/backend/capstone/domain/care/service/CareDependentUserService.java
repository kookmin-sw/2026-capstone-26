package backend.capstone.domain.care.service;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.care.dto.CareDependentUserListResponse;
import backend.capstone.domain.care.mapper.CareDependentUserMapper;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import backend.capstone.domain.user.entity.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareDependentUserService {

    private final CareRelationshipRepository careRelationshipRepository;
    private final LatestGpsPointRepository latestGpsPointRepository;

    public CareDependentUserListResponse getDependentUsers(Long guardianUserId) {
        List<User> dependentUsers = careRelationshipRepository.findDependentUsersByGuardianUserId(
            guardianUserId);
        if (dependentUsers.isEmpty()) {
            return CareDependentUserMapper.toListResponse(List.of(), Map.of());
        }

        List<Long> dependentUserIds = dependentUsers.stream()
            .map(User::getId)
            .toList();
        List<LatestGpsPoint> latestGpsPoints = latestGpsPointRepository.findAllByUserIdIn(
            dependentUserIds);

        return CareDependentUserMapper.toListResponse(dependentUsers, toLatestGpsPointMap(latestGpsPoints));
    }

    private Map<Long, LatestGpsPoint> toLatestGpsPointMap(List<LatestGpsPoint> latestGpsPoints) {
        Map<Long, LatestGpsPoint> latestGpsPointByUserId = new HashMap<>();
        for (LatestGpsPoint latestGpsPoint : latestGpsPoints) {
            latestGpsPointByUserId.put(latestGpsPoint.getUserId(), latestGpsPoint);
        }
        return latestGpsPointByUserId;
    }
}
