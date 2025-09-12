package com.shoppinglive.api.config;

import com.shoppinglive.api.entity.ChatRoom;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.repository.ChatRoomRepository;
import com.shoppinglive.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"!docker", "!dev"}) // docker, dev 프로파일이 아닐 때만 실행
public class DataInitializer {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        log.info("애플리케이션 시작 - 초기 사용자 데이터 10000개 생성");

        List<User> users = IntStream.rangeClosed(1, 10000)
                .mapToObj(i -> User.builder()
                        .nickname("user" + i)
                        .build())
                .toList();

        userRepository.saveAll(users);

        chatRoomRepository.saveAll(createChatRooms());
    }

    private List<ChatRoom> createChatRooms() {
        List<ChatRoom> chatRooms = new ArrayList<>();

        chatRooms.add(ChatRoom.builder()
                .roomName("올가을 스타일 완성! 패션 핫템 특집")
                .description("계절마다 달라지는 트렌드, 이번 시즌 놓치면 안 될 필수 아이템을 준비했습니다. 라이브 중에만 가능한 한정 할인과 스타일링 팁도 함께 만나보세요.")
                .build());

        chatRooms.add(ChatRoom.builder()
                .roomName("오늘의 밥상, 신선 특가 식품전")
                .description("산지 직송 신선 식품부터 인기 간편식까지! 맛과 가격 모두 잡은 특별한 구성으로 준비했습니다. 방송 중 깜짝 증정 이벤트도 함께 즐겨보세요.")
                .build());

        chatRooms.add(ChatRoom.builder()
                .roomName("생활이 편해지는 스마트 가전 모음")
                .description("집안일을 더 쉽고 똑똑하게! 최신 가전제품을 라이브 단독 혜택으로 만나보세요. 실시간으로 제품 사용법과 꿀팁도 알려드립니다.")
                .build());

        return chatRooms;
    }
}