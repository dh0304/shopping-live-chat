package com.shoppinglive.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 * 
 * STOMP 프로토콜을 사용하여 실시간 채팅 기능을 위한 WebSocket 통신을 설정합니다.
 * 클라이언트는 WebSocket을 통해 실시간으로 메시지를 주고받을 수 있습니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커를 설정합니다.
     * 
     * 클라이언트 간의 메시지 라우팅을 담당하는 브로커를 구성합니다.
     * - "/topic" 접두사를 가진 목적지로 브로드캐스트 메시지를 전송할 수 있습니다.
     * - "/app" 접두사를 가진 목적지로 애플리케이션에 메시지를 전송할 수 있습니다.
     * 
     * @param config 메시지 브로커 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(Destinations.TOPIC_PREFIX);
        config.setApplicationDestinationPrefixes(Destinations.APP_PREFIX);
    }

    /**
     * STOMP 엔드포인트를 등록합니다.
     * 
     * 클라이언트가 WebSocket 서버에 연결할 수 있는 엔드포인트를 정의합니다.
     * SockJS fallback을 활성화하여 WebSocket을 지원하지 않는 브라우저에서도 연결할 수 있습니다.
     * 모든 origin에서의 접근을 허용하여 CORS 문제를 방지합니다.
     * 
     * @param registry STOMP 엔드포인트 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(Destinations.WS_ENDPOINT)
                .setAllowedOriginPatterns("*") //TODO 이후 Phase에서 Same Origin에서만 가능하도록 수정할 것
                .withSockJS();
    }

    /**
     * WebSocket 관련 경로들을 관리하는 상수 클래스
     */
    public static class Destinations {
        // Broker prefixes
        public static final String TOPIC_PREFIX = "/topic";
        public static final String APP_PREFIX = "/app";
        
        // WebSocket endpoint
        public static final String WS_ENDPOINT = "/ws";

        // Topic destinations
        public static final String ROOM_TOPIC = "/topic/room/{roomId}";
        public static final String ROOM_COUNT_TOPIC = "/topic/room/{roomId}/count";
        
        public static String getRoomTopic(String roomId) {
            return ROOM_TOPIC.replace("{roomId}", roomId);
        }
        
        public static String getRoomCountTopic(String roomId) {
            return ROOM_COUNT_TOPIC.replace("{roomId}", roomId);
        }
    }
}