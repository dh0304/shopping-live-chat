import React, { useState, useEffect, useRef } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { ChatMessage, HeartAnimation } from '../types';
import { authApi } from '../api/authApi';
import HeartAnimationComponent from './HeartAnimationComponent';
import './ChatRoomScreen.css';

const ChatRoomScreen: React.FC = () => {
  const { state, dispatch } = useAppContext();
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [hearts, setHearts] = useState<HeartAnimation[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const chatContainerRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 임시 메시지 데이터 (실제로는 WebSocket으로 받을 예정)
  useEffect(() => {
    const mockMessages: ChatMessage[] = [
      {
        id: '1',
        roomId: state.currentRoom!.id,
        userId: 101,
        nickname: '쇼핑러버',
        message: '안녕하세요! 오늘 라이브 너무 기대돼요 ❤️',
        type: 'CHAT',
        timestamp: Date.now() - 300000,
      },
      {
        id: '2',
        roomId: state.currentRoom!.id,
        userId: 102,
        nickname: '뷰티마니아',
        message: '이 제품 색상이 정말 예뻐요!',
        type: 'CHAT',
        timestamp: Date.now() - 240000,
      },
      {
        id: '3',
        roomId: state.currentRoom!.id,
        userId: 103,
        nickname: '패션왕',
        message: '가격 할인 언제까지인가요?',
        type: 'CHAT',
        timestamp: Date.now() - 180000,
      },
    ];
    setMessages(mockMessages);
  }, [state.currentRoom]);

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !state.currentUser || state.currentUser.isGuest) return;

    const message: ChatMessage = {
      id: Date.now().toString(),
      roomId: state.currentRoom!.id,
      userId: state.currentUser.id,
      nickname: state.currentUser.nickname,
      message: newMessage.trim(),
      type: 'CHAT',
      timestamp: Date.now(),
    };

    setMessages(prev => [...prev, message]);
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
          {messages.map((message) => (
            <div key={message.id} className="message">
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
            </div>
          ))}
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
              maxLength={200}
            />
            <button
              type="submit"
              disabled={!newMessage.trim()}
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