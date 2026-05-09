package backend.capstone.domain.friend.carerelationshipinvite.repository;

import backend.capstone.domain.friend.carerelationshipinvite.entity.CareRelationshipInvite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRelationshipInviteRepository extends JpaRepository<CareRelationshipInvite, Long> {

    boolean existsByInviteCode(String inviteCode);

    Optional<CareRelationshipInvite> findByInviteCode(String inviteCode);
}
