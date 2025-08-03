/**
 * WebSocket 관련 경로들을 관리하는 상수들
 * 백엔드의 WebSocketConfig.Destinations와 일치하도록 구성
 */

export const WS_CONFIG = {
  SERVER_URL: 'http://localhost:8080',
  
  ENDPOINTS: {
    WS: '/ws',
  },
  
  PREFIXES: {
    TOPIC: '/topic',
    APP: '/app',
  },
  
  TOPICS: {
    ROOM: (roomId: string) => `/topic/room/${roomId}`,
    ROOM_COUNT: (roomId: string) => `/topic/room/${roomId}/count`,
  },
  
  DESTINATIONS: {
    JOIN: (roomId: string, userId: string) => `/app/chat/rooms/${roomId}/users/${userId}`,
    MESSAGE: (roomId: string) => `/app/chat/rooms/${roomId}/messages`,
  },
  
  UTILS: {
    getWebSocketUrl: () => WS_CONFIG.SERVER_URL + WS_CONFIG.ENDPOINTS.WS,
  }
} as const;