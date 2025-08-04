package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     *
     * 유저가 DB에 존재하지 않으면 저장합니다.
     *
     * @param userId
     * @param nickname
     * @return
     */
    @Transactional
    public String saveUserIfNotExists(final String userId, final String nickname) {
        if(!userRepository.existsById(userId)) {
            log.info("User {} not found. Creating new user.", userId);

            userRepository.save(
                    User.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .build()
            );
        }
        return userId;
    }
}
