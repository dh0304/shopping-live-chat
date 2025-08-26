package com.shoppinglive.api.repository;

import com.shoppinglive.api.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    Optional<UserChatRoom> findByUser_IdAndChatRoom_IdAndExitTimeIsNull(Long userId, Long chatRoomId);
}
