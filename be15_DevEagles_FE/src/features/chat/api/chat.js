// src/api/chat.js
import api from '@/plugins/axios';

// 채팅방 생성
export const createChatRoom = async () => {
  const response = await api.post('/chatrooms');
  return response.data;
};

// 채팅 메시지 조회
export const getChatMessages = async roomId => {
  const response = await api.get(`/chatrooms/${roomId}/messages`);
  return response.data;
};

// 채팅방 목록 조회 (옵션)
export const getChatRooms = async () => {
  const response = await api.get('/chatrooms');
  return response.data;
};

// ✅ greeting 메시지 전송
export const sendGreetingMessage = async roomId => {
  await api.post(`/chatrooms/${roomId}/send-greeting`);
};

// 상담사 전환
export const switchToStaff = async roomId => {
  await api.post(`/chatrooms/${roomId}/switch-to-staff`);
};
