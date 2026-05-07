package backend.capstone.domain.friend.friendship.repository;

import backend.capstone.domain.friend.friendship.entity.Friendship;
import backend.capstone.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    boolean existsByUserAndFriend(User user, User friend);

    List<Friendship> findAllByUserOrFriend(User user, User friend);
}
