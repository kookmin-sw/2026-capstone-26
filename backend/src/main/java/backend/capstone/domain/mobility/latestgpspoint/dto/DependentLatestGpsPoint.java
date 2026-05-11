package backend.capstone.domain.mobility.latestgpspoint.dto;

import java.time.Instant;

public record DependentLatestGpsPoint(
    Long dependentUserId,
    LatestGpsPoint latestGpsPoint
) {

    public record LatestGpsPoint(
        double latitude,
        double longitude,
        Instant recordedAt
    ) {

    }
}
