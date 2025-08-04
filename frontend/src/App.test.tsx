import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders shopping live chat interface', () => {
  render(<App />);
  const titleElement = screen.getByText(/쇼핑 라이브 채팅/i);
  expect(titleElement).toBeInTheDocument();
  
  const joinButton = screen.getByText(/채팅방 입장하기/i);
  expect(joinButton).toBeInTheDocument();
});
