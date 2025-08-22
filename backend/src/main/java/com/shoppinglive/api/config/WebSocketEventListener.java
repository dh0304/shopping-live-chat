package com.shoppinglive.api.config;

import com.shoppinglive.api.dto.ChatMessage;
import com.shoppinglive.api.model.MessageType;
import com.shoppinglive.api.service.ChatRoomQueryService;
import com.shoppinglive.api.service.ChatRoomService;
import com.shoppinglive.api.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket 이벤트 리스너
 * <p>
 * WebSocket 연결과 관련된 이벤트를 처리합니다.
 * 주로 사용자의 연결 해제 시 채팅방에서 사용자를 제거하고 퇴장 메시지를 브로드캐스트합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final UserQueryService userQueryService;

    /**
     * WebSocket 연결 해제 이벤트를 처리합니다.
     * <p>
     * 사용자가 브라우저를 닫거나 새로고침할 때 자동으로 호출되어
     * 해당 사용자를 채팅방에서 제거하고 다른 사용자들에게 퇴장 알림을 전송합니다.
     *
     * @param event WebSocket 연결 해제 이벤트 객체
     */
    @EventListener
    //TODO 비동기처리가 필요할 수 있음 (예: @Async)
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");

        if (userId != null && roomId != null) {
            String nickname = userQueryService.getUserNickname(userId);
            chatRoomService.leaveChatRoom(roomId, userId);

            messagingTemplate.convertAndSend(
                    WebSocketConfig.Destinations.getRoomTopic(roomId),
                    createLeaveSystemMessage(roomId, nickname)
            );
            messagingTemplate.convertAndSend(
                    WebSocketConfig.Destinations.getRoomCountTopic(roomId),
                    chatRoomQueryService.getRoomUserCount(roomId)
            );
        }
    }

    private ChatMessage createLeaveSystemMessage(Long chatRoomId, String userNickname) {
        return new ChatMessage(
                "SYSTEM",
                chatRoomId,
                userNickname,
                userNickname + "님이 퇴장했습니다.",
                MessageType.LEAVE,
                System.currentTimeMillis()
        );
    }
}