package backend.capstone.domain.bookmarkplace.repository;

import backend.capstone.domain.bookmarkplace.entity.BookmarkPlace;
import backend.capstone.domain.bookmarkplace.entity.BookmarkPlaceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkPlaceRepository extends JpaRepository<BookmarkPlace, Long> {

    @Query("""
        select bp
        from BookmarkPlace bp
        where bp.user.id = :userId
        order by bp.id asc
        """)
    List<BookmarkPlace> findByUserIdOrderByIdAsc(@Param("userId") Long userId);

    @Query("""
        select bp
        from BookmarkPlace bp
        where bp.id = :bookmarkPlaceId
            and bp.user.id = :userId
        """)
    Optional<BookmarkPlace> findByIdAndUserId(@Param("bookmarkPlaceId") Long bookmarkPlaceId,
        @Param("userId") Long userId);

    @Query("""
        select bp
        from BookmarkPlace bp
        where bp.user.id = :userId
            and bp.type = :type
        """)
    Optional<BookmarkPlace> findByUserIdAndType(@Param("userId") Long userId,
        @Param("type") BookmarkPlaceType type);

    @Query("""
        select count(bp) > 0
        from BookmarkPlace bp
        where bp.user.id = :userId
            and bp.type = :type
        """)
    boolean existsByUserIdAndType(@Param("userId") Long userId,
        @Param("type") BookmarkPlaceType type);

    @Query("""
        select count(bp) > 0
        from BookmarkPlace bp
        where bp.user.id = :userId
            and bp.type = :type
            and bp.id <> :bookmarkPlaceId
        """)
    boolean existsByUserIdAndTypeAndIdNot(@Param("userId") Long userId,
        @Param("type") BookmarkPlaceType type, @Param("bookmarkPlaceId") Long bookmarkPlaceId);
}
