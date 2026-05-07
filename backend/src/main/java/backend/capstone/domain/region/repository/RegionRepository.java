package backend.capstone.domain.region.repository;

import backend.capstone.domain.region.entity.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByLegalDongCode(String legalDongCode);
}
