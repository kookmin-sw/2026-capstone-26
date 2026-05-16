package backend.capstone.domain.care.sse.dto;

import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.global.util.KstDateTimeUtils;
import java.time.OffsetDateTime;

public record CareLocationUpdatedPayload(
    Long dependentUserId,
    LatestGpsPointPayload latestGpsPoint
) {

    public static CareLocationUpdatedPayload from(LatestGpsPoint latestGpsPoint) {
        return new CareLocationUpdatedPayload(
            latestGpsPoint.getUserId(),
            new LatestGpsPointPayload(
                latestGpsPoint.getLatitude(),
                latestGpsPoint.getLongitude(),
                KstDateTimeUtils.toKstOffsetDateTime(latestGpsPoint.getRecordedAt())
            )
        );
    }

    public record LatestGpsPointPayload(
        double latitude,
        double longitude,
        OffsetDateTime recordedAt
    ) {

    }
}
