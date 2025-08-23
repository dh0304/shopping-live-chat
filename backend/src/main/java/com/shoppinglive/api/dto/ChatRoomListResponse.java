package com.shoppinglive.api.dto;

import com.shoppinglive.api.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomListResponse {
    
    private Long id;
    private String roomName;
    private String description;
    private int userCount;
    
    public static ChatRoomListResponse from(ChatRoom chatRoom) {
        return ChatRoomListResponse.builder()
                .id(chatRoom.getId())
                .roomName(chatRoom.getRoomName())
                .description(chatRoom.getDescription())
                .build();
    }
}