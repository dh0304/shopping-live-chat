# 쇼핑 라이브 채팅 프로젝트

[우아한형제들 기술블로그: 배민쇼핑라이브를 만드는 기술: 채팅 편](https://techblog.woowahan.com/5268/)을 참고하여 실시간 채팅 시스템을 단계적으로 구현하는 프로젝트입니다.

## 기술 스택

- **Backend**: Spring Boot 3, Java 21, Spring WebSocket, STOMP Protocol

## 단계별 아키텍처 및 구현

### [Phase 0](https://github.com/dh0304/shopping-live-chat/tree/feat/phase0)

#### 기능 구현
- [X] 단일 서버/메모리 기반 실시간 채팅
- [X] STOMP을 통한 기본 브로드캐스트
- [X] 메모리만 사용 (DB X)
- [X] React로 방 입장/채팅 송수신

## 주요 기능

### 핵심 기능
- 라이브 방 생성/시작/종료
- 실시간 텍스트 채팅
- 좋아요/시청자 수 카운트
- 채팅 로그 저장 및 조회


