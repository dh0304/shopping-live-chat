package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.ChatRoom;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.entity.UserChatRoom;
import com.shoppinglive.api.repository.ChatRoomRepository;
import com.shoppinglive.api.repository.UserChatRoomRepository;
import com.shoppinglive.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 채팅방 관리 서비스
 * 
 * DB 기반으로 채팅방과 사용자 정보를 관리합니다.
 * 채팅 메시지는 메모리에서 관리하고, 사용자 및 채팅방 관계는 DB에서 관리합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 사용자를 채팅방에 추가합니다.
     * <p>
     * 새로운 사용자가 채팅방에 입장할 때 호출되며,
     * 채팅방이 존재하지 않으면 새로 생성합니다.
     *
     * @param chatRoomId 채팅방 ID
     * @param userId     사용자 ID
     * @param nickname   사용자 닉네임
     */
    //TODO 동시성 문제
    @Transactional
    public void enterChatRoom(final String chatRoomId, final String userId, final String nickname) {
        //TODO 채팅방 별 락?
        final ChatRoom chatRoom = makeChatRoomIfNotExists(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            saveUserChatRoom(chatRoomId, userId);
            chatRoom.incrementUserCount();

            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    @Transactional
    public synchronized void enterChatRoom2(final String chatRoomId, final String userId, final String nickname) {
        /*
        트랜잭션 AOP에 의해 트랜잭션이 끝나기 전에 모니터락을 반환한다. 모니터락 반환 <-> 트랜잭션 종료 사이에 다른 스레드가 접근하면 Race Condition이 발생한다.
         */
        final ChatRoom chatRoom = makeChatRoomIfNotExists(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            saveUserChatRoom(chatRoomId, userId);
            chatRoom.incrementUserCount();

            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    @Transactional
    public synchronized void enterChatRoom3(final String chatRoomId, final String userId, final String nickname) {
        /*
        비관적 락이 의미 없어진 케이스

        안전해 보이지만, 첫 스레드가 접근할 때 chatRoom이 존재하지 않으면 쓰기 락을 획득 할 수 없다.
        그러면 @Transcational + synchronized 문제가 그대로 발생한다.
         */

        final ChatRoom chatRoom = makeChatRoomIfNotExists3(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            saveUserChatRoom(chatRoomId, userId);
            chatRoom.incrementUserCount();

            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    @Transactional
    public void enterChatRoom4(final String chatRoomId, final String userId, final String nickname) {
        /*
        비관적 락이 의미 없어진 케이스

        트랜잭션 + 메서드에 synchronized + forUpdate 테스트3에서 발생하는 문제와 동일

        안전해 보이지만, 첫 스레드가 접근할 때 chatRoom이 존재하지 않으면 쓰기 락을 획득 할 수 없다.
        그러면 @Transactional + synchronized 문제가 그대로 발생한다.
         */
        final ChatRoom chatRoom = makeChatRoomIfNotExists3(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            saveUserChatRoom(chatRoomId, userId);
            chatRoom.incrementUserCount();

            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    public synchronized void enterChatRoom5(final String chatRoomId, final String userId, final String nickName) {
        /*
        메서드 분리 방식
        Self-Invocation 이슈로 트랜잭션이 적용되지 않음
         */
        enterChatRoomInternal(chatRoomId, userId, nickName);
    }

    @Transactional
    protected void enterChatRoomInternal(final String chatRoomId, final String userId, final String nickname) {
        /*
        메서드 분리 방식
        Self-Invocation 이슈로 트랜잭션이 적용되지 않음
         */
        final ChatRoom chatRoom = makeChatRoomIfNotExists(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            saveUserChatRoom(chatRoomId, userId);
            chatRoom.incrementUserCount();

            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    private final ChatRoomTransactionService chatRoomTransactionService;

    public synchronized void enterChatRoom6(final String chatRoomId, final String userId, final String nickname) {
        /*
        TransactionalService로 분리 (문제 해결 가능 케이스)
         */
        chatRoomTransactionService.enterChatRoom(chatRoomId, userId, nickname);
    }

    @Autowired
    @Lazy // @Lazy를 사용하지 않으면 Spring Container 시작 시 순환 참조가 발생, 실제 사용 시점에 실제 빈 주입으로 문제 해결
    private ChatRoomService self;

    public synchronized void enterChatRoom7(final String chatRoomId, final String userId, final String nickname) {
        /*
        self-injection 사용 (문제 해결 가능 케이스)
         */
        self.enterChatRoom(chatRoomId, userId, nickname);
    }




    private void saveUserChatRoom(final String chatRoomId, final String userId) {
        userChatRoomRepository.save(
                UserChatRoom.builder()
                        .user(findUser(userId))
                        .chatRoom(findChatRoom(chatRoomId))
                        .build()
        );
    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    public synchronized void enterChatRoom8(final String chatRoomId, final String userId, final String nickname) {
        /*
        프로그래매틱(programmatic) 트랜잭션 (문제 해결 가능 케이스)
         */
        transactionTemplate.execute((status) -> {
                    final ChatRoom chatRoom = makeChatRoomIfNotExists(chatRoomId);

                    if (!isUserInChatRoom(chatRoomId, userId)) {
                        saveUserChatRoom(chatRoomId, userId);
                        chatRoom.incrementUserCount();

                        log.info("User {} joined room {}", nickname, chatRoomId);
                    }

                    return null; // transactionTemplate.executeWithoutResult 사용하면 return 불필요
                });
    }

    @Transactional
    public void enterChatRoom9(final String chatRoomId, final String userId, final String nickname) {
        final ChatRoom chatRoom = makeChatRoomIfNotExists9_1(chatRoomId);

        if (!isUserInChatRoom(chatRoomId, userId)) {
            saveUserChatRoom(chatRoomId, userId);
            chatRoom.incrementUserCount();

            log.info("User {} joined room {}", nickname, chatRoomId);
        }
    }

    private ChatRoom makeChatRoomIfNotExists9(final String chatRoomId) {
        /*
        재시도 횟수가 부족하면 데드락은 아니지만 라이브락(livelock) 상황이 발생할 수 있다.
         */
        return chatRoomRepository.findById(chatRoomId)
                .orElseGet(() -> {
                    log.info("ChatRoom {} not found. Creating new ChatRoom.", chatRoomId);
                    
                    int maxRetries = 3;
                    for (int attempt = 0; attempt < maxRetries; attempt++) {
                        try {
                            return chatRoomRepository.save(
                                    ChatRoom.builder()
                                            .chatRoomId(chatRoomId)
                                            .build());
                        } catch (Exception e) {
                            if (attempt == maxRetries - 1) {
                                // 마지막 시도에서도 실패하면 다시 조회 시도
                                return chatRoomRepository.findById(chatRoomId)
                                        .orElseThrow(() -> new RuntimeException("Failed to create or find ChatRoom after retries", e));
                            }

                            log.warn("Failed to create ChatRoom {}, attempt {}/{}: {}", chatRoomId, attempt + 1, maxRetries, e.getMessage());
                            
                            // Exponential backoff with jitter
                            long baseDelay = 50; // 50ms
                            long delay = baseDelay * (1L << attempt) + (long) (Math.random() * 50);
                            
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("Thread interrupted during retry", ie);
                            }
                        }
                    }
                    throw new RuntimeException("Should not reach here");
                });
    }

    private ChatRoom makeChatRoomIfNotExists9_1(final String chatRoomId) {
        /*
        재시도 횟수 제한 없음 - 무한 재시도 (위험!)
         */
        return chatRoomRepository.findById(chatRoomId)
                .orElseGet(() -> {
                    log.info("ChatRoom {} not found. Creating new ChatRoom.", chatRoomId);
                    
                    int attempt = 0;
                    while (true) {
                        try {
                            return chatRoomRepository.save(
                                    ChatRoom.builder()
                                            .chatRoomId(chatRoomId)
                                            .build());
                        } catch (Exception e) {
                            log.warn("Failed to create ChatRoom {}, attempt {}: {}", chatRoomId, attempt + 1, e.getMessage());
                            
                            // Exponential backoff with jitter
                            long baseDelay = 50; // 50ms
                            long delay = baseDelay * (1L << Math.min(attempt, 10)) + (long) (Math.random() * 50);
                            
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("Thread interrupted during retry", ie);
                            }
                            
                            attempt++;
                        }
                    }
                });
    }

//    @Transactional
//    public void enterChatRoom10(final String chatRoomId, final String userId, final String nickname) {
//        /*
//        낙관적 락 방식 - CREATE 상황에서는 부자연스러운 방식
//        ChatRoom이 없을 때는 낙관적 락이 의미가 없으므로,
//        ChatRoom 생성 후 userCount 증가 시에만 낙관적 락이 적용됨
//         */
//        final ChatRoom chatRoom = makeChatRoomIfNotExists10(chatRoomId);
//
//        if (!isUserInChatRoom(chatRoomId, userId)) {
//            saveUserChatRoom(chatRoomId, userId);
//            chatRoom.incrementUserCount();
//
//            log.info("User {} joined room {}", nickname, chatRoomId);
//        }
//    }

    private final RedissonClient redissonClient;

    public void enterChatRoom11(final String chatRoomId, final String userId, final String nickname) {
        String lockKey = "chatroom:enter:" + chatRoomId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.tryLock(10, 3, TimeUnit.SECONDS)) {
                try {
                    transactionTemplate.execute(status -> {
                        final ChatRoom chatRoom = makeChatRoomIfNotExists(chatRoomId);

                        if (!isUserInChatRoom(chatRoomId, userId)) {
                            saveUserChatRoom(chatRoomId, userId);
                            chatRoom.incrementUserCount();

                            log.info("User {} joined room {}", nickname, chatRoomId);
                        }
                        return null;
                    });
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("채팅방 입장 처리 중 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("채팅방 입장 처리가 중단되었습니다.", e);
        }
    }

    private final ConcurrentHashMap<String, Object> chatRoomLocks = new ConcurrentHashMap<>();

    //TODO 프로그래머틱 트랜잭션 + ConcurrentMap으로 락 분리
    public void enterChatRoom12(final String chatRoomId, final String userId, final String nickname) {
        Object lock = chatRoomLocks.computeIfAbsent(chatRoomId, k -> new Object());
        
        synchronized (lock) {
            transactionTemplate.execute(status -> {
                final ChatRoom chatRoom = makeChatRoomIfNotExists(chatRoomId);

                if (!isUserInChatRoom(chatRoomId, userId)) {
                    saveUserChatRoom(chatRoomId, userId);
                    chatRoom.incrementUserCount();

                    log.info("User {} joined room {}", nickname, chatRoomId);
                }
                return null;
            });
        }
    }

    //TODO DB 락없이 transactional + synchronized 분리로 했을 경우 vs transactional + 낙관적락 또는 비관적락을 사용했을 경우 (수정, 삭제만 가능) -> 비즈니스 로직상 방 존재 여부에서 race condition이 발생하므로 테스트 불가능

    //TODO 단일 프로세스에서 레디스 락을 사용했을 경우 vs transactional + synchronized를 사용했을 경우

    private User findUser(final String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private ChatRoom findChatRoom(final String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
    }

    private ChatRoom makeChatRoomIfNotExists(final String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseGet(() -> {
                    log.info("ChatRoom {} not found. Creating new ChatRoom.", chatRoomId);

                    return chatRoomRepository.save(
                            ChatRoom.builder()
                                    .chatRoomId(chatRoomId)
                                    .build());
                });
    }

    private ChatRoom makeChatRoomIfNotExists3(final String chatRoomId) {
        return chatRoomRepository.findByIdWithPessimisticLock(chatRoomId)
                .orElseGet(() -> {
                    log.info("ChatRoom {} not found. Creating new ChatRoom.", chatRoomId);

                    return chatRoomRepository.save(
                            ChatRoom.builder()
                                    .chatRoomId(chatRoomId)
                                    .build());
                });
    }

    /**
     * 사용자를 채팅방에서 제거합니다.
     * 
     * 사용자가 채팅방을 나가거나 연결이 끊어졌을 때 호출됩니다.
     *
     * @param chatRoomId 채팅방 ID
     * @param userId     사용자 ID
     */
    @Transactional
    public void leaveChatRoom(final String chatRoomId, final String userId) {
        if (isUserInChatRoom(chatRoomId, userId)) {
            removeUser(findChatRoom(chatRoomId), userId);

            log.info("User {} left room {}", userId, chatRoomId);
        }
    }

    private boolean isUserInChatRoom(final String chatRoomId, final String userId) {
        return userChatRoomRepository.findByUserIdAndRoomId(userId, chatRoomId).isPresent();
    }

    private void removeUser(final ChatRoom chatRoom, final String userId) {
        userChatRoomRepository.deleteByUserUserIdAndChatRoomChatRoomId(userId, chatRoom.getChatRoomId());
        chatRoom.decrementUserCount();
    }
}
