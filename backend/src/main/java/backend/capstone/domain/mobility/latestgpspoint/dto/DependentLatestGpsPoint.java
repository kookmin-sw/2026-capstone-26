package backend.capstone.domain.mobility.latestgpspoint.dto;

import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import java.time.Instant;

public record DependentLatestGpsPoint(
    Long dependentUserId,
    double latitude,
    double longitude,
    Instant recordedAt
) {

    public static DependentLatestGpsPoint from(LatestGpsPoint latestGpsPoint) {
        return new DependentLatestGpsPoint(
            latestGpsPoint.getUserId(),
            latestGpsPoint.getLatitude(),
            latestGpsPoint.getLongitude(),
            latestGpsPoint.getRecordedAt()
        );
    }
}
