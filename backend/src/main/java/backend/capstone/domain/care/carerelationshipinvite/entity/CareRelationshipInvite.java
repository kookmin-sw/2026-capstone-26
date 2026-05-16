package backend.capstone.domain.care.carerelationshipinvite.entity;

import backend.capstone.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "care_relationship_invite",
    indexes = {
        @Index(
            name = "idx_care_relationship_invite_guardian_user_id",
            columnList = "guardian_user_id"
        ),
        @Index(name = "idx_care_relationship_invite_invite_code", columnList = "invite_code")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareRelationshipInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_user_id", nullable = false)
    private User guardianUser;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareRelationshipInviteStatus status;

    @Column(nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_user_id")
    private User dependentUser;

    private Instant acceptedAt;

    @Builder
    public CareRelationshipInvite(User guardianUser, String inviteCode, Instant expiresAt) {
        this.guardianUser = guardianUser;
        this.inviteCode = inviteCode;
        this.expiresAt = expiresAt;
        this.status = CareRelationshipInviteStatus.PENDING;
    }

    public void accept(User dependentUser, Instant acceptedAt) {
        this.dependentUser = dependentUser;
        this.acceptedAt = acceptedAt;
        this.status = CareRelationshipInviteStatus.ACCEPTED;
    }

    public void expire() {
        this.status = CareRelationshipInviteStatus.EXPIRED;
    }

    public void cancel() {
        this.status = CareRelationshipInviteStatus.CANCELED;
    }
}
