package backend.capstone.domain.mobility.analysis.ongoinghomestatus.repository;

import backend.capstone.domain.mobility.analysis.ongoinghomestatus.entity.OngoingHomeStatus;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OngoingHomeStatusRepository extends JpaRepository<OngoingHomeStatus, Long> {

    Optional<OngoingHomeStatus> findByDayRoute(DayRoute dayRoute);
}
