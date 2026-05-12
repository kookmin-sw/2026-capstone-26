package backend.capstone.domain.visitedregion.mapper;

import backend.capstone.domain.dayroute.entity.DayRoute;
import backend.capstone.domain.region.entity.Region;
import backend.capstone.domain.visitedregion.entity.VisitedRegion;
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
