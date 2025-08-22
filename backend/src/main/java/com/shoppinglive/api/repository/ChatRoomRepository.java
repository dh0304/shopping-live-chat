package com.shoppinglive.api.repository;

import com.shoppinglive.api.entity.ChatRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

//    @Query(value = "SELECT * FROM chat_rooms WHERE chat_room_id = :chatRoomId FOR UPDATE", nativeQuery = true)
//    Optional<ChatRoom> findByIdWithLock(@Param("chatRoomId") String chatRoomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.chatRoomId = :chatRoomId")
    Optional<ChatRoom> findByIdWithPessimisticLock(@Param("chatRoomId") String chatRoomId);
}