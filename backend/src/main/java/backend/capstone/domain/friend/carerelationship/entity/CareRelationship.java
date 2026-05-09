package backend.capstone.domain.friend.carerelationship.entity;

import backend.capstone.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "care_relationship",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_care_relationship_guardian_dependent",
            columnNames = {"guardian_user_id", "dependent_user_id"}
        )
    },
    indexes = {
        @Index(name = "idx_care_relationship_guardian_user_id", columnList = "guardian_user_id"),
        @Index(
            name = "idx_care_relationship_dependent_user_id",
            columnList = "dependent_user_id"
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_user_id", nullable = false)
    private User guardianUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_user_id", nullable = false)
    private User dependentUser;

    private CareRelationship(User guardianUser, User dependentUser) {
        this.guardianUser = guardianUser;
        this.dependentUser = dependentUser;
    }

    public static CareRelationship of(User guardianUser, User dependentUser) {
        if (guardianUser.getId().equals(dependentUser.getId())) {
            throw new IllegalArgumentException();
        }
        return new CareRelationship(guardianUser, dependentUser);
    }
}
