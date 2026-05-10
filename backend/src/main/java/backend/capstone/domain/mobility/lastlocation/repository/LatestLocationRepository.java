package backend.capstone.domain.mobility.lastlocation.repository;

import backend.capstone.domain.mobility.lastlocation.entity.LatestLocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LatestLocationRepository extends JpaRepository<LatestLocation, Long> {

    List<LatestLocation> findAllByUserIdIn(List<Long> userIds);
}
