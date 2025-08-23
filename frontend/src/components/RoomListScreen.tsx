import React, { useEffect, useState, useMemo } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { ChatRoom } from '../types';
import { authApi } from '../api/authApi';
import './RoomListScreen.css';

const RoomListScreen: React.FC = () => {
  const { state, dispatch } = useAppContext();
  const [loading, setLoading] = useState(false);

  // 임시 채팅방 데이터 (실제로는 API에서 가져올 예정)
  const mockChatRooms: ChatRoom[] = useMemo(() => [
    {
      id: 1,
      name: '🎀 뷰티 라이브',
      description: '최신 화장품과 뷰티 팁을 공유해요!',
      activeUsers: 45,
      isActive: true,
    },
    {
      id: 2,
      name: '👗 패션 쇼핑',
      description: '트렌디한 패션 아이템을 소개합니다',
      activeUsers: 23,
      isActive: true,
    },
    {
      id: 3,
      name: '🏠 홈&리빙',
      description: '집꾸미기 아이템과 생활용품',
      activeUsers: 18,
      isActive: true,
    },
    {
      id: 4,
      name: '📱 전자제품',
      description: '최신 가젯과 전자제품 리뷰',
      activeUsers: 7,
      isActive: false,
    },
  ], []);

  useEffect(() => {
    setLoading(true);
    // 실제로는 API 호출
    setTimeout(() => {
      dispatch({ type: 'SET_CHAT_ROOMS', payload: mockChatRooms });
      setLoading(false);
    }, 500);
  }, [dispatch, mockChatRooms]);

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