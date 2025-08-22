//package com.shoppinglive.api.service;
//
//import com.shoppinglive.api.entity.User;
//import com.shoppinglive.api.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    /**
//     *
//     * 유저가 DB에 존재하지 않으면 저장합니다.
//     *
//     * @param userId
//     * @param nickname
//     * @return
//     */
//    @Transactional
//    public Long saveUserIfNotExists(final Long userId, final String nickname) {
//        if(userId != null && !userRepository.existsById(userId)) {
//            log.info("User {} not found. Creating new user.", userId);
//
//            userRepository.save(
//                    User.builder()
//                    .nickname(nickname)
//                    .build()
//            );
//        } else if (userId == null) {
//            User newUser = userRepository.save(
//                    User.builder()
//                    .nickname(nickname)
//                    .build()
//            );
//            return newUser.getUserId();
//        }
//        return userId;
//    }
//}
