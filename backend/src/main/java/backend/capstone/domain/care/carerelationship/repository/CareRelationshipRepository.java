package backend.capstone.domain.care.carerelationship.repository;

import backend.capstone.domain.care.carerelationship.entity.CareRelationship;
import backend.capstone.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CareRelationshipRepository extends JpaRepository<CareRelationship, Long> {

    boolean existsByGuardianUserAndDependentUser(User guardianUser, User dependentUser);

    @Query("""
            select cr.dependentUser.id
            from CareRelationship cr
            where cr.guardianUser.id = :guardianUserId
        """)
    List<Long> findDependentUserIdsByGuardianUserId(@Param("guardianUserId") Long guardianUserId);
}
