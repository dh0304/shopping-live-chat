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
public class UserQueryService {

    private final UserRepository userRepository;

    /**
     * 사용자 ID로 닉네임을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 닉네임 (존재하지 않으면 null)
     */
    @Transactional(readOnly = true)
    public String getUserNickname(Long userId) {
        return userRepository.findById(userId)
                .map(User::getNickname)
                .orElse(null);
    }
}
