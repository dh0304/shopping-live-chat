package com.shoppinglive.api.controller;

import com.shoppinglive.api.dto.ChatRoomListResponse;
import com.shoppinglive.api.service.ChatRoomQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    
    private final ChatRoomQueryService chatRoomQueryService;
    
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponse>> getChatRooms() {
        List<ChatRoomListResponse> chatRooms = chatRoomQueryService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }
}