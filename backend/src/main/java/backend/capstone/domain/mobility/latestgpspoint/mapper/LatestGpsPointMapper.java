package backend.capstone.domain.mobility.latestgpspoint.mapper;

import backend.capstone.domain.mobility.latestgpspoint.dto.DependentLatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.global.util.KstDateTimeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LatestGpsPointMapper {

    public static DependentLatestGpsPoint toLatestGpsPointResponse(LatestGpsPoint latestGpsPoint) {
        return new DependentLatestGpsPoint(
            latestGpsPoint.getUserId(),
            new DependentLatestGpsPoint.LatestGpsPoint(
                latestGpsPoint.getLatitude(),
                latestGpsPoint.getLongitude(),
                KstDateTimeUtils.toKstOffsetDateTime(latestGpsPoint.getRecordedAt())
            )
        );
    }
}
