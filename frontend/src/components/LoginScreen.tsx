import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext';
import { authApi } from '../api/authApi';
import './LoginScreen.css';

const LoginScreen: React.FC = () => {
  const [nickname, setNickname] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { dispatch } = useAppContext();

  const handleMemberLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!nickname.trim()) return;

    setLoading(true);
    setError('');

    try {
      const response = await authApi.login(nickname.trim());
      
      dispatch({
        type: 'LOGIN_USER',
        payload: {
          id: response.userId,
          nickname: response.nickname,
          isGuest: false,
        },
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : '로그인 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleGuestLogin = () => {
    const guestNickname = `게스트${Math.floor(Math.random() * 1000)}`;
    dispatch({
      type: 'LOGIN_USER',
      payload: {
        id: Date.now(),
        nickname: guestNickname,
        isGuest: true,
      },
    });
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-header">
          <h1 className="login-title">🛍️ 쇼핑 라이브</h1>
          <p className="login-subtitle">실시간 쇼핑 채팅에 참여하세요!</p>
        </div>

        <form onSubmit={handleMemberLogin} className="login-form">
          <div className="form-group">
            <label htmlFor="nickname" className="form-label">닉네임</label>
            <input
              id="nickname"
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="닉네임을 입력하세요"
              className="form-input"
              maxLength={20}
            />
          </div>

          <button
            type="submit"
            disabled={!nickname.trim() || loading}
            className="btn-primary"
          >
            {loading ? '로그인 중...' : '회원으로 입장하기'}
          </button>
        </form>

        {error && (
          <div className="error-message" style={{ color: 'red', textAlign: 'center', margin: '10px 0' }}>
            {error}
          </div>
        )}

        <div className="divider">
          <div className="divider-text">또는</div>
        </div>

        <button onClick={handleGuestLogin} className="btn-secondary">
          비회원으로 접속하기
        </button>

        <div className="login-info">
          <p className="info-text">
            💡 회원은 채팅 참여가 가능하고, 비회원은 하트만 누를 수 있어요!
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginScreen;