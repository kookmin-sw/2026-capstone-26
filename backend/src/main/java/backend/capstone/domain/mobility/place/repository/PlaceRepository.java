package backend.capstone.domain.mobility.place.repository;

import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.place.entity.Place;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("""
        select coalesce(max(rp.orderIndex), 0)
        from Place rp
        where rp.dayRoute = :dayRoute
        """)
    int findMaxOrderIdxByRoute(@Param("dayRoute") DayRoute dayRoute);

    List<Place> findByDayRouteOrderByOrderIndex(DayRoute dayRoute);

    boolean existsByDayRoute(DayRoute dayRoute);

    @Query("""
        select p
        from Place p
        join p.dayRoute dr
        where dr.user.id = :userId
          and dr.date between :startDate and :endDate
        """)
    List<Place> findByUserIdAndDateBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    Optional<Place> findByIdAndDayRoute(Long placeId, DayRoute dayRoute);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Place p
        set p.orderIndex = p.orderIndex - 1
        where p.dayRoute = :dayRoute
          and p.orderIndex > :orderIndex
        """)
    int decrementOrderIndexesGreaterThan(@Param("dayRoute") DayRoute dayRoute,
        @Param("orderIndex") int orderIndex);

}
