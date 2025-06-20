<script setup>
  const chats = [
    { id: 1, title: '가입 문의', preview: 'CRM 도입 절차를 알고 싶어요.' },
    { id: 2, title: '결제 오류', preview: '결제 페이지 접근이 안됩니다.' },
    { id: 3, title: '데이터 백업', preview: '지난달 고객 데이터 백업 가능한가요?' },
    { id: 4, title: '발송 제한', preview: '알림톡이 발송되지 않아요.' },
    { id: 5, title: '통계 리포트', preview: '직원별 실적 리포트 확인 방법 알려주세요.' },
  ];
</script>

<template>
  <div class="chat-list-view">
    <ul class="chat-list">
      <li v-for="chat in chats" :key="chat.id" class="chat-list-item">
        <div class="chat-avatar-circle">
          {{ chat.title.charAt(0) }}
        </div>
        <div class="chat-list-text">
          <p class="chat-title">{{ chat.title }}</p>
          <p class="chat-preview">{{ chat.preview }}</p>
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
  .chat-list-view {
    height: 100%;
    overflow-y: auto;
  }

  .chat-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .chat-list-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 0.5rem;
    border-bottom: 1px solid #e5e7eb;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }
  .chat-list-item:hover {
    background-color: #f0f2f5;
  }

  .chat-avatar-circle {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    background-color: #e0e7ff;
    color: #3f51b5;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    flex-shrink: 0;
  }

  .chat-list-text {
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  .chat-title {
    font-weight: 600;
    font-size: 13px;
    color: #222;
    margin-bottom: 1px;
  }

  .chat-preview {
    font-size: 12px;
    color: #666;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
</style>
