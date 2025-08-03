export interface ChatMessage {
  roomId: string;
  userId: string;
  nickname: string;
  message: string;
  type: 'CHAT' | 'JOIN' | 'LEAVE';
  timestamp: number;
}

export interface User {
  userId: string;
  nickname: string;
}

export interface ChatRoom {
  roomId: string;
  userCount: number;
}