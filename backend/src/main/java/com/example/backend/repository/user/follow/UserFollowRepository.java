package com.example.backend.repository.user;

import com.example.backend.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, String>, UserFollowRepositoryCustom {


    /**
     * Tìm follow record còn active (deleted=0)
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId AND uf.deleted = 0")
    Optional<UserFollow> findActiveFollow(@Param("followerId") String followerId, @Param("followingId") String followingId);

    /**
     * Tìm follow record kể cả đã soft-delete (để reactivate thay vì tạo mới)
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
    Optional<UserFollow> findByFollowerIdAndFollowingId(@Param("followerId") String followerId, @Param("followingId") String followingId);

    /**
     * Kiểm tra đang follow (chỉ active)
     */
    @Query("SELECT COUNT(uf) > 0 FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId AND uf.deleted = 0")
    boolean existsByFollowerIdAndFollowingId(@Param("followerId") String followerId, @Param("followingId") String followingId);


    long countByFollowingIdAndDeleted(String followingId, Integer deleted);

    long countByFollowerIdAndDeleted(String followerId, Integer deleted);

    @Query("SELECT uf.following.id FROM UserFollow uf WHERE uf.follower.id = :userId AND uf.deleted = 0")
    List<String> findFollowingUserIds(@Param("userId") String userId);

    /**
     * Danh sách userId đã follow nhau 2 chiều với userId của mình
     */
    @Query("""
        SELECT uf1.following.id FROM UserFollow uf1
        WHERE uf1.follower.id = :userId AND uf1.deleted = 0
        AND EXISTS (
            SELECT 1 FROM UserFollow uf2
            WHERE uf2.follower.id = uf1.following.id
            AND uf2.following.id = :userId
            AND uf2.deleted = 0
        )
    """)
    List<String> findMutualFollowUserIds(@Param("userId") String userId);
}