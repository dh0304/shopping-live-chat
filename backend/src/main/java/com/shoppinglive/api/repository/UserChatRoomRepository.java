package com.shoppinglive.api.repository;

import com.shoppinglive.api.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    
    @Query("SELECT ucr FROM UserChatRoom ucr WHERE ucr.user.userId = :userId AND ucr.chatRoom.chatRoomId = :chatRoomId")
    Optional<UserChatRoom> findByUserIdAndRoomId(@Param("userId") String userId, @Param("chatRoomId") String chatRoomId);
    
    @Query("SELECT ucr FROM UserChatRoom ucr WHERE ucr.chatRoom.chatRoomId = :chatRoomId")
    List<UserChatRoom> findByRoomId(@Param("chatRoomId") String chatRoomId);
    
    @Query("SELECT COUNT(ucr) FROM UserChatRoom ucr WHERE ucr.chatRoom.chatRoomId = :chatRoomId")
    long countByRoomId(@Param("chatRoomId") String chatRoomId);
    
    @Query("SELECT ucr FROM UserChatRoom ucr WHERE ucr.user.userId = :userId")
    List<UserChatRoom> findByUserId(@Param("userId") String userId);
    
    void deleteByUserUserIdAndChatRoomChatRoomId(String userId, String chatRoomId);
}