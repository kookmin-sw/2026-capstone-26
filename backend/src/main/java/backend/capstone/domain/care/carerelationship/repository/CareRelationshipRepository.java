package backend.capstone.domain.care.carerelationship.repository;

import backend.capstone.domain.care.carerelationship.entity.CareRelationship;
import backend.capstone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRelationshipRepository extends JpaRepository<CareRelationship, Long> {

    boolean existsByGuardianUserAndDependentUser(User guardianUser, User dependentUser);
}
