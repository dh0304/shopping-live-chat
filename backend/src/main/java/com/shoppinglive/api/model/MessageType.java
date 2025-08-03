package com.shoppinglive.api.model;

public enum MessageType {

    /**
     * 메시지 타입을 구분
     *
     * CHAT: 일반 채팅 메시지
     * JOIN: 사용자 입장 알림 메시지
     * LEAVE: 사용자 퇴장 알림 메시지
     */
    CHAT, JOIN, LEAVE
}
