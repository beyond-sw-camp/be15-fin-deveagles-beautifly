<template>
  <div class="staff-detail-view">
    <h2>직원 정보 설정</h2>

    <div class="staff-detail-layout">
      <div class="form-column">
        <StaffForm v-model="staffForm" />
      </div>
      <div class="permission-column">
        <StaffPermission v-model="staffForm.permissions" />
      </div>
    </div>

    <!-- 저장 버튼 -->
    <div class="button-area">
      <BaseButton @click="handleSave">저장하기</BaseButton>
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import StaffForm from '@/features/staffs/components/StaffForm.vue';
  import StaffPermission from '@/features/staffs/components/StaffPermission.vue';

  // 직원 정보 초기값
  const staffForm = ref({
    profileImage: '',
    colorCode: '#FFB6B6',
    name: '김보라',
    username: 'bora@example.com',
    phone: '010-1111-1111',
    position: '수석 디자이너',
    status: true,
    joinDate: '2021-12-25',
    retireDate: '',
    permissions: [
      {
        key: 'customer',
        label: '고객 관리',
        enabled: true,
        read: true,
        write: false,
        delete: false,
      },
      {
        key: 'reservation',
        label: '예약 관리',
        enabled: false,
        read: false,
        write: false,
        delete: false,
      },
      {
        key: 'sales',
        label: '매출 관리',
        enabled: true,
        read: true,
        write: true,
        delete: false,
      },
    ],
  });

  const handleSave = () => {
    // todo api 연동
    console.log('직원 정보 저장:', staffForm.value);
  };
</script>

<style scoped>
  .staff-detail-layout {
    display: flex;
    gap: 24px;
    align-items: flex-start;
  }
  .form-column {
    flex: 1;
    min-width: 0;
  }
  .permission-column {
    width: 300px; /* or 350px, 너네 권한 칸 사이즈에 따라 */
    min-width: 280px;
  }
  .staff-detail-view {
    max-width: 720px;
    margin: 0 auto;
    padding: 32px 24px;
    background-color: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  h2 {
    font-size: 20px;
    margin-bottom: 24px;
  }
  .button-area {
    margin-top: 32px;
    display: flex;
    justify-content: flex-end;
  }
</style>
