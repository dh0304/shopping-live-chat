import React from 'react';
import { AppProvider, useAppContext } from './contexts/AppContext';
import LoginScreen from './components/LoginScreen';
import RoomListScreen from './components/RoomListScreen';
import ChatRoomScreen from './components/ChatRoomScreen';
import './App.css';

const AppContent: React.FC = () => {
  const { state } = useAppContext();

  if (!state.currentUser) {
    return <LoginScreen />;
  }

  if (state.currentRoom) {
    return <ChatRoomScreen />;
  }

  return <RoomListScreen />;
};

const App: React.FC = () => {
  return (
    <AppProvider>
      <div className="App">
        <AppContent />
      </div>
    </AppProvider>
  );
};

export default App;