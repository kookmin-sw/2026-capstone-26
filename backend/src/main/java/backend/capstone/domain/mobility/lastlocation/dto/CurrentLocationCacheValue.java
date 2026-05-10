package backend.capstone.domain.mobility.lastlocation.dto;

import java.time.Instant;

public record CurrentLocationCacheValue(
    double latitude,
    double longitude,
    Instant recordedAt
) {

}
