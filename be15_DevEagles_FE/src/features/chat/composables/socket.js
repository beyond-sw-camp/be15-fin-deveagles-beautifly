import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
const wsUrl = `${apiBaseUrl}/ws-chat`;

const SUB_PREFIX = '/sub/chatroom';
const PUB_DESTINATION = '/pub/chat/send';

let stompClient = null;
let isConnected = false;
const subscriptionMap = new Map();

export const ensureSocketConnected = async (onReceive, onAuthError) => {
  if (stompClient?.connected) return;

  const token = localStorage.getItem('accessToken');
  if (!token) {
    console.warn('❌ WebSocket 연결 실패: accessToken 없음');
    onAuthError?.();
    return;
  }

  stompClient = new Client({
    webSocketFactory: () => new SockJS(wsUrl),
    connectHeaders: { Authorization: `Bearer ${token}` },
    debug: () => {},
    reconnectDelay: 3000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => {
      console.info('✅ WebSocket 연결 성공');
      isConnected = true;
    },
    onStompError: frame => {
      console.error('🚫 STOMP 오류', frame);
      isConnected = false;
      onAuthError?.();
    },
    onWebSocketClose: () => {
      console.warn('🔌 WebSocket 연결 종료됨');
      isConnected = false;
    },
    onWebSocketError: err => {
      console.error('❗ WebSocket 에러', err);
      isConnected = false;
    },
  });

  stompClient.activate();

  while (!stompClient.connected) {
    await new Promise(res => setTimeout(res, 100));
  }
};

export const safeSubscribeToRoom = (roomId, onReceive) => {
  if (subscriptionMap.has(roomId)) return;

  const sub = stompClient.subscribe(`${SUB_PREFIX}/${roomId}`, msg => {
    const parsed = JSON.parse(msg.body);
    console.log('💬 [WebSocket 메시지 수신됨]', parsed);
    onReceive(parsed);
  });

  subscriptionMap.set(roomId, sub);
  console.info(`📡 구독 완료: ${roomId}`);
};

export const sendSocketMessage = (roomId, message) => {
  if (!isConnected || !stompClient?.connected) {
    console.warn('🚫 WebSocket 미연결 상태');
    return;
  }

  try {
    stompClient.publish({
      destination: PUB_DESTINATION,
      body: JSON.stringify({ ...message, roomId }),
    });
  } catch (e) {
    console.error('❌ 메시지 발송 실패', e);
  }
};

export const disconnectSocket = () => {
  subscriptionMap.forEach(sub => sub.unsubscribe());
  subscriptionMap.clear();
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
  isConnected = false;
};
