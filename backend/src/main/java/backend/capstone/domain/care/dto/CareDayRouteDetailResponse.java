package backend.capstone.domain.care.dto;

import backend.capstone.domain.mobility.dayroute.dto.DayRouteDetailResponse.GpsPointItem;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CareDayRouteDetailResponse(
    LocalDate date,
    double totalDistance,
    List<GpsPointItem> gpsPoints
) {

}
