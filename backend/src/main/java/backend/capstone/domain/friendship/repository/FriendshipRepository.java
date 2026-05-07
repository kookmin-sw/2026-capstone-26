package backend.capstone.domain.friendship.repository;

import backend.capstone.domain.friendship.entity.Friendship;
import backend.capstone.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findAllByUserOrFriend(User user, User friend);
}
