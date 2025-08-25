import { MessageResponse } from '../types/ChatTypes';
import { API_BASE_URL } from '../config/api';

export const messageApi = {
  async getRecentMessages(chatRoomId: number): Promise<MessageResponse[]> {
    const response = await fetch(`${API_BASE_URL}/messages/chat-rooms/${chatRoomId}/recent`, {
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