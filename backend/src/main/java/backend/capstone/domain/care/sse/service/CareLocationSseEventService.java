package backend.capstone.domain.care.sse.service;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.care.sse.dto.CareLocationUpdatedPayload;
import backend.capstone.domain.care.sse.dto.CareSseEventType;
import backend.capstone.domain.care.sse.registry.CareSseEmitterRegistry;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareLocationSseEventService {

    private final CareRelationshipRepository careRelationshipRepository;
    private final CareSseEmitterRegistry careSseEmitterRegistry;

    public void publishLocationUpdated(LatestGpsPoint latestGpsPoint) {
        if (latestGpsPoint == null) {
            return;
        }

        List<Long> guardianUserIds = careRelationshipRepository.findGuardianUserIdsByDependentUserId(
            latestGpsPoint.getUserId());
        if (guardianUserIds.isEmpty()) {
            return;
        }

        CareLocationUpdatedPayload payload = CareLocationUpdatedPayload.from(latestGpsPoint);
        for (Long guardianUserId : guardianUserIds) {
            careSseEmitterRegistry.publish(guardianUserId, CareSseEventType.LOCATION_UPDATED,
                payload);
        }
    }
}
