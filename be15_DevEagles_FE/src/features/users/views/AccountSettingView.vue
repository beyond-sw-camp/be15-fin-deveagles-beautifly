<template>
  <div class="mypage-container">
    <div v-if="isLoading">로딩 중 ...</div>

    <div class="profile-card-vertical">
      <div class="form-fields">
        <div class="label-row">
          <label for="email">회원 아이디(이메일 주소)</label>
        </div>
        <BaseForm id="email" v-model="user.email" type="email" />

        <div class="label-row">
          <label for="newPassword">변경할 비밀번호</label>
        </div>
        <BaseForm id="newPassword" v-model="user.newPassword" type="password" />

        <div class="label-row">
          <label for="confirmPassword">비밀번호 확인</label>
        </div>
        <BaseForm id="confirmPassword" v-model="user.confirmPassword" type="password" />

        <div class="label-row">
          <label>알림 설정</label>
          <BaseToggleSwitch v-model="user.notification" />
        </div>

        <div class="button-row">
          <BaseButton class="edit-button" type="primary" @click="handleEdit">
            변경사항 저장
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
  import { onMounted, ref } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToggleSwitch from '@/components/common/BaseToggleSwitch.vue';

  const isLoading = ref(true);
  const user = ref({
    email: '',
    newPassword: '',
    confirmPassword: '',
    notification: '',
  });

  onMounted(() => {
    user.value = {
      email: 'eagles@example.com',
      newPassword: '',
      confirmPassword: '',
      notification: true,
    };
    isLoading.value = false;
  });

  const handleEdit = () => {
    // todo 변경사항 저장 로직
  };
</script>
<style scoped>
  .profile-card-vertical {
    display: flex;
    flex-direction: column;
    align-items: center;
    border: 1px solid var(--color-gray-200);
    padding: 40px 24px;
    border-radius: 16px;
    background-color: #fff;
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
    font-family: 'Noto Sans KR', sans-serif;
    color: var(--color-gray-800);
  }
  .form-fields {
    width: 360px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .label-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4px;
    font-weight: 600;
    font-size: 14px;
    color: var(--color-gray-900);
  }
  .edit-button {
    margin-top: 32px;
    width: 100%;
  }
  .mypage-container {
    padding: 40px 20px;
    max-width: 800px;
    margin: 0 auto;
  }

  .toggle-switch input[type='radio'] {
    display: none;
  }

  /* 스위치 스타일 공통 */
  .toggle-switch label {
    position: relative;
    display: inline-block;
    width: 48px;
    height: 24px;
    background-color: #ccc;
    border-radius: 50px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  .toggle-switch label::after {
    content: '';
    position: absolute;
    width: 18px;
    height: 18px;
    top: 3px;
    left: 3px;
    background-color: white;
    border-radius: 50%;
    transition: left 0.3s ease;
  }
  .toggle-switch input[type='radio']:checked + label {
    background-color: #3fc1c9;
  }
  .toggle-switch input[type='radio']:checked + label::after {
    left: 27px;
  }
</style>
