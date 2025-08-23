import React, { useEffect, useState } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { ChatRoom } from '../types';
import { ChatRoomListResponse } from '../types/ChatTypes';
import { authApi } from '../api/authApi';
import { chatRoomApi } from '../api/chatRoomApi';
import './RoomListScreen.css';

const RoomListScreen: React.FC = () => {
  const { state, dispatch } = useAppContext();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 백엔드 데이터를 프론트엔드 형태로 변환하는 함수
  const convertToFrontendChatRoom = (backendRoom: ChatRoomListResponse): ChatRoom => ({
    id: backendRoom.id,
    name: backendRoom.roomName,
    description: backendRoom.description,
    activeUsers: backendRoom.userCount,
    isActive: true, // 기본값으로 활성 상태, 추후 실제 상태 API 추가 예정
  });

  useEffect(() => {
    const fetchChatRooms = async () => {
      setLoading(true);
      setError(null);
      try {
        const backendRooms = await chatRoomApi.getChatRooms();
        const frontendRooms = backendRooms.map(convertToFrontendChatRoom);
        dispatch({ type: 'SET_CHAT_ROOMS', payload: frontendRooms });
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '채팅방 목록을 불러오는데 실패했습니다.';
        setError(errorMessage);
        console.error('채팅방 목록 로드 실패:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchChatRooms();
  }, [dispatch]);

  const handleRoomSelect = (room: ChatRoom) => {
    if (!room.isActive) return;
    dispatch({ type: 'JOIN_ROOM', payload: room });
  };

  const handleLogout = async () => {
    try {
      await authApi.logout();
      dispatch({ type: 'LOGOUT_USER' });
    } catch (error) {
      console.error('로그아웃 실패:', error);
      dispatch({ type: 'LOGOUT_USER' });
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-content">
          <div className="loading-spinner"></div>
          <p className="loading-text">채팅방 목록을 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="room-list-screen">
        <header className="room-list-header">
          <div className="header-content">
            <div className="header-info">
              <h1>🛍️ 쇼핑 라이브</h1>
              <p>
                안녕하세요, {state.currentUser?.nickname}님!{' '}
                {state.currentUser?.isGuest && '(비회원)'}
              </p>
            </div>
            <button onClick={handleLogout} className="logout-btn">
              로그아웃
            </button>
          </div>
        </header>
        <div className="error-container">
          <div className="error-content">
            <p className="error-text">{error}</p>
            <button 
              onClick={() => window.location.reload()} 
              className="retry-btn"
            >
              다시 시도
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="room-list-screen">
      <header className="room-list-header">
        <div className="header-content">
          <div className="header-info">
            <h1>🛍️ 쇼핑 라이브</h1>
            <p>
              안녕하세요, {state.currentUser?.nickname}님!{' '}
              {state.currentUser?.isGuest && '(비회원)'}
            </p>
          </div>
          <button onClick={handleLogout} className="logout-btn">
            로그아웃
          </button>
        </div>
      </header>

      <main className="room-list-main">
        <div className="main-header">
          <h2 className="main-title">라이브 채팅방</h2>
          <p className="main-subtitle">참여하고 싶은 라이브 쇼핑 채팅방을 선택하세요!</p>
        </div>

        <div className="rooms-grid">
          {state.chatRooms.map((room) => (
            <div
              key={room.id}
              onClick={() => handleRoomSelect(room)}
              className={`room-card ${!room.isActive ? 'room-card--inactive' : ''}`}
            >
              <div className="room-header">
                <h3 className="room-title">{room.name}</h3>
                <div className={`room-status ${room.isActive ? 'room-status--active' : 'room-status--inactive'}`} />
              </div>
              
              <p className="room-description">{room.description}</p>
              
              <div className="room-footer">
                <div className="room-users">{room.activeUsers}명 참여중</div>
                <span className={`room-badge ${room.isActive ? 'room-badge--active' : 'room-badge--inactive'}`}>
                  {room.isActive ? 'LIVE' : 'OFFLINE'}
                </span>
              </div>
            </div>
          ))}
        </div>

        {state.currentUser?.isGuest && (
          <div className="guest-notice">
            <span className="guest-notice-icon">ℹ️</span>
            <p className="guest-notice-text">
              비회원은 채팅 참여가 제한되며, 하트 버튼만 사용할 수 있습니다.
            </p>
          </div>
        )}
      </main>
    </div>
  );
};

export default RoomListScreen;