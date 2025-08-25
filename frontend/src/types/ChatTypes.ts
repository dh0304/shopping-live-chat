export interface ChatMessage {
  roomId: number;
  userId: number;
  nickname: string;
  message: string;
  type: 'CHAT' | 'JOIN' | 'LEAVE';
  timestamp: number;
}

export interface User {
  userId: number;
  nickname: string;
}

export interface ChatRoom {
  roomId: number;
  userCount: number;
}

export interface ChatRoomListResponse {
  id: number;
  roomName: string;
  description: string;
  userCount: number;
}

export interface MessageResponse {
  id: number;
  nickname: string;
  content: string;
  createdAt: string;
}