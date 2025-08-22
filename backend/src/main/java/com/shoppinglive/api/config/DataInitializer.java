package com.shoppinglive.api.config;

import com.shoppinglive.api.entity.ChatRoom;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.repository.ChatRoomRepository;
import com.shoppinglive.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        log.info("애플리케이션 시작 - 초기 사용자 데이터 10개 생성");
        
        List<User> users = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> User.builder()
                        .nickname("user" + i)
                        .build())
                .toList();
        
        userRepository.saveAll(users);

        List<ChatRoom> chatRooms = IntStream.rangeClosed(1, 2)
                .mapToObj(i -> ChatRoom.builder()
                        .roomName("room" + i)
                        .build())
                .toList();

        chatRoomRepository.saveAll(chatRooms);
    }
}