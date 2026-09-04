package com.example.backend.repository.post.saved;

import com.example.backend.entity.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

public interface SavedPostRepository extends JpaRepository<SavedPost, String>, SavedPostRepositoryCustom {
    Optional<SavedPost> findByUserIdAndPostId(String userId, String postId);

    
    boolean existsByUserIdAndPostIdAndDeleted(String userId, String postId, Integer deleted);

    @Query("SELECT s.post.id FROM SavedPost s WHERE s.user.id = :userId AND s.post.id IN :postIds AND s.deleted = :deleted")
    List<String> findSavedPostIdsByUserIdAndPostIdsIn(@Param("userId") String userId, @Param("postIds") Collection<String> postIds, @Param("deleted") Integer deleted);
}
