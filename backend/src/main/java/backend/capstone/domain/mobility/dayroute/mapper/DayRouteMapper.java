package backend.capstone.domain.mobility.dayroute.mapper;

import backend.capstone.domain.mobility.dayroute.dto.DayRouteBookmarkListResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteDetailResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteMonthlyResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteSummaryResponse;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.gpspoint.entity.GpsPoint;
import backend.capstone.domain.user.entity.User;
import backend.capstone.global.util.DurationFormatUtils;
import backend.capstone.global.util.KstDateTimeUtils;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DayRouteMapper {

    public static DayRoute toEntity(User user, LocalDate date) {
        return DayRoute.builder()
            .user(user)
            .date(date)
            .build();
    }

    public static DayRouteDetailResponse toDayRouteDetailResponse(DayRoute dayRoute,
        List<GpsPoint> gpsPoints) {
        return DayRouteDetailResponse.builder()
            .date(dayRoute.getDate())
            .totalDistance(dayRoute.getTotalDistance())
            .title(dayRoute.getTitle())
            .memo(dayRoute.getMemo())
            .isBookmarked(dayRoute.isBookmarked())
//            .encodedPath(dayRoute.getEncodedPath())
//            .pathPointCount(dayRoute.getPathPointCount())
            .gpsPoints(gpsPoints.stream()
                .map(gp -> new DayRouteDetailResponse.GpsPointItem(
                    KstDateTimeUtils.toKstOffsetDateTime(gp.getRecordedAt()),
                    gp.getLatitude(),
                    gp.getLongitude()
                ))
                .toList())
            .build();
    }

    public static DayRouteSummaryResponse toDayRouteSummaryResponse(DayRoute dayRoute,
        List<String> visitedDongName) {
        return new DayRouteSummaryResponse(
            KstDateTimeUtils.toKstOffsetDateTime(dayRoute.getOutingTime()),
            KstDateTimeUtils.toKstOffsetDateTime(dayRoute.getEnterHomeTime()),
            dayRoute.getTotalOutingCount(),
            dayRoute.getTotalOutingSeconds(),
            DurationFormatUtils.formatOutingDurationText(dayRoute.getTotalOutingSeconds()),
            visitedDongName
        );
    }

    public static DayRouteMonthlyResponse toDayRouteMonthlyResponse(int year, int month,
        List<DayRoute> dayRoutes) {
        YearMonth yearMonth = YearMonth.of(year, month);
        // DayRoute 목록을 날짜 기준 map으로 변경
        Map<LocalDate, DayRoute> dayRouteMap = dayRoutes.stream()
            .collect(Collectors.toMap(DayRoute::getDate, Function.identity()));

        return DayRouteMonthlyResponse.builder()
            .year(year)
            .month(month)
            .days(yearMonth.atDay(1).datesUntil(yearMonth.atEndOfMonth().plusDays(1))
                .map(date -> {
                    DayRoute dayRoute = dayRouteMap.get(date);
                    return DayRouteMonthlyResponse.DayItem.builder()
                        .date(date)
                        .dayRouteExists(dayRoute != null)
                        .dayRoute(dayRoute == null ? null
                            : DayRouteMonthlyResponse.DayRouteItem.builder()
                                .hasPolyline(dayRoute.isHasPolyline())
                                .hasDetails(dayRoute.isHasDetails())
                                .isBookmarked(dayRoute.isBookmarked())
                                .build())
                        .build();
                })
                .toList())
            .build();
    }

    public static DayRouteBookmarkListResponse toDayRouteBookmarkListResponse(
        List<DayRoute> dayRoutes, boolean hasNext,
        LocalDate nextCursorDate, Map<Long, List<String>> visitedRegionMap
    ) {
        return DayRouteBookmarkListResponse.builder()
            .dayRouteCount(dayRoutes.size())
            .hasNext(hasNext)
            .nextCursorDate(nextCursorDate)
            .dayRoutes(dayRoutes.stream()
                .map(dayRoute -> DayRouteBookmarkListResponse.DayRouteBookmarkItem.builder()
                    .date(dayRoute.getDate())
                    .title(dayRoute.getTitle())
                    .visitedRegions(visitedRegionMap.getOrDefault(dayRoute.getId(), List.of()))
                    .build())
                .toList())
            .build();
    }
}
