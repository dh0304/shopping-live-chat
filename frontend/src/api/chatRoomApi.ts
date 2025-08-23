import { ChatRoomListResponse } from '../types/ChatTypes';
import { API_BASE_URL } from '../config/api';

export const chatRoomApi = {
  async getChatRooms(): Promise<ChatRoomListResponse[]> {
    const response = await fetch(`${API_BASE_URL}/chat-rooms`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(errorMessage);
    }

    return response.json();
  },
};