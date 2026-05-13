package backend.capstone.domain.care.caredependent.mapper;

import backend.capstone.domain.care.caredependent.dto.CareDayRouteDetailResponse;
import backend.capstone.domain.care.caredependent.dto.CareDependentUserListResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteDetailResponse;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.user.entity.User;
import backend.capstone.global.util.KstDateTimeUtils;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CareDependentMapper {

    public static CareDependentUserListResponse toListResponse(
        List<User> dependentUsers,
        Map<Long, LatestGpsPoint> latestGpsPointByUserId
    ) {
        return CareDependentUserListResponse.builder()
            .dependentUserCount(dependentUsers.size())
            .dependentUsers(dependentUsers.stream()
                .map(dependentUser -> toItem(dependentUser,
                    latestGpsPointByUserId.get(dependentUser.getId())))
                .toList())
            .build();
    }

    private static CareDependentUserListResponse.CareDependentUserItem toItem(
        User dependentUser,
        LatestGpsPoint latestGpsPoint
    ) {
        return CareDependentUserListResponse.CareDependentUserItem.builder()
            .dependentUserId(dependentUser.getId())
            .nickname(dependentUser.getNickname())
            .profileImageUrl(dependentUser.getProfileImageUrl())
            .latestGpsPoint(toLatestGpsPointItem(latestGpsPoint))
            .build();
    }

    private static CareDependentUserListResponse.LatestGpsPointItem toLatestGpsPointItem(
        LatestGpsPoint latestGpsPoint
    ) {
        if (latestGpsPoint == null) {
            return null;
        }

        return CareDependentUserListResponse.LatestGpsPointItem.builder()
            .latitude(latestGpsPoint.getLatitude())
            .longitude(latestGpsPoint.getLongitude())
            .recordedAt(KstDateTimeUtils.toKstOffsetDateTime(latestGpsPoint.getRecordedAt()))
            .build();
    }

    public static CareDayRouteDetailResponse toCareDayRouteDetailResponse(
        DayRouteDetailResponse dayRouteDetailResponse
    ) {
        return CareDayRouteDetailResponse.builder()
            .date(dayRouteDetailResponse.date())
            .totalDistance(dayRouteDetailResponse.totalDistance())
            .gpsPoints(dayRouteDetailResponse.gpsPoints())
            .build();
    }
}
