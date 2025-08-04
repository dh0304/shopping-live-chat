package com.shoppinglive.api.repository;

import com.shoppinglive.api.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    
    @Modifying
    @Query("UPDATE ChatRoom c SET c.userCount = c.userCount + 1 WHERE c.chatRoomId = :chatRoomId")
    int incrementUserCount(@Param("chatRoomId") String chatRoomId);
    
    @Modifying
    @Query("UPDATE ChatRoom c SET c.userCount = c.userCount - 1 WHERE c.chatRoomId = :chatRoomId AND c.userCount > 0")
    int decrementUserCount(@Param("chatRoomId") String chatRoomId);
}