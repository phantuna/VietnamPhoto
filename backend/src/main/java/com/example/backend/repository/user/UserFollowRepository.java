package com.example.backend.repository.user;

import com.example.backend.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, String> {

    /** Tìm follow record còn active (deleted=0) */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId AND uf.deleted = 0")
    Optional<UserFollow> findActiveFollow(@Param("followerId") String followerId, @Param("followingId") String followingId);

    /** Tìm follow record kể cả đã soft-delete (để reactivate thay vì tạo mới) */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
    Optional<UserFollow> findByFollowerIdAndFollowingId(@Param("followerId") String followerId, @Param("followingId") String followingId);

    /** Kiểm tra đang follow (chỉ active) */
    @Query("SELECT COUNT(uf) > 0 FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId AND uf.deleted = 0")
    boolean existsByFollowerIdAndFollowingId(@Param("followerId") String followerId, @Param("followingId") String followingId);

    /** Lấy danh sách followers (người theo dõi mình) */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.following.id = :userId AND uf.deleted = 0")
    List<UserFollow> findFollowersByUserId(@Param("userId") String userId);

    /** Lấy danh sách following (mình đang theo dõi ai) */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :userId AND uf.deleted = 0")
    List<UserFollow> findFollowingByUserId(@Param("userId") String userId);

    long countByFollowingIdAndDeleted(String followingId, Integer deleted);
    long countByFollowerIdAndDeleted(String followerId, Integer deleted);
}
