package backend.capstone.domain.mobility.latestgpspoint.repository;

import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LatestGpsPointRepository extends JpaRepository<LatestGpsPoint, Long> {

    List<LatestGpsPoint> findAllByUserIdIn(List<Long> userIds);
}
