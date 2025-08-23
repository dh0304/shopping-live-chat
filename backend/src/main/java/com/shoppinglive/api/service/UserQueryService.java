package com.shoppinglive.api.service;

import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public User findByNickname(final String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("User not found with nickname: " + nickname));
    }

    public User findByUserId(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}
