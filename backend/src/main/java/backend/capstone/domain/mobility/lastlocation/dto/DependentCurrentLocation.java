package backend.capstone.domain.mobility.lastlocation.dto;

import backend.capstone.domain.mobility.lastlocation.entity.LatestLocation;
import java.time.Instant;

public record DependentCurrentLocation(
    Long dependentUserId,
    double latitude,
    double longitude,
    Instant recordedAt
) {

    public static DependentCurrentLocation from(LatestLocation latestLocation) {
        return new DependentCurrentLocation(
            latestLocation.getUserId(),
            latestLocation.getLatitude(),
            latestLocation.getLongitude(),
            latestLocation.getRecordedAt()
        );
    }
}
