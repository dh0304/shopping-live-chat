import React, { useState } from 'react';
import JoinForm from './components/JoinForm';
import LiveChatRoom from './components/LiveChatRoom';
import './App.css';

interface UserSession {
  roomId: string;
  userId: string;
  nickname: string;
}

const App: React.FC = () => {
  const [userSession, setUserSession] = useState<UserSession | null>(null);

  const handleJoin = (roomId: string, nickname: string) => {
    const userId = `user_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    setUserSession({
      roomId,
      userId,
      nickname
    });
  };

  const handleLeave = () => {
    setUserSession(null);
  };

  return (
    <div className="App">
      {userSession ? (
        <div className="chat-layout">
          <LiveChatRoom
            roomId={userSession.roomId}
            userId={userSession.userId}
            nickname={userSession.nickname}
          />
          <button className="leave-button" onClick={handleLeave}>
            채팅방 나가기
          </button>
        </div>
      ) : (
        <JoinForm onJoin={handleJoin} />
      )}
    </div>
  );
};

export default App;
