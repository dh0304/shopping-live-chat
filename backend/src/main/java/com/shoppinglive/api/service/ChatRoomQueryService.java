package com.shoppinglive.api.service;

import com.shoppinglive.api.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public int getRoomUserCount(Long chatRoomId) {
        return userChatRoomRepository.countByChatRoom_Id(chatRoomId);
    }

}
