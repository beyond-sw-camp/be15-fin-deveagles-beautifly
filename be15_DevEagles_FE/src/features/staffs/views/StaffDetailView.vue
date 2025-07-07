<template>
  <div class="staff-detail-view">
    <h2>직원 정보 설정</h2>

    <div v-if="staff" class="staff-detail-layout">
      <div class="form-column">
        <StaffForm v-model="staff" />
      </div>
      <div class="permission-column">
        <StaffPermission v-model="staff.permissions" />
      </div>
    </div>

    <!-- 저장 버튼 -->
    <div class="button-area">
      <BaseButton @click="handleSave">저장하기</BaseButton>
    </div>
  </div>
  <BaseToast ref="toastRef" />
</template>

<script setup>
  import { onMounted, ref } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import StaffForm from '@/features/staffs/components/StaffForm.vue';
  import StaffPermission from '@/features/staffs/components/StaffPermission.vue';
  import { useRoute, useRouter } from 'vue-router';
  import { getStaffDetail } from '@/features/staffs/api/staffs.js';
  import BaseToast from '@/components/common/BaseToast.vue';

  const route = useRoute();
  const router = useRouter();
  const toastRef = ref();

  const staffId = route.params.staffId;
  const staff = ref(null);

  const fetchStaffDetail = async () => {
    try {
      const res = await getStaffDetail(staffId);
      staff.value = res.data.data;
    } catch (err) {
      const message = err?.message || '존재하지 않는 직원입니다.';
      toastRef.value?.error?.(message);
      setTimeout(() => {
        router.push('/settings/staff');
      }, 1000);
    }
  };

  onMounted(() => {
    fetchStaffDetail();
  });

  const handleSave = () => {
    // todo api 연동
    console.log('직원 정보 저장:', staff.value);
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
