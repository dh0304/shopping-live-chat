package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.ChatRoom;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.entity.UserChatRoom;
import com.shoppinglive.api.repository.ChatRoomRepository;
import com.shoppinglive.api.repository.UserChatRoomRepository;
import com.shoppinglive.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;
    
    private final Map<String, Object> enterLocks = new ConcurrentHashMap<>();

    /**
     * 사용자를 채팅방에 추가합니다.
     *
     * @param chatRoomId 채팅방 ID
     * @param userId     사용자 ID
     */
    public void enterChatRoom(final Long chatRoomId, final Long userId) {
        final String lockKey = userId + ":" + chatRoomId;
        final Object lock = enterLocks.computeIfAbsent(lockKey, k -> new Object());
        
        synchronized (lock) {
            transactionTemplate.execute(status -> {
                final Optional<UserChatRoom> activeUserChatRoom = userChatRoomRepository
                        .findByUser_IdAndChatRoom_IdAndExitTimeIsNull(userId, chatRoomId);
                        
                if (activeUserChatRoom.isPresent()) {
                    log.info("이미 입장한 유저 - userId: {}, chatRoomId: {}", userId, chatRoomId);
                    return null;
                }
                
                userChatRoomRepository.save(
                        UserChatRoom.builder()
                                .user(findUser(userId))
                                .chatRoom(findChatRoom(chatRoomId))
                                .build()
                );

                log.info("유저 입장 - userId: {}, chatRoomId: {}", userId, chatRoomId);
                return null;
            });
        }
    }

    private User findUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private ChatRoom findChatRoom(final Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
    }

    /**
     * 사용자를 채팅방에서 제거합니다.
     * <p>
     *
     * @param chatRoomId 채팅방 ID
     * @param userId     사용자 ID
     */
    @Transactional
    public void leaveChatRoom(final Long chatRoomId, final Long userId) {
        final UserChatRoom userChatRoom = userChatRoomRepository.findByUser_IdAndChatRoom_IdAndExitTimeIsNull(userId, chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("UserChatRoom not found with userId: " + userId + " and chatRoomId: " + chatRoomId));

        userChatRoom.exit(LocalDateTime.now());

        log.info("유저 퇴장 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }
}
