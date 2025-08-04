package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.ChatRoom;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.entity.UserChatRoom;
import com.shoppinglive.api.model.ChatMessage;
import com.shoppinglive.api.model.MessageType;
import com.shoppinglive.api.repository.ChatRoomRepository;
import com.shoppinglive.api.repository.UserChatRoomRepository;
import com.shoppinglive.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅방 관리 서비스
 * 
 * DB 기반으로 채팅방과 사용자 정보를 관리합니다.
 * 채팅 메시지는 메모리에서 관리하고, 사용자 및 채팅방 관계는 DB에서 관리합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;
    
    /**
     * 사용자를 채팅방에 추가합니다.
     * 
     * 새로운 사용자가 채팅방에 입장할 때 호출되며,
     * 채팅방이 존재하지 않으면 새로 생성합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @param nickname 사용자 닉네임
     */
    //TODO 동시성 문제
    @Transactional
    public void enterChatRoom(final String chatRoomId, final String userId, final String nickname) {
        makeChatRoomIfNotExists(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            userChatRoomRepository.save(
                    UserChatRoom.builder()
                    .user(findUser(userId))
                    .chatRoom(findChatRoom(chatRoomId))
                    .build()
            );

            chatRoomRepository.incrementUserCount(chatRoomId);
            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    private User findUser(final String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private ChatRoom findChatRoom(final String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
    }

    //TODO 동시성 문제
    @Transactional
    public String makeChatRoomIfNotExists(final String chatRoomId) {
        if(!chatRoomRepository.existsById(chatRoomId)) {
            log.info("ChatRoom {} not found. Creating new ChatRoom.", chatRoomId);

            chatRoomRepository.save(
                    ChatRoom.builder()
                    .chatRoomId(chatRoomId)
                    .roomName("Room " + chatRoomId)
                    .build()
            );
        }
        return chatRoomId;
    }

    /**
     * 사용자를 채팅방에서 제거합니다.
     * 
     * 사용자가 채팅방을 나가거나 연결이 끊어졌을 때 호출됩니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void leaveChatRoom(final String chatRoomId, final String userId) {
        if (isUserInChatRoom(chatRoomId, userId)) {
            removeUser(chatRoomId, userId);
            
            log.info("User {} left room {}", userId, chatRoomId);
        }
    }

    private boolean isUserInChatRoom(final String chatRoomId, final String userId) {
        return userChatRoomRepository.findByUserIdAndRoomId(userId, chatRoomId).isPresent();
    }

    private void removeUser(final String chatRoomId, final String userId) {
        userChatRoomRepository.deleteByUserUserIdAndChatRoomChatRoomId(userId, chatRoomId);
        chatRoomRepository.decrementUserCount(chatRoomId);
    }
}
