package backend.capstone.domain.care.mapper;

import backend.capstone.domain.care.dto.CareDayRouteDetailResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteDetailResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CareDayRouteMapper {

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
