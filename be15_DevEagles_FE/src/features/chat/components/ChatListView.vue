<script setup>
  import { onMounted, ref } from 'vue';
  import { getChatRooms } from '@/features/chat/api/chat.js'; // ✅ 너가 만든 API 명칭

  const emit = defineEmits(['select']);
  const chatRooms = ref([]);

  onMounted(async () => {
    try {
      const res = await getChatRooms(); // ✅ 정확한 함수명
      chatRooms.value = res.data.sort(
        (a, b) => new Date(b.lastMessageAt || 0) - new Date(a.lastMessageAt || 0)
      );
    } catch (e) {
      console.error('❌ 채팅방 목록 불러오기 실패:', e);
    }
  });
</script>

<template>
  <div class="chat-list">
    <p class="chat-list-title">내가 배정받은 채팅방</p>

    <div
      v-for="room in chatRooms"
      :key="room.roomId"
      class="chat-room-card"
      @click="$emit('select', room.roomId)"
    >
      <div class="room-id">채팅방 ID: {{ room.roomId }}</div>

      <div class="last-message-wrapper">
        <span class="last-message-label">마지막 메시지:</span>
        <span class="last-message-content">
          {{ room.lastMessage || '메시지 없음' }}
        </span>
      </div>

      <div v-if="room.lastMessageAt" class="last-message-time">
        {{ new Date(room.lastMessageAt).toLocaleString() }}
      </div>
    </div>
  </div>
</template>

<style scoped>
  .chat-list {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .chat-list-title {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 0.5rem;
    color: var(--color-gray-800);
  }

  .chat-room-card {
    background: white;
    border: 1px solid var(--color-gray-300);
    border-radius: 10px;
    padding: 0.75rem 1rem;
    cursor: pointer;
    transition: background 0.2s ease;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  }

  .chat-room-card:hover {
    background: #f8f9fa;
  }

  .room-id {
    font-weight: 600;
    font-size: 13px;
    color: var(--color-gray-900);
    margin-bottom: 0.4rem;
    word-break: break-word;
  }

  .last-message-wrapper {
    display: flex;
    flex-direction: column;
  }

  .last-message-label {
    font-size: 12px;
    color: var(--color-gray-600);
  }

  .last-message-content {
    font-size: 13px;
    color: var(--color-gray-800);
    margin-top: 2px;
    word-break: break-word;
  }

  .last-message-time {
    font-size: 12px;
    color: var(--color-gray-500);
    margin-top: 6px;
  }
</style>
