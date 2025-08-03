package com.shoppinglive.api.controller;

import com.shoppinglive.api.config.WebSocketConfig;
import com.shoppinglive.api.model.ChatMessage;
import com.shoppinglive.api.model.MessageType;
import com.shoppinglive.api.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    
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
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(System.currentTimeMillis());
        log.info("Broadcasting message: {} from user: {} in room: {}", 
                chatMessage.getMessage(), chatMessage.getNickname(), roomId);
        
        messagingTemplate.convertAndSend(WebSocketConfig.Destinations.getRoomTopic(roomId), chatMessage);
    }
    
    /**
     * 사용자를 채팅방에 추가합니다.
     * 
     * 새로운 사용자가 채팅방에 입장할 때 호출됩니다.
     * 세션에 사용자 정보를 저장하고, 채팅방에 사용자를 추가한 후
     * 입장 메시지와 현재 사용자 수를 브로드캐스트합니다.
     *
     * @param roomId 사용자가 입장할 방
     * @param userId 사용자 식별 번호
     * @param chatMessage 사용자 정보가 포함된 메시지 객체
     * @param headerAccessor WebSocket 세션 헤더에 접근하기 위한 객체
     */
    @MessageMapping("/chat/rooms/{roomId}/users/{userId}")
    public void addUser(@DestinationVariable String roomId, @DestinationVariable String userId,
            @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        
        chatRoomService.addUserToRoom(roomId, userId, chatMessage.getNickname());
        
        ChatMessage joinMessage = chatRoomService.createSystemMessage(
                roomId,
            chatMessage.getNickname() + "님이 입장했습니다.",
            MessageType.JOIN
        );
        
        messagingTemplate.convertAndSend(WebSocketConfig.Destinations.getRoomTopic(roomId), joinMessage);
        
        int userCount = chatRoomService.getRoomUserCount(roomId);
        messagingTemplate.convertAndSend(WebSocketConfig.Destinations.getRoomCountTopic(roomId), userCount);
    }
}