# Phase 0: 간단한 채팅 구현

## 요약

실시간 채팅 시스템의 MVP를 구현했습니다. WebSocket(STOMP)을 활용하여 다중 사용자 실시간 채팅 기능을 제공합니다.

## 주요 기능

### Backend
- **WebSocket Configuration**: STOMP 엔드포인트 설정
- **ChatController**: 메시지 처리 및 브로드캐스팅
- **ChatRoomService**: 채팅방 및 사용자 관리
- **Event Listener**: 연결 해제 시 자동 정리

## 기술 스택

**Backend**
- Spring Boot
- Spring WebSocket
- STOMP Protocol

## 파일 구조

```
backend/src/main/java/com/shoppinglive/api/
├── controller/
│   └── ChatController.java   # 채팅 메시지 컨트롤러
├── service/
│   └── ChatRoomService.java  # 채팅방 서비스
├── config/
│   ├── WebSocketConfig.java  # WebSocket 설정
│   └── WebSocketEventListener.java  # 이벤트 리스너
└── model/
    ├── ChatMessage.java      # 채팅 메시지 모델
    └── MessageType.java      # 메시지 타입 ENUM
```

## 테스트 방법

1. **Backend 실행**: `./gradlew bootRun`
2. **Frontend 실행**: `npm start`
3. **다중 사용자 테스트**: 여러 브라우저 창에서 동일한 방 ID로 접속

## WebSocket 통신 플로우

1. **연결**: SockJS + STOMP로 WebSocket 연결
2. **입장**: `/app/chat/rooms/{roomId}/users/{userId}` 엔드포인트로 입장 메시지 전송
3. **구독**: 
   - `/topic/room/{roomId}` - 채팅 메시지 수신
   - `/topic/room/{roomId}/count` - 사용자 수 업데이트
4. **메시지 전송**: `/app/chat/rooms/{roomId}/messages` 엔드포인트 사용
5. **퇴장**: 연결 해제 시 자동으로 퇴장 메시지 브로드캐스팅

## Phase 0 완료 사항

- [X] 단일 서버/메모리 기반 실시간 채팅
- [X] STOMP을 통한 기본 브로드캐스트
- [X] 메모리만 사용 (DB X)
- [X] React로 방 입장/채팅 송수신

## 기능 동작 시연
[![채팅방 입장]([https://imgur.com/a/hr7UmPS](https://imgur.com/gallery/hr7UmPS#cYtXUtH))](https://imgur.com/gallery/hr7UmPS#cYtXUtH)
