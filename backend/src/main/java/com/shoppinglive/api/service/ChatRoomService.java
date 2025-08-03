package com.shoppinglive.api.service;

import com.shoppinglive.api.model.ChatMessage;
import com.shoppinglive.api.model.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 채팅방 관리 서비스
 * 
 * 메모리 기반으로 채팅방과 사용자 정보를 관리합니다.
 * 동시성을 고려하여 thread-safe한 자료구조를 사용합니다.
 */
@Service
@Slf4j
public class ChatRoomService {
    
    /**
     * 채팅방별 사용자 목록을 저장하는 맵
     * Key: 채팅방 ID, Value: 해당 채팅방에 있는 사용자 ID 집합
     */
    private final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();
    
    /**
     * 사용자 ID와 닉네임을 매핑하는 맵
     * Key: 사용자 ID, Value: 사용자 닉네임
     */
    private final Map<String, String> userNicknames = new ConcurrentHashMap<>();
    
    /**
     * 사용자를 채팅방에 추가합니다.
     * 
     * 새로운 사용자가 채팅방에 입장할 때 호출되며,
     * 채팅방이 존재하지 않으면 새로 생성합니다.
     * 
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     * @param nickname 사용자 닉네임
     */
    public void addUserToRoom(String roomId, String userId, String nickname) {
        // TODO 테스트 후, ConcurrentHashMap.newKeySet()도 고려
        roomUsers.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(userId);
        userNicknames.put(userId, nickname);
        log.info("User {} joined room {}", nickname, roomId);
    }
    
    /**
     * 사용자를 채팅방에서 제거합니다.
     * 
     * 사용자가 채팅방을 나가거나 연결이 끊어졌을 때 호출됩니다.
     * 채팅방에 사용자가 없으면 채팅방도 함께 제거합니다.
     * 
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     */
    public void removeUserFromRoom(String roomId, String userId) {
        Set<String> users = roomUsers.get(roomId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                roomUsers.remove(roomId);
            }
        }
        String nickname = userNicknames.remove(userId);
        log.info("User {} left room {}", nickname, roomId);
    }
    
    /**
     * 채팅방의 현재 사용자 수를 반환합니다.
     * 
     * @param roomId 채팅방 ID
     * @return 해당 채팅방에 있는 사용자 수
     */
    public int getRoomUserCount(String roomId) {
        return roomUsers.getOrDefault(roomId, Set.of()).size();
    }
    
    /**
     * 사용자 ID로 닉네임을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자 닉네임 (존재하지 않으면 null)
     */
    public String getUserNickname(String userId) {
        return userNicknames.get(userId);
    }
    
    /**
     * 시스템 메시지를 생성합니다.
     * 
     * 사용자 입장/퇴장 알림 등의 시스템 메시지를 생성할 때 사용됩니다.
     * 시스템 메시지는 사용자 ID가 "SYSTEM"이고 닉네임이 "System"으로 설정됩니다.
     * 
     * @param roomId 채팅방 ID
     * @param message 시스템 메시지 내용
     * @param type 메시지 타입 (JOIN, LEAVE 등)
     * @return 생성된 시스템 메시지 객체
     */
    public ChatMessage createSystemMessage(String roomId, String message, MessageType type) {
        return new ChatMessage(
            roomId,
            "SYSTEM",
            "System",
            message,
            type,
            System.currentTimeMillis()
        );
    }
}