package com.example.backend.repository.chat;

import com.example.backend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    /**
     * Tìm conversation giữa 2 người (không quan tâm thứ tự truyền vào).
     */
    @Query("""
            SELECT c FROM Conversation c
            WHERE (c.user1.id = :idA AND c.user2.id = :idB)
               OR (c.user1.id = :idB AND c.user2.id = :idA)
            """)
    Optional<Conversation> findBetweenUsers(@Param("idA") String idA, @Param("idB") String idB);

    /**
     * Lấy tất cả conversations của 1 user, sắp xếp theo lần chỉnh sửa gần nhất.
     */
    @Query("""
            SELECT c FROM Conversation c
            WHERE c.user1.id = :userId OR c.user2.id = :userId
            ORDER BY c.modifiedDate DESC
            """)
    List<Conversation> findAllByUserId(@Param("userId") String userId);
}
