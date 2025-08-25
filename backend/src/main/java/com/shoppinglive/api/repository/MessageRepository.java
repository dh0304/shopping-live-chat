package com.shoppinglive.api.repository;

import com.shoppinglive.api.dto.MessageResponse;
import com.shoppinglive.api.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT new com.shoppinglive.api.dto.MessageResponse(m.id, m.user.nickname, m.content, m.createdDate) " +
           "FROM Message m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.createdDate DESC LIMIT 30")
    List<MessageResponse> findRecentMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}