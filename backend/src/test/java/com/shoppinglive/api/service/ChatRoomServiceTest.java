package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.support.IntegerationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ChatRoomServiceTest extends IntegerationTest {

    @Autowired
    private ChatRoomService sut;

    @Test
    @DisplayName("트랜잭션만 사용")
    void enterChatRoomConcurrencyTest1() throws InterruptedException {
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("트랜잭션 + 메서드에 synchronized")
    void enterChatRoomConcurrencyTest2() throws InterruptedException {
        /*
        트랜잭션 AOP에 의해 트랜잭션이 끝나기 전에 모니터락을 반환한다. 모니터락 반환 <-> 트랜잭션 종료 사이에 다른 스레드가 접근하면 Race Condition이 발생한다.
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom2(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("트랜잭션 + 메서드에 synchronized + forUpdate(비관적락)")
    void enterChatRoomConcurrencyTest3() throws InterruptedException {
        /*
        비관적 락이 의미 없어진 케이스

        안전해 보이지만, 첫 스레드가 접근할 때 chatRoom이 존재하지 않으면 쓰기 락을 획득 할 수 없다.
        그러면 @Transcational + synchronized 문제가 그대로 발생한다.
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom3(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("트랜잭션 + forUpdate(비관적락)")
    void enterChatRoomConcurrencyTest4() throws InterruptedException {
        /*
        비관적 락이 의미 없어진 케이스

        트랜잭션 + 메서드에 synchronized + forUpdate 테스트3에서 발생하는 문제와 동일

        안전해 보이지만, 첫 스레드가 접근할 때 chatRoom이 존재하지 않으면 쓰기 락을 획득 할 수 없다.
        그러면 @Transactional + synchronized 문제가 그대로 발생한다.
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom4(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("트랜잭션, synchronized 메서드 분리")
    void enterChatRoomConcurrencyTest5() throws InterruptedException {
      /*
        메서드 분리 방식
        Self-Invocation 이슈로 트랜잭션이 적용되지 않음
        성공은 하는데 원자성을 보장할 수 없다.
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom5(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("트랜잭션, synchronized 클래스 분리")
    void enterChatRoomConcurrencyTest6() throws InterruptedException {
      /*
        메서드 분리 방식
        Self-Invocation 이슈로 트랜잭션이 적용되지 않음
        성공은 하는데 원자성을 보장할 수 없다.
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom6(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("Self-Injection 사용")
    void enterChatRoomConcurrencyTest7() throws InterruptedException {
       /*
        self-injection 사용 (문제 해결 가능 케이스)
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom7(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("프로그래매틱 트랜잭션 방식")
    void enterChatRoomConcurrencyTest8() throws InterruptedException {
       /*
        프로그래매틱(programmatic) 트랜잭션 (문제 해결 가능 케이스)
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom8(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("chatRoom의 roomId의 유니크 제약조건으로 인한 예외 발생시 재시도 로직")
    void enterChatRoomConcurrencyTest9() throws InterruptedException {
        /*
        이 테스트는 스레드 10개만 실행해도 끝나지 않는다.
        chatRoomId의 PRIMARY KEY 제약조건 위반, 무한 재시도 발생해서 스레드가 계속 재시도하다가 타임아웃
        예상대로 라이브락 발생
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom9(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("프로그래매틱 트랜잭션 + Redisson")
    void enterChatRoomConcurrencyTest11() throws InterruptedException {
       /*
        프로그래매틱(programmatic) 트랜잭션 + Redisson (문제 해결 가능 케이스)
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom11(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }

    @Test
    @DisplayName("프로그래매틱 트랜잭션 + ConcurrentMap(방이름)")
    void enterChatRoomConcurrencyTest12() throws InterruptedException {
       /*
        프로그래매틱(programmatic) 트랜잭션 + ConcurrentMap (문제 해결 가능 케이스)
         */
        //given
        String chatRoomId = "testChatRoom";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            testSupport.save(
                    User.builder()
                            .userId("user" + i)
                            .nickname("nickname" + i)
                            .build()
            );
        }

        //when
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    sut.enterChatRoom12(chatRoomId, "user" + userId, "nickname" + userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertEquals(threadCount, successCount.get(), "모든 스레드가 성공적으로 채팅방에 입장해야 합니다");
    }
}