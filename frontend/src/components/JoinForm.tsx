import React, { useState } from 'react';
import { parseId } from '../utils/typeConverters';
import './JoinForm.css';

interface JoinFormProps {
  onJoin: (roomId: number, nickname: string) => void;
}

const JoinForm: React.FC<JoinFormProps> = ({ onJoin }) => {
  const [roomId, setRoomId] = useState<string>('1');
  const [nickname, setNickname] = useState<string>('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (roomId.trim() && nickname.trim()) {
      const numericRoomId = parseId(roomId.trim());
      if (numericRoomId !== null) {
        onJoin(numericRoomId, nickname.trim());
      }
    }
  };

  return (
    <div className="join-container">
      <div className="join-card">
        <div className="join-header">
          <h2>🛍️ 쇼핑 라이브 채팅</h2>
          <p>실시간 채팅에 참여해보세요!</p>
        </div>
        
        <form onSubmit={handleSubmit} className="join-form">
          <div className="form-group">
            <label htmlFor="roomId">라이브 방 ID</label>
            <input
              id="roomId"
              type="text"
              value={roomId}
              onChange={(e) => setRoomId(e.target.value)}
              placeholder="방 ID를 입력하세요"
              className="form-input"
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="nickname">닉네임</label>
            <input
              id="nickname"
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="닉네임을 입력하세요"
              className="form-input"
              maxLength={20}
              required
            />
          </div>
          
          <button
            type="submit"
            className="join-button"
            disabled={!roomId.trim() || !nickname.trim() || parseId(roomId.trim()) === null}
          >
            채팅방 입장하기
          </button>
        </form>
        
        <div className="join-info">
          <p>💡 팁: 다른 브라우저나 시크릿 모드로 여러 창을 열어서 테스트해보세요!</p>
        </div>
      </div>
    </div>
  );
};

export default JoinForm;