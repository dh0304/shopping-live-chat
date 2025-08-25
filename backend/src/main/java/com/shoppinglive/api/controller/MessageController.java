package com.shoppinglive.api.controller;

import com.shoppinglive.api.dto.MessageResponse;
import com.shoppinglive.api.service.MessageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageQueryService messageQueryService;
    
    @GetMapping("/chat-rooms/{chatRoomId}/recent")
    public ResponseEntity<List<MessageResponse>> getRecentMessages(@PathVariable final Long chatRoomId) {
        final List<MessageResponse> messages = messageQueryService.getRecentMessages(chatRoomId);
        return ResponseEntity.ok(messages);
    }
}