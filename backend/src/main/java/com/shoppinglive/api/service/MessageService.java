package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.ChatRoom;
import com.shoppinglive.api.entity.Message;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.repository.ChatRoomRepository;
import com.shoppinglive.api.repository.MessageRepository;
import com.shoppinglive.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Long saveMessage(final Long chatRoomId, final Long userId, final String content) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        final Message message = Message.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content(content)
                .build();
        
        final Message savedMessage = messageRepository.save(message);
        log.info("메시지 저장 완료 - messageId: {}, chatRoomId: {}, userId: {}", 
                savedMessage.getId(), chatRoomId, userId);

        return savedMessage.getId();
    }
}