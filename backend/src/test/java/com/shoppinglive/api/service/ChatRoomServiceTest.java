package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.ChatRoom;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.entity.UserChatRoom;
import com.shoppinglive.api.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRoomServiceTest extends IntegrationTest {

    @Autowired
    private ChatRoomService sut;

    @Test
    @DisplayName("동시에 같은 사용자가 같은 채팅방 입장 시도 시 하나만 성공한다")
    void enterChatRoomConcurrencyControl() throws InterruptedException {
        //given
        final User user = testSupport.save(
                User.builder()
                        .nickname("testUser")
                        .build()
        );

        final ChatRoom chatRoom = testSupport.save(
                ChatRoom.builder()
                        .roomName("testRoom")
                        .description("test description")
                        .build()
        );

        final int threadCount = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failureCount = new AtomicInteger(0);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom(chatRoom.getId(), user.getId());
                    successCount.incrementAndGet();
                } catch (final DataIntegrityViolationException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        final List<UserChatRoom> userChatRooms = testSupport.findAll(UserChatRoom.class);
        
        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(failureCount.get()).isEqualTo(0);
        assertThat(userChatRooms).hasSize(1); // 하나의 레코드만 생성

        final UserChatRoom userChatRoom = userChatRooms.getFirst();
        assertThat(userChatRoom.getUser().getId()).isEqualTo(user.getId());
        assertThat(userChatRoom.getChatRoom().getId()).isEqualTo(chatRoom.getId());
        assertThat(userChatRoom.getExitTime()).isNull(); // 활성 상태
    }

    @Test
    @DisplayName("퇴장 후 재입장하면 새로운 UserChatRoom 데이터가 생성된다")
    void enterChatRoomAfterExit() {
        //given
        final User user = testSupport.save(
                User.builder()
                        .nickname("testUser")
                        .build()
        );

        final ChatRoom chatRoom = testSupport.save(
                ChatRoom.builder()
                        .roomName("testRoom")
                        .description("test description")
                        .build()
        );

        sut.enterChatRoom(chatRoom.getId(), user.getId());
        sut.leaveChatRoom(chatRoom.getId(), user.getId());

        //when
        sut.enterChatRoom(chatRoom.getId(), user.getId()); // 퇴장 후 재입장

        //then
        final List<UserChatRoom> userChatRooms = testSupport.findAll(UserChatRoom.class);
        assertThat(userChatRooms).hasSize(2);
        
        final long activeCount = userChatRooms.stream()
                .filter(ucr -> ucr.getExitTime() == null)
                .count();
        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("다른 사용자들의 동시 입장은 모두 성공한다")
    void enterChatRoomDifferentUsers() throws InterruptedException {
        //given
        final ChatRoom chatRoom = testSupport.save(
                ChatRoom.builder()
                        .roomName("testRoom")
                        .description("test description")
                        .build()
        );

        final int userCount = 5;
        final User[] users = new User[userCount];
        
        // 사용자들을 미리 생성
        for (int i = 0; i < userCount; i++) {
            users[i] = testSupport.save(
                    User.builder()
                            .nickname("testUser" + i)
                            .build()
            );
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        final CountDownLatch latch = new CountDownLatch(userCount);

        //when
        for (int i = 0; i < userCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom(chatRoom.getId(), users[index].getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        final List<UserChatRoom> userChatRooms = testSupport.findAll(UserChatRoom.class);
        assertThat(userChatRooms).hasSize(userCount); // 모든 사용자 입장 성공
        assertThat(userChatRooms).allSatisfy(ucr -> 
                assertThat(ucr.getExitTime()).isNull()
        );
    }
}