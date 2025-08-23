package com.shoppinglive.api.service;

import com.shoppinglive.api.dto.ChatRoomListResponse;
import com.shoppinglive.api.repository.ChatRoomRepository;
import com.shoppinglive.api.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final UserChatRoomRepository userChatRoomRepository;
    private final ChatRoomRepository chatRoomRepository;

    public int getRoomUserCount(Long chatRoomId) {
        return userChatRoomRepository.countByChatRoom_Id(chatRoomId);
    }

    public List<ChatRoomListResponse> getAllChatRooms() {
        return chatRoomRepository.findAll().stream()
                .map(chatRoom -> ChatRoomListResponse.builder()
                        .id(chatRoom.getId())
                        .roomName(chatRoom.getRoomName())
                        .description(chatRoom.getDescription())
                        .userCount(getRoomUserCount(chatRoom.getId()))
                        .build())
                .toList();
    }

}
