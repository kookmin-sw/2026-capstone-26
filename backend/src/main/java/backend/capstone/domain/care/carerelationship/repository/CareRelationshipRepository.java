package backend.capstone.domain.care.carerelationship.repository;

import backend.capstone.domain.care.carerelationship.entity.CareRelationship;
import backend.capstone.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CareRelationshipRepository extends JpaRepository<CareRelationship, Long> {

    boolean existsByGuardianUserAndDependentUser(User guardianUser, User dependentUser);

    boolean existsByGuardianUserIdAndDependentUserId(Long guardianUserId, Long dependentUserId);

    @Query("""
            select cr.guardianUser.id
            from CareRelationship cr
            where cr.dependentUser.id = :dependentUserId
        """)
    List<Long> findGuardianUserIdsByDependentUserId(@Param("dependentUserId") Long dependentUserId);

    @Query("""
            select cr.dependentUser
            from CareRelationship cr
            where cr.guardianUser.id = :guardianUserId
        """)
    List<User> findDependentUsersByGuardianUserId(@Param("guardianUserId") Long guardianUserId);
}
