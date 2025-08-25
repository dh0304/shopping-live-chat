package com.shoppinglive.api.controller;

import com.shoppinglive.api.config.WebSocketConfig;
import com.shoppinglive.api.dto.ChatMessage;
import com.shoppinglive.api.model.MessageType;
import com.shoppinglive.api.service.ChatRoomService;
import com.shoppinglive.api.service.ChatRoomSessionManager;
import com.shoppinglive.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채팅 메시지 컨트롤러
 * 
 * STOMP를 통한 실시간 채팅 메시지 처리를 담당합니다.
 * 클라이언트로부터 메시지를 받아 해당 채팅방의 모든 사용자에게 브로드캐스트합니다.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatRoomSessionManager sessionManager;
    private final MessageService messageService;

    /**
     * 채팅 메시지를 전송합니다.
     * 
     * 클라이언트로부터 받은 채팅 메시지에 타임스탬프를 추가하고
     * 해당 채팅방의 모든 구독자에게 메시지를 브로드캐스트합니다.
     *
     * @param roomId 사용자가 입장할 방
     * @param chatMessage 전송할 채팅 메시지 객체
     */
    @MessageMapping("/chat/rooms/{roomId}/messages")
    public void sendMessage(@DestinationVariable Long roomId, @Payload ChatMessage chatMessage, 
                           SimpMessageHeaderAccessor headerAccessor) {
        log.info("sendMessage - roomdId: {}, userId: {}, message: {}", roomId, chatMessage.getNickname(), chatMessage.getMessage());
        final Long sessionUserId = (Long) headerAccessor.getSessionAttributes().get("userId");
        
        if (sessionUserId == null) {
            log.warn("메시지 전송 실패 - 세션에 유저 정보가 없음");
            return;
        }
        
        messageService.saveMessage(roomId, sessionUserId, chatMessage.getMessage());
        
        chatMessage.setTimestamp(System.currentTimeMillis());
        messagingTemplate.convertAndSend(WebSocketConfig.Destinations.getRoomTopic(roomId), chatMessage);
    }
    
    /**
     * 사용자를 채팅방에 추가합니다.
     * 
     * 새로운 사용자가 채팅방에 입장할 때 호출됩니다.
     * 입장 메시지와 현재 사용자 수를 브로드캐스트합니다.
     *
     * @param roomId 사용자가 입장할 방
     * @param chatMessage 사용자 정보가 포함된 메시지 객체
     * @param headerAccessor WebSocket 세션 헤더에 접근하기 위한 객체
     */
    @MessageMapping("/chat/rooms/{roomId}/users")
    public void addUser(@DestinationVariable Long roomId,
            @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        final String sessionId = headerAccessor.getSessionId();
        final Long sessionUserId = (Long) headerAccessor.getSessionAttributes().get("userId");

        sessionManager.addSession(roomId, sessionId);
        headerAccessor.getSessionAttributes().put("roomId", roomId);

        if (sessionUserId == null) {
            handleGuestUser(roomId, chatMessage.getNickname());
            return;
        }

        handleMemberUser(roomId, sessionUserId, chatMessage.getNickname());
    }

    private void handleGuestUser(final Long roomId, final String nickname) {
        final int userCount = sessionManager.getCurrentUserCount(roomId);
        messagingTemplate.convertAndSend(WebSocketConfig.Destinations.getRoomCountTopic(roomId), userCount);

        log.info("비회원 입장 - roomId: {}, nickname: {}", roomId, nickname);
    }
    
    private void handleMemberUser(final Long roomId, final Long userId, final String nickname) {
        chatRoomService.enterChatRoom(roomId, userId);
        log.info("회원 입장 - roomId: {}, userId: {}, nickname: {}", roomId, userId, nickname);

        final ChatMessage joinMessage = createJoinSystemMessage(roomId, nickname);
        messagingTemplate.convertAndSend(WebSocketConfig.Destinations.getRoomTopic(roomId), joinMessage);

        final int userCount = sessionManager.getCurrentUserCount(roomId);
        messagingTemplate.convertAndSend(WebSocketConfig.Destinations.getRoomCountTopic(roomId), userCount);
    }

    private ChatMessage createJoinSystemMessage(final Long chatRoomId, final String userNickname) {
        return new ChatMessage(
                "SYSTEM",
                chatRoomId,
                userNickname,
                userNickname + "님이 입장했습니다.",
                MessageType.JOIN,
                System.currentTimeMillis()
        );
    }
}