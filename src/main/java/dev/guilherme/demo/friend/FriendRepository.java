package dev.guilherme.demo.friend;

import dev.guilherme.demo.friend.dtos.RankingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friendship, Long> {

    @Query("""
    SELECT new dev.guilherme.demo.friend.dtos.RankingDTO(
        u.id,
        u.name,
        CAST(COALESCE(SUM(s.durationMinutes)/60.0, 0.0) AS DOUBLE)
    )
    FROM UserModel u
    LEFT JOIN StudySessionModel s
        ON s.user.id = u.id
        AND s.startTime >= :startOfWeek
    GROUP BY u.id, u.name
    ORDER BY SUM(s.durationMinutes) DESC
""")
    List<RankingDTO> getWeeklyRanking(@Param("startOfWeek") LocalDateTime startOfWeek);

    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);

    @Query("SELECT f FROM Friendship f WHERE (f.user.id = :userId OR f.friend.id = :userId)")
    List<Friendship> findByUserIdOrFriendId(@Param("userId") Long userId, @Param("userId") Long friendId);
}
