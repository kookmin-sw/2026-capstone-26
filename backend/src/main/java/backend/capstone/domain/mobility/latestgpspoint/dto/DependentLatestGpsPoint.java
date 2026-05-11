package backend.capstone.domain.mobility.latestgpspoint.dto;

import java.time.OffsetDateTime;

public record DependentLatestGpsPoint(
    Long dependentUserId,
    LatestGpsPoint latestGpsPoint
) {

    public record LatestGpsPoint(
        double latitude,
        double longitude,
        OffsetDateTime recordedAt
    ) {

    }
}
