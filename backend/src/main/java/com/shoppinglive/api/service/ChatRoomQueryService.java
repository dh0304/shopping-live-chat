package com.shoppinglive.api.service;

import com.shoppinglive.api.dto.ChatRoomListResponse;
import com.shoppinglive.api.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomSessionManager chatRoomSessionManager;

    @Transactional(readOnly = true)
    public List<ChatRoomListResponse> getAllChatRooms() {
        return chatRoomRepository.findAll().stream()
                .map(chatRoom -> ChatRoomListResponse.builder()
                        .id(chatRoom.getId())
                        .roomName(chatRoom.getRoomName())
                        .description(chatRoom.getDescription())
                        .userCount(chatRoomSessionManager.getCurrentUserCount(chatRoom.getId()))
                        .build())
                .toList();
    }

}
