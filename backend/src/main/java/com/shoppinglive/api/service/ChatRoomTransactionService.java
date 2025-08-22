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

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomTransactionService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void enterChatRoom(final String chatRoomId, final String userId, final String nickname) {
        final ChatRoom chatRoom = makeChatRoomIfNotExists(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            saveUserChatRoom(chatRoomId, userId);
            chatRoom.incrementUserCount();

            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    private ChatRoom makeChatRoomIfNotExists(final String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseGet(() -> {
                    log.info("ChatRoom {} not found. Creating new ChatRoom.", chatRoomId);

                    return chatRoomRepository.save(
                            ChatRoom.builder()
                                    .chatRoomId(chatRoomId)
                                    .build());
                });
    }

    private boolean isUserInChatRoom(final String chatRoomId, final String userId) {
        return userChatRoomRepository.findByUserIdAndRoomId(userId, chatRoomId).isPresent();
    }

    private void saveUserChatRoom(final String chatRoomId, final String userId) {
        userChatRoomRepository.save(
                UserChatRoom.builder()
                        .user(findUser(userId))
                        .chatRoom(findChatRoom(chatRoomId))
                        .build()
        );
    }

    private User findUser(final String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private ChatRoom findChatRoom(final String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
    }
}
