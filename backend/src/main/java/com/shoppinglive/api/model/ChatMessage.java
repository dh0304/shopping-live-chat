package com.shoppinglive.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 모델 클래스
 * 
 * 실시간 채팅에서 주고받는 메시지의 데이터 구조를 정의합니다.
 * 일반 채팅 메시지뿐만 아니라 사용자 입장/퇴장 시스템 메시지도 포함합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String roomId;
    private String userId;
    private String nickname;
    private String message;
    private MessageType type;
    private long timestamp;
}