const http = require('http');
const WebSocket = require('ws');
const crypto = require('crypto');

// 사용자 데이터 설정 (VU별 고유 정보)
function setUserData(context, events, done) {
  // 해시 기반으로 1-10000 범위의 고유 사용자 생성
  const hash = crypto.randomBytes(4).readUInt32BE(0);
  const userId = (hash % 10000) + 1;
  context.vars.nickname = `user${userId}`;
  
  // 쿠키 저장을 위한 변수 초기화
  context.vars.cookies = '';
  context.vars.roomId = null;
  
  return done();
}


// 랜덤 범위 함수 (템플릿에서 사용)
function randomBetween(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

// 관찰자용 WebSocket 연결 (채팅 안함)
function connectWebSocketAndChat(context, events, done) {
  const nickname = context.vars.nickname;
  const roomId = context.vars.roomId;
  const cookies = context.vars.cookies;
  
  if (!roomId) {
    console.error(`❌ No roomId for ${nickname}`);
    return done();
  }
  
  const wsUrl = `ws://backend:8080/ws/websocket`;
  const wsOptions = {};
  
  // 쿠키가 있으면 헤더에 포함
  if (cookies) {
    wsOptions.headers = {
      'Cookie': cookies
    };
  }
  
  const ws = new WebSocket(wsUrl, wsOptions);
  context.vars.ws = ws;
  
  ws.on('open', () => {
    console.log(`🔌 WebSocket connected for ${nickname}`);
    
    // STOMP CONNECT
    const connectFrame = `CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\0`;
    ws.send(connectFrame);
  });
  
  ws.on('message', (data) => {
    const message = data.toString();
    
    if (message.startsWith('CONNECTED')) {
      console.log(`✅ STOMP CONNECTED for ${nickname}`);
      
      // 구독
      const subscribeFrame = `SUBSCRIBE\nid:sub-0\ndestination:/topic/room/${roomId}\n\n\0`;
      ws.send(subscribeFrame);
      
      const subscribeCountFrame = `SUBSCRIBE\nid:sub-1\ndestination:/topic/room/${roomId}/count\n\n\0`;
      ws.send(subscribeCountFrame);
      
      // 입장
      const joinFrame = `SEND\ndestination:/app/chat/rooms/${roomId}/users\ncontent-type:application/json\n\n${JSON.stringify({
        nickname: nickname,
        roomId: roomId,
        type: 'JOIN'
      })}\0`;
      ws.send(joinFrame);
      
      console.log(`🚪 Joined room ${roomId} for ${nickname}`);
    }
  });
  
  ws.on('error', (err) => {
    console.error(`❌ WebSocket error for ${nickname}:`, err);
  });
  
  ws.on('close', () => {
    console.log(`🔌 WebSocket closed for ${nickname}`);
  });
  
  return done();
}

// 참여자용 WebSocket 연결 (채팅 참여)
function connectWebSocketWithParticipation(context, events, done) {
  const nickname = context.vars.nickname;
  const roomId = context.vars.roomId;
  const cookies = context.vars.cookies;
  
  if (!roomId) {
    console.error(`❌ No roomId for ${nickname}`);
    return done();
  }
  
  const wsUrl = `ws://backend:8080/ws/websocket`;
  const wsOptions = {};
  
  // 쿠키가 있으면 헤더에 포함
  if (cookies) {
    wsOptions.headers = {
      'Cookie': cookies
    };
  }
  
  const ws = new WebSocket(wsUrl, wsOptions);
  context.vars.ws = ws;
  
  ws.on('open', () => {
    console.log(`🔌 WebSocket connected for participant ${nickname}`);
    
    // STOMP CONNECT
    const connectFrame = `CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\0`;
    ws.send(connectFrame);
  });
  
  ws.on('message', (data) => {
    const message = data.toString();
    
    if (message.startsWith('CONNECTED')) {
      console.log(`✅ STOMP CONNECTED for participant ${nickname}`);
      
      // 구독
      const subscribeFrame = `SUBSCRIBE\nid:sub-0\ndestination:/topic/room/${roomId}\n\n\0`;
      ws.send(subscribeFrame);
      
      const subscribeCountFrame = `SUBSCRIBE\nid:sub-1\ndestination:/topic/room/${roomId}/count\n\n\0`;
      ws.send(subscribeCountFrame);
      
      // 입장
      const joinFrame = `SEND\ndestination:/app/chat/rooms/${roomId}/users\ncontent-type:application/json\n\n${JSON.stringify({
        nickname: nickname,
        roomId: roomId,
        type: 'JOIN'
      })}\0`;
      ws.send(joinFrame);
      
      console.log(`🚪 Joined room ${roomId} for participant ${nickname}`);
      
      // 2-3분 후에 채팅 시작 스케줄링
      setTimeout(() => {
        startChatting(ws, nickname, roomId);
      }, 120000 + Math.random() * 60000); // 2-3분
    }
  });
  
  ws.on('error', (err) => {
    console.error(`❌ WebSocket error for participant ${nickname}:`, err);
  });
  
  return done();
}

// 구매자용 WebSocket 연결 (적극적 채팅)
function connectWebSocketWithBuying(context, events, done) {
  const nickname = context.vars.nickname;
  const roomId = context.vars.roomId;
  const cookies = context.vars.cookies;
  
  if (!roomId) {
    console.error(`❌ No roomId for ${nickname}`);
    return done();
  }
  
  const wsUrl = `ws://backend:8080/ws/websocket`;
  const wsOptions = {};
  
  // 쿠키가 있으면 헤더에 포함
  if (cookies) {
    wsOptions.headers = {
      'Cookie': cookies
    };
  }
  
  const ws = new WebSocket(wsUrl, wsOptions);
  context.vars.ws = ws;
  
  ws.on('open', () => {
    console.log(`🔌 WebSocket connected for buyer ${nickname}`);
    
    // STOMP CONNECT
    const connectFrame = `CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\0`;
    ws.send(connectFrame);
  });
  
  ws.on('message', (data) => {
    const message = data.toString();
    
    if (message.startsWith('CONNECTED')) {
      console.log(`✅ STOMP CONNECTED for buyer ${nickname}`);
      
      // 구독
      const subscribeFrame = `SUBSCRIBE\nid:sub-0\ndestination:/topic/room/${roomId}\n\n\0`;
      ws.send(subscribeFrame);
      
      const subscribeCountFrame = `SUBSCRIBE\nid:sub-1\ndestination:/topic/room/${roomId}/count\n\n\0`;
      ws.send(subscribeCountFrame);
      
      // 입장
      const joinFrame = `SEND\ndestination:/app/chat/rooms/${roomId}/users\ncontent-type:application/json\n\n${JSON.stringify({
        nickname: nickname,
        roomId: roomId,
        type: 'JOIN'
      })}\0`;
      ws.send(joinFrame);
      
      console.log(`🚪 Joined room ${roomId} for buyer ${nickname}`);
      
      // 4분 30초 후에 구매 관련 채팅 시작
      setTimeout(() => {
        startBuyerChatting(ws, nickname, roomId);
      }, 270000); // 4분 30초
    }
  });
  
  ws.on('error', (err) => {
    console.error(`❌ WebSocket error for buyer ${nickname}:`, err);
  });
  
  return done();
}

// 일반 채팅 시작
function startChatting(ws, nickname, roomId) {
  const messages = [
    '안녕하세요!',
    '재미있네요',
    '궁금한게 있어요',
    '좋은 정보 감사해요',
    '언제까지 하나요?',
    '다음에 또 올게요'
  ];
  
  let chatCount = Math.floor(Math.random() * 20) + 10; // 10-30개
  const interval = setInterval(() => {
    if (chatCount <= 0 || ws.readyState !== WebSocket.OPEN) {
      clearInterval(interval);
      return;
    }
    
    const randomMessage = messages[Math.floor(Math.random() * messages.length)];
    const messageFrame = `SEND\ndestination:/app/chat/rooms/${roomId}/messages\ncontent-type:application/json\n\n${JSON.stringify({
      nickname: nickname,
      message: randomMessage,
      type: 'CHAT'
    })}\0`;
    
    ws.send(messageFrame);
    console.log(`💬 Chat message sent by ${nickname}: ${randomMessage}`);
    chatCount--;
  }, 2000 + Math.random() * 3000); // 2-5초 간격
}

// 구매자 채팅 시작
function startBuyerChatting(ws, nickname, roomId) {
  const buyerMessages = [
    '구매하고 싶어요!',
    '할인가격이 얼마인가요?',
    '재고가 얼마나 남았나요?',
    '배송은 언제 되나요?',
    '지금 주문할게요!',
    '결제는 어떻게 하나요?'
  ];
  
  let chatCount = 40; // 고정 40개
  const interval = setInterval(() => {
    if (chatCount <= 0 || ws.readyState !== WebSocket.OPEN) {
      clearInterval(interval);
      return;
    }
    
    const randomMessage = buyerMessages[Math.floor(Math.random() * buyerMessages.length)];
    const messageFrame = `SEND\ndestination:/app/chat/rooms/${roomId}/messages\ncontent-type:application/json\n\n${JSON.stringify({
      nickname: nickname,
      message: randomMessage,
      type: 'CHAT'
    })}\0`;
    
    ws.send(messageFrame);
    console.log(`🛒 Buyer message sent by ${nickname}: ${randomMessage}`);
    chatCount--;
  }, 2000 + Math.random() * 3000); // 2-5초 간격
}


// 로그인 응답에서 쿠키 저장
function saveCookie(requestParams, response, context, ee, next) {
  if (response.headers && response.headers['set-cookie']) {
    // Set-Cookie 헤더가 배열로 올 수 있으므로 join으로 합치기
    context.vars.cookies = response.headers['set-cookie'].join('; ');
    console.log(`🍪 Cookie saved for ${context.vars.nickname}: ${context.vars.cookies}`);
  }
  return next();
}

module.exports = {
  setUserData,
  connectWebSocketAndChat,
  connectWebSocketWithParticipation,
  connectWebSocketWithBuying,
  randomBetween,
  saveCookie
};