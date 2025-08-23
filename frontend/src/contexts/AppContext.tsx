import React, { createContext, useContext, useReducer, ReactNode } from 'react';
import { User, ChatRoom } from '../types';

interface AppState {
  currentUser: User | null;
  currentRoom: ChatRoom | null;
  chatRooms: ChatRoom[];
}

type AppAction =
  | { type: 'LOGIN_USER'; payload: User }
  | { type: 'LOGOUT_USER' }
  | { type: 'SET_CHAT_ROOMS'; payload: ChatRoom[] }
  | { type: 'JOIN_ROOM'; payload: ChatRoom }
  | { type: 'LEAVE_ROOM' };

interface AppContextType {
  state: AppState;
  dispatch: React.Dispatch<AppAction>;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

const initialState: AppState = {
  currentUser: null,
  currentRoom: null,
  chatRooms: [],
};

function appReducer(state: AppState, action: AppAction): AppState {
  switch (action.type) {
    case 'LOGIN_USER':
      return { ...state, currentUser: action.payload };
    case 'LOGOUT_USER':
      return { ...initialState };
    case 'SET_CHAT_ROOMS':
      return { ...state, chatRooms: action.payload };
    case 'JOIN_ROOM':
      return { ...state, currentRoom: action.payload };
    case 'LEAVE_ROOM':
      return { ...state, currentRoom: null };
    default:
      return state;
  }
}

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [state, dispatch] = useReducer(appReducer, initialState);

  return (
    <AppContext.Provider value={{ state, dispatch }}>
      {children}
    </AppContext.Provider>
  );
};

export const useAppContext = (): AppContextType => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
};