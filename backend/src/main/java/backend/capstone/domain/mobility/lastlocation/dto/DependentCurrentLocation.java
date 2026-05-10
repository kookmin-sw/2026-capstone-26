package backend.capstone.domain.mobility.lastlocation.dto;

import java.time.Instant;

public record DependentCurrentLocation(
    Long dependentUserId,
    double latitude,
    double longitude,
    Instant recordedAt
) {

    public static DependentCurrentLocation from(Long dependentUserId,
        CurrentLocationCacheValue cacheValue) {
        return new DependentCurrentLocation(
            dependentUserId,
            cacheValue.latitude(),
            cacheValue.longitude(),
            cacheValue.recordedAt()
        );
    }
}
