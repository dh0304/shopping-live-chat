import { useCallback, useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { ChatMessage } from '../types';
import { MessageResponse } from '../types/ChatTypes';
import { WS_CONFIG } from '../constants/webSocketPaths';
import { messageApi } from '../api/messageApi';

interface UseWebSocketProps {
  roomId: number;
  userId: number;
  nickname: string;
}

export const useWebSocket = ({ roomId, userId, nickname }: UseWebSocketProps) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [userCount, setUserCount] = useState<number>(0);
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const clientRef = useRef<Client | null>(null);

  const loadRecentMessages = useCallback(async () => {
    try {
      const recentMessages = await messageApi.getRecentMessages(roomId);
      const convertedMessages: ChatMessage[] = recentMessages.map((msg: MessageResponse) => ({
        roomId,
        userId: 0,
        nickname: msg.nickname,
        message: msg.content,
        type: 'CHAT' as const,
        timestamp: new Date(msg.createdAt).getTime(),
        id: msg.id.toString()
      }));
      setMessages(convertedMessages);
    } catch (error) {
      console.error('Failed to load recent messages:', error);
    }
  }, [roomId]);

  const connect = useCallback(() => {
    const socket = new SockJS(WS_CONFIG.UTILS.getWebSocketUrl());
    const client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
      onConnect: async () => {
        console.log('Connected to WebSocket');
        setIsConnected(true);

        // 최근 메시지 로드
        await loadRecentMessages();

        // 채팅 메시지 구독
        client.subscribe(WS_CONFIG.TOPICS.ROOM(roomId), (message) => {
          const chatMessage: ChatMessage = JSON.parse(message.body);
          setMessages(prev => [...prev, chatMessage]);
        });

        // 사용자 수 구독
        client.subscribe(WS_CONFIG.TOPICS.ROOM_COUNT(roomId), (message) => {
          const count = parseInt(message.body);
          setUserCount(count);
        });

        // 입장 메시지 전송
        client.publish({
          destination: WS_CONFIG.DESTINATIONS.JOIN(roomId),
          body: JSON.stringify({
            roomId,
            userId,
            nickname,
            type: 'JOIN'
          })
        });
      },
      onDisconnect: () => {
        console.log('Disconnected from WebSocket');
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      }
    });

    client.activate();
    clientRef.current = client;
  }, [roomId, userId, nickname, loadRecentMessages]);

  const disconnect = useCallback(() => {
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setIsConnected(false);
    }
  }, []);

  const sendMessage = useCallback((messageText: string) => {
    if (clientRef.current && isConnected && messageText.trim()) {
      const chatMessage: Omit<ChatMessage, 'id' | 'timestamp'> = {
        roomId,
        userId,
        nickname,
        message: messageText.trim(),
        type: 'CHAT'
      };

      clientRef.current.publish({
        destination: WS_CONFIG.DESTINATIONS.MESSAGE(roomId),
        body: JSON.stringify(chatMessage)
      });
    }
  }, [roomId, userId, nickname, isConnected]);

  useEffect(() => {
    connect();
    return () => disconnect();
  }, [connect, disconnect]);

  return {
    messages,
    userCount,
    isConnected,
    sendMessage
  };
};