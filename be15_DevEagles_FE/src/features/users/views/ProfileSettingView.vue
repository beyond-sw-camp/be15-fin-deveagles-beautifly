<template>
  <div class="mypage-container">
    <div v-if="isLoading">로딩 중 ...</div>
    <div class="profile-card-vertical">
      <div class="form-fields">
        <ProfileUpload v-model:model-value="profilePreview" />

        <div class="label-row">
          <label for="userName">이름</label>
        </div>
        <BaseForm id="userName" v-model="user.userName" type="text" />

        <div class="label-row">
          <label for="grade">직급</label>
        </div>
        <BaseForm
          id="grade"
          v-model="user.grade"
          type="select"
          :options="[
            { value: '1', text: '사장' },
            { value: '2', text: '수석 디자이너' },
            { value: '3', text: '디자이너' },
            { value: '4', text: '수습' },
          ]"
        />
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
  import ProfileUpload from '@/features/users/components/ProfileUpload.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { onMounted, ref } from 'vue';

  const isLoading = ref(true);
  const user = ref({
    userName: '',
    grade: '',
    thumbnailUrl: '',
  });

  const profilePreview = ref(user.value.thumbnailUrl || '');

  onMounted(() => {
    user.value = {
      userName: '김이글',
      grade: '1',
      thumbnailUrl: '',
    };
    isLoading.value = false;
  });

  const handleEdit = () => {};
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
</style>
