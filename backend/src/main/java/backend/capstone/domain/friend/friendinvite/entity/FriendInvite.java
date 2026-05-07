package backend.capstone.domain.friend.friendinvite.entity;

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
    name = "friend_invite",
    indexes = {
        @Index(name = "idx_friend_invite_inviter_user_id", columnList = "inviter_user_id"),
        @Index(name = "idx_friend_invite_invite_code", columnList = "invite_code")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_user_id", nullable = false)
    private User inviterUser;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendInviteStatus status;

    @Column(nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_user_id")
    private User acceptedUser;

    private Instant acceptedAt;

    @Builder
    public FriendInvite(User inviterUser, String inviteCode, Instant expiresAt) {
        this.inviterUser = inviterUser;
        this.inviteCode = inviteCode;
        this.expiresAt = expiresAt;
        this.status = FriendInviteStatus.PENDING;
    }

    public void accept(User acceptedUser, Instant acceptedAt) {
        this.acceptedUser = acceptedUser;
        this.acceptedAt = acceptedAt;
        this.status = FriendInviteStatus.ACCEPTED;
    }

    public void expire() {
        this.status = FriendInviteStatus.EXPIRED;
    }

    public void cancel() {
        this.status = FriendInviteStatus.CANCELED;
    }
}
