package backend.capstone.domain.mobility.analysis.visitedregion.mapper;

import backend.capstone.domain.mobility.analysis.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.region.entity.Region;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitedRegionMapper {

    public static VisitedRegion toEntity(DayRoute dayRoute, Region region) {
        return VisitedRegion.builder()
            .dayRoute(dayRoute)
            .region(region)
            .totalStaySeconds(0)
            .build();
    }
}
