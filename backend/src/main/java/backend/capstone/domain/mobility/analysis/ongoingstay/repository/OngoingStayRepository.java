package backend.capstone.domain.mobility.analysis.ongoingstay.repository;

import backend.capstone.domain.mobility.analysis.ongoingstay.entity.OngoingStay;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OngoingStayRepository extends JpaRepository<OngoingStay, Long> {

    Optional<OngoingStay> findByDayRoute(DayRoute dayRoute);

    @Query("""
        select distinct os.dayRoute.id
        from OngoingStay os
        where os.dayRoute.endTime <= :now
        """)
    List<Long> findDayRouteIdsWithEndedOngoingStay(@Param("now") Instant now);
}
