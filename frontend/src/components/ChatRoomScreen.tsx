import React, { useState, useRef } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { HeartAnimation } from '../types';
import { authApi } from '../api/authApi';
import { useWebSocket } from '../hooks/useWebSocket';
import HeartAnimationComponent from './HeartAnimationComponent';
import './ChatRoomScreen.css';

const ChatRoomScreen: React.FC = () => {
  const { state, dispatch } = useAppContext();
  const [newMessage, setNewMessage] = useState('');
  const [hearts, setHearts] = useState<HeartAnimation[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const chatContainerRef = useRef<HTMLDivElement>(null);

  const { messages, userCount, isConnected, sendMessage } = useWebSocket({
    roomId: state.currentRoom!.id,
    userId: state.currentUser!.id,
    nickname: state.currentUser!.nickname
  });

  React.useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !state.currentUser || state.currentUser.isGuest) return;

    sendMessage(newMessage.trim());
    setNewMessage('');
  };

  const handleHeartClick = (e: React.MouseEvent) => {
    if (!chatContainerRef.current) return;

    const rect = chatContainerRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const heart: HeartAnimation = {
      id: Date.now().toString(),
      x,
      y,
      timestamp: Date.now(),
    };

    setHearts(prev => [...prev, heart]);

    // 3초 후 하트 제거
    setTimeout(() => {
      setHearts(prev => prev.filter(h => h.id !== heart.id));
    }, 3000);
  };

  const handleBackToRooms = () => {
    dispatch({ type: 'LEAVE_ROOM' });
  };

  const handleLogout = async () => {
    try {
      await authApi.logout();
      dispatch({ type: 'LOGOUT_USER' });
    } catch (error) {
      console.error('Logout failed:', error);
      dispatch({ type: 'LOGOUT_USER' });
    }
  };

  const formatTime = (timestamp: number) => {
    return new Date(timestamp).toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="chat-room-screen">
      <header className="chat-header">
        <div className="header-content">
          <div className="header-left">
            <button onClick={handleBackToRooms} className="back-button">
              ←
            </button>
            <div className="header-info">
              <h1>{state.currentRoom?.name}</h1>
              <p>{state.currentUser?.nickname}</p>
            </div>
          </div>
          
          <div className="header-right">
            <div className="viewer-count">
              👥 {userCount}명
            </div>
            <div className="connection-status">
              <span className={`status-dot ${isConnected ? 'connected' : 'disconnected'}`}></span>
              {isConnected ? '연결됨' : '연결 중...'}
            </div>
            <span className="live-indicator"></span>
            <span className="live-text">LIVE</span>
            <button onClick={handleLogout} className="logout-button">
              로그아웃
            </button>
          </div>
        </div>
      </header>

      <div 
        ref={chatContainerRef}
        className="chat-messages"
      >
        <div className="messages-container">
          {messages.slice().reverse().map((message, index) => {
            const isSystemMessage = message.type !== 'CHAT';
            const isMyMessage = message.userId === state.currentUser!.id;
            
            return (
              <div key={`${message.timestamp}-${index}`} className={`message ${isSystemMessage ? 'system-message' : ''} ${isMyMessage ? 'my-message' : 'other-message'}`}>
                {isSystemMessage ? (
                  <div className="system-text">{message.message}</div>
                ) : (
                  <>
                    <div className="message-avatar">
                      {message.nickname.charAt(0)}
                    </div>
                    <div className="message-content">
                      <div className="message-header">
                        <span className="message-nickname">{message.nickname}</span>
                        <span className="message-timestamp">{formatTime(message.timestamp)}</span>
                      </div>
                      <div className="message-bubble">
                        <p className="message-text">{message.message}</p>
                      </div>
                    </div>
                  </>
                )}
              </div>
            );
          })}
          <div ref={messagesEndRef} />
        </div>

        {hearts.map((heart) => (
          <HeartAnimationComponent key={heart.id} heart={heart} />
        ))}
      </div>

      <div className="message-input-area">
        {state.currentUser?.isGuest ? (
          <div className="guest-notice">
            <p className="guest-notice-text">
              비회원은 채팅 참여가 제한됩니다. 화면을 터치해서 하트를 보내보세요! ❤️
            </p>
          </div>
        ) : (
          <form onSubmit={handleSendMessage} className="message-form">
            <input
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              placeholder="메시지를 입력하세요..."
              className="message-input"
              disabled={!isConnected}
              maxLength={200}
            />
            <button
              type="submit"
              disabled={!isConnected || !newMessage.trim()}
              className="send-button"
            >
              전송
            </button>
          </form>
        )}
      </div>

      <button
        onClick={handleHeartClick}
        className="heart-button"
        aria-label="하트 보내기"
      >
        ❤️
      </button>
    </div>
  );
};

export default ChatRoomScreen;