package backend.capstone.domain.mobility.analysis.visitedregion.repository;

import backend.capstone.domain.mobility.analysis.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.region.entity.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VisitedRegionRepository extends JpaRepository<VisitedRegion, Long> {

    @Query("""
        select vr
        from VisitedRegion vr
        where vr.dayRoute = :dayRoute
        order by vr.totalStaySeconds desc
        """)
    List<VisitedRegion> findByDayRouteOrderByTotalStaySecondsDesc(
        @Param("dayRoute") DayRoute dayRoute);

    Optional<VisitedRegion> findByDayRouteAndRegion(DayRoute dayRoute, Region region);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete
        from VisitedRegion vr
        where vr.dayRoute = :dayRoute
        """)
    void deleteByDayRoute(@Param("dayRoute") DayRoute dayRoute);
}
