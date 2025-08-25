package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findByNickname(final String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("User not found with nickname: " + nickname));
    }
}
