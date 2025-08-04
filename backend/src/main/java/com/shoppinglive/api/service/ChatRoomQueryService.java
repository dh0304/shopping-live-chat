package com.shoppinglive.api.service;

import com.shoppinglive.api.model.ChatMessage;
import com.shoppinglive.api.model.MessageType;
import com.shoppinglive.api.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final UserChatRoomRepository userChatRoomRepository;

    /**
     * 채팅방의 현재 사용자 수를 반환합니다.
     *
     * @param chatRoomId 채팅방 ID
     * @return 해당 채팅방에 있는 사용자 수
     */
    @Transactional(readOnly = true)
    public int getRoomUserCount(String chatRoomId) {
        return (int) userChatRoomRepository.countByRoomId(chatRoomId);
    }

    /**
     * 시스템 메시지를 생성합니다.
     *
     * 사용자 입장/퇴장 알림 등의 시스템 메시지를 생성할 때 사용됩니다.
     * 시스템 메시지는 사용자 ID가 "SYSTEM"이고 닉네임이 "System"으로 설정됩니다.
     *
     * @param chatRoomId 채팅방 ID
     * @param message 시스템 메시지 내용
     * @param type 메시지 타입 (JOIN, LEAVE 등)
     * @return 생성된 시스템 메시지 객체
     */
    public ChatMessage createSystemMessage(String chatRoomId, String message, MessageType type) {
        return new ChatMessage(
                chatRoomId,
                "SYSTEM",
                "System",
                message,
                type,
                System.currentTimeMillis()
        );
    }
}
