package com.shoppinglive.api.service;

import com.shoppinglive.api.dto.MessageResponse;
import com.shoppinglive.api.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageQueryService {

    private final MessageRepository messageRepository;

    public List<MessageResponse> getRecentMessages(final Long chatRoomId) {
        return messageRepository.findRecentMessagesByChatRoomId(chatRoomId);
    }
}