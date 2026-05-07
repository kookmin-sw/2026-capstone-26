package backend.capstone.domain.friend.friendship.entity;

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
    name = "friendship",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_friendship_user_friend", columnNames = {"user_id",
            "friend_id"})
    },
    indexes = {
        @Index(name = "idx_friendship_user_id", columnList = "user_id"),
        @Index(name = "idx_friendship_friend_id", columnList = "friend_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    private Friendship(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }

    public static Friendship of(User userA, User userB) {
        validateUsers(userA, userB);

        if (userA.getId() < userB.getId()) {
            return new Friendship(userA, userB);
        }
        return new Friendship(userB, userA);
    }

    private static void validateUsers(User userA, User userB) {
        if (userA == null || userB == null) {
            throw new IllegalArgumentException("친구 관계 사용자 정보는 null일 수 없습니다.");
        }
        if (userA.getId() == null || userB.getId() == null) {
            throw new IllegalArgumentException("친구 관계를 생성하려면 사용자 ID가 필요합니다.");
        }
        if (userA.getId().equals(userB.getId())) {
            throw new IllegalArgumentException("자기 자신과 친구 관계를 생성할 수 없습니다.");
        }
    }
}
