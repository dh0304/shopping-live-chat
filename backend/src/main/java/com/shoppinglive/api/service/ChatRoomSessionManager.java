package com.shoppinglive.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatRoomSessionManager {
    
    private final Map<Long, Set<String>> activeSessions = new ConcurrentHashMap<>();
    
    public void addSession(final Long roomId, final String sessionId) {
        activeSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        log.info("세션 추가 - roomId: {}, sessionId: {}", roomId, sessionId);
    }
    
    public void removeSession(final Long roomId, final String sessionId) {
        final Set<String> sessions = activeSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                activeSessions.remove(roomId);
            }
            log.info("세션 제거 - roomId: {}, sessionId: {}", roomId, sessionId);
        }
    }
    
    public int getCurrentUserCount(final Long roomId) {
        return activeSessions.getOrDefault(roomId, Collections.emptySet()).size();
    }
}