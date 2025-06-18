<template>
  <div class="staff-create-page">
    <h2 class="page-title">직원 등록</h2>

    <ProfileUpload v-model="form.profileImage" />

    <div class="form-wrapper">
      <BaseForm
        v-model="form.name"
        label="직원 이름"
        placeholder="직원 이름을 입력해주세요."
        :error="errors.name"
      />
      <BaseForm
        v-model="form.email"
        label="이메일 주소"
        type="email"
        placeholder="이메일 주소(ID)를 입력해주세요."
        :error="errors.email"
      />
      <div class="form-group">
        <div class="form-label-row">
          <label for="password">비밀번호</label>
          <p class="form-hint">특수문자, 영문자, 숫자를 포함한 8자리 이상</p>
        </div>
        <BaseForm
          v-model="form.password"
          type="password"
          placeholder="비밀번호를 입력해주세요."
          :error="errors.password"
        />
        <BaseForm
          v-model="form.passwordConfirm"
          placeholder="비밀번호 확인"
          type="password"
          :error="errors.passwordConfirm"
        />
      </div>
      <BaseForm
        v-model="form.phone"
        label="전화번호"
        placeholder="전화번호를 입력해주세요."
        :error="errors.phone"
      />
      <BaseForm
        v-model="form.position"
        label="직급"
        type="select"
        :options="[
          { value: '', text: '직급을 선택하세요' },
          { value: '사장', text: '사장' },
          { value: '수석 디자이너', text: '수석 디자이너' },
          { value: '디자이너', text: '디자이너' },
          { value: '수습', text: '수습' },
        ]"
        :error="errors.position"
      />

      <BaseButton class="submit-btn" @click="handleSubmit">직원등록 ✔</BaseButton>
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import ProfileUpload from '@/features/users/components/ProfileUpload.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const form = ref({
    profileImage: '',
    name: '',
    email: '',
    password: '',
    passwordConfirm: '',
    phone: '',
    position: '',
  });

  const handleSubmit = () => {
    if (!validate()) return;
    // API 등록 로직 실행
    console.log('등록할 값:', form.value);
  };

  const errors = ref({
    name: '',
    email: '',
    password: '',
    passwordConfirm: '',
    phone: '',
    position: '',
  });

  const validate = () => {
    errors.value = {};

    if (!form.value.name) errors.value.name = '이름을 입력해주세요.';
    if (!form.value.email || !form.value.email.includes('@'))
      errors.value.email = '유효한 이메일을 입력해주세요.';
    if (!form.value.password || form.value.password.length < 6)
      errors.value.password = '비밀번호는 6자 이상이어야 합니다.';
    if (!form.value.password || form.value.password !== form.value.passwordConfirm)
      errors.value.passwordConfirm = '비밀번호가 일치하지 않습니다.';
    if (!form.value.phone) errors.value.phone = '휴대전화를 입력해주세요.';
    if (!form.value.position) errors.value.position = '직급을 선택해주세요.';

    return Object.keys(errors.value).length === 0;
  };
</script>

<style scoped>
  .staff-create-page {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
    padding: 24px;
    max-width: 400px;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  }
  .page-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 8px;
  }
  .submit-btn {
    margin-top: 12px;
    width: 100%;
  }
  .form-wrapper {
    width: 100%;
    max-width: 360px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .base-form select,
  .base-form input {
    width: 100%;
  }
  .form-group {
    display: flex;
    flex-direction: column;
    gap: 4px; /* ← 간격 확 줄이기 */
  }
  .form-label-row {
    display: flex;
    align-items: center;
    justify-content: space-between; /* 또는 gap: 8px; */
    margin-bottom: 6px;
  }
  .form-label-row label {
    font-weight: 700;
    font-size: 14px;
    color: #333;
  }
  .form-hint {
    font-size: 12px;
    color: #888;
  }
</style>
