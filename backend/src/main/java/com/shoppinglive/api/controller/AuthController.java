package com.shoppinglive.api.controller;

import com.shoppinglive.api.dto.LoginRequest;
import com.shoppinglive.api.dto.LoginResponse;
import com.shoppinglive.api.entity.User;
import com.shoppinglive.api.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserQueryService userQueryService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                             HttpServletRequest request) {
        final User user = userQueryService.findByNickname(loginRequest.getNickname());

        final HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("nickname", user.getNickname());
        session.setMaxInactiveInterval(30 * 60); // 30분

        log.info("로그인 성공 - userId: {}, nickname: {}", user.getId(), user.getNickname());

        final LoginResponse loginResponse = new LoginResponse(user.getId(), user.getNickname());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        log.info("로그아웃 성공");

        return ResponseEntity.ok().build();
    }
}