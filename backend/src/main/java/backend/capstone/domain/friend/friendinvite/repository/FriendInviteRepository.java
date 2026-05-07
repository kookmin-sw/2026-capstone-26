package backend.capstone.domain.friend.friendinvite.repository;

import backend.capstone.domain.friend.friendinvite.entity.FriendInvite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendInviteRepository extends JpaRepository<FriendInvite, Long> {

    Optional<FriendInvite> findByInviteCode(String inviteCode);
}
