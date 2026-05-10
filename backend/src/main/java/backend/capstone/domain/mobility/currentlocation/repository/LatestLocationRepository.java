package backend.capstone.domain.mobility.currentlocation.repository;

import backend.capstone.domain.mobility.currentlocation.entity.LatestLocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LatestLocationRepository extends JpaRepository<LatestLocation, Long> {

    List<LatestLocation> findAllByUserIdIn(List<Long> userIds);
}
