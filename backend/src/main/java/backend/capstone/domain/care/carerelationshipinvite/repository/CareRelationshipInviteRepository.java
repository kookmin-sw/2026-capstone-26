package backend.capstone.domain.care.carerelationshipinvite.repository;

import backend.capstone.domain.care.carerelationshipinvite.entity.CareRelationshipInvite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRelationshipInviteRepository extends
    JpaRepository<CareRelationshipInvite, Long> {

    boolean existsByInviteCode(String inviteCode);

    Optional<CareRelationshipInvite> findByInviteCode(String inviteCode);
}
