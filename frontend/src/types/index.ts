export interface User {
  id: number;
  nickname: string;
  isGuest: boolean;
}

export interface ChatRoom {
  id: number;
  name: string;
  description?: string;
  activeUsers: number;
  isActive: boolean;
}

export interface ChatMessage {
  id: string;
  roomId: number;
  userId: number;
  nickname: string;
  message: string;
  type: 'CHAT' | 'JOIN' | 'LEAVE';
  timestamp: number;
}

export interface HeartAnimation {
  id: string;
  x: number;
  y: number;
  timestamp: number;
}

export type AuthState = 
  | { type: 'UNAUTHENTICATED' }
  | { type: 'AUTHENTICATED'; user: User }
  | { type: 'GUEST'; user: User };

export type AppState = 
  | { screen: 'LOGIN' }
  | { screen: 'ROOM_LIST'; user: User }
  | { screen: 'CHAT_ROOM'; user: User; roomId: number };