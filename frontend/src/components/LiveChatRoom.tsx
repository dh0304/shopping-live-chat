import React, { useState, useRef, useEffect } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import { ChatMessage } from '../types/ChatTypes';
import './LiveChatRoom.css';

interface LiveChatRoomProps {
  roomId: string;
  userId: string;
  nickname: string;
}

const LiveChatRoom: React.FC<LiveChatRoomProps> = ({ roomId, userId, nickname }) => {
  const [inputMessage, setInputMessage] = useState<string>('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const { messages, userCount, isConnected, sendMessage } = useWebSocket({
    roomId,
    userId,
    nickname
  });

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputMessage.trim()) {
      sendMessage(inputMessage);
      setInputMessage('');
    }
  };

  const formatTime = (timestamp: number) => {
    return new Date(timestamp).toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const renderMessage = (message: ChatMessage, index: number) => {
    const isSystemMessage = message.type !== 'CHAT';
    const isMyMessage = message.userId === userId;

    return (
      <div
        key={`${message.timestamp}-${index}`}
        className={`message ${isSystemMessage ? 'system-message' : ''} ${
          isMyMessage ? 'my-message' : 'other-message'
        }`}
      >
        {isSystemMessage ? (
          <div className="system-text">{message.message}</div>
        ) : (
          <>
            <div className="message-header">
              <span className="nickname">{message.nickname}</span>
              <span className="timestamp">{formatTime(message.timestamp)}</span>
            </div>
            <div className="message-content">{message.message}</div>
          </>
        )}
      </div>
    );
  };

  return (
    <div className="live-chat-container">
      <div className="chat-header">
        <div className="live-indicator">
          <span className="live-dot"></span>
          LIVE
        </div>
        <div className="viewer-count">
          👥 {userCount}명 시청 중
        </div>
        <div className="connection-status">
          <span className={`status-dot ${isConnected ? 'connected' : 'disconnected'}`}></span>
          {isConnected ? '연결됨' : '연결 중...'}
        </div>
      </div>

      <div className="messages-container">
        <div className="messages-list">
          {messages.map(renderMessage)}
          <div ref={messagesEndRef} />
        </div>
      </div>

      <form className="message-input-form" onSubmit={handleSendMessage}>
        <div className="input-container">
          <input
            type="text"
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            placeholder="채팅을 입력하세요..."
            className="message-input"
            disabled={!isConnected}
            maxLength={200}
          />
          <button
            type="submit"
            className="send-button"
            disabled={!isConnected || !inputMessage.trim()}
          >
            전송
          </button>
        </div>
      </form>
    </div>
  );
};

export default LiveChatRoom;