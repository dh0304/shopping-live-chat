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

/**
 * 채팅방 관리 서비스
 * <p>
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
     * @param chatRoomId 채팅방 ID
     * @param userId     사용자 ID
     */
    //TODO 동시성 문제
    @Transactional
    public void enterChatRoom(final Long chatRoomId, final Long userId) {
        final ChatRoom chatRoom = findChatRoom(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            userChatRoomRepository.save(
                    UserChatRoom.builder()
                            .user(findUser(userId))
                            .chatRoom(chatRoom)
                            .build()
            );

            log.info("유저 입장 - userId: {}, chatRoomId: {}", userId, chatRoomId);
        }
    }

    private boolean isUserInChatRoom(final Long chatRoomId, final Long userId) {
        return userChatRoomRepository.findByUser_IdAndChatRoom_Id(userId, chatRoomId).isPresent();
    }

    private User findUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private ChatRoom findChatRoom(final Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
    }

//    //TODO 동시성 문제
//    @Transactional
//    public Long makeChatRoomIfNotExists(final Long chatRoomId) {
//        if(chatRoomId != null && !chatRoomRepository.existsById(chatRoomId)) {
//            log.info("ChatRoom {} not found. Creating new ChatRoom.", chatRoomId);
//
//            chatRoomRepository.save(
//                    ChatRoom.builder()
//                    .roomName("Room " + chatRoomId)
//                    .build()
//            );
//        } else if (chatRoomId == null) {
//            ChatRoom newRoom = chatRoomRepository.save(
//                    ChatRoom.builder()
//                    .roomName("New Room")
//                    .build()
//            );
//            return newRoom.getChatRoomId();
//        }
//        return chatRoomId;
//    }

    /**
     * 사용자를 채팅방에서 제거합니다.
     * <p>
     *
     * @param chatRoomId 채팅방 ID
     * @param userId     사용자 ID
     */
    @Transactional
    public void leaveChatRoom(final Long chatRoomId, final Long userId) {
        userChatRoomRepository.deleteByUser_IdAndChatRoom_Id(userId, chatRoomId);

        log.info("유저 퇴장 - userId: {}, chatRoomId: {}", userId, chatRoomId);
    }
}
