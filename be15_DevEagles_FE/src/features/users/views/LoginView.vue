<template>
  <div class="login-container input-reset-scope">
    <form :class="['login-vbox', { shake }]" @submit.prevent="fetchUser">
      <img :src="Logo" alt="로고" class="logo" />

      <BaseForm
        v-model="params.username"
        label="아이디"
        type="text"
        placeholder="아이디를 입력해주세요."
        @focus="errorMessage = ''"
      />

      <BaseForm
        v-model="params.password"
        label="비밀번호"
        type="password"
        placeholder="비밀번호를 입력해주세요."
        :error="errorMessage"
        @focus="errorMessage = ''"
      />

      <div class="login-links">
        <a href="#" @click.prevent="goToFindId">아이디 찾기</a>
        <span>|</span>
        <a href="#" @click.prevent="goToFindPwd">비밀번호 찾기</a>
      </div>

      <div class="login-buttons">
        <a class="btn btn-primary" @click.prevent="goToSignup">회원가입</a>
        <button class="btn btn-primary" type="submit">로그인</button>
      </div>
    </form>
  </div>

  <BaseModal v-model="showVerifyModal" title="미인증 회원">
    <p class="modal-text">
      인증되지 않은 회원입니다.<br />
      이메일을 인증하시겠습니까?
    </p>

    <template #footer>
      <div class="modal-footer-buttons">
        <BaseButton type="error" @click="showVerifyModal = false">취소</BaseButton>
        <BaseButton type="primary" @click="goVerifyEmail">확인</BaseButton>
      </div>
    </template>
  </BaseModal>
</template>
<script setup lang="ts">
  import BaseForm from '@/components/common/BaseForm.vue';
  import { useRouter } from 'vue-router';
  import { ref } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import Logo from '@/images/logo_name_navy.png';
  import BaseModal from '@/components/common/BaseModal.vue';

  const router = useRouter();
  //const authStore = useAuthStore();
  const params = ref({
    username: '',
    password: '',
  });

  const errorMessage = ref('');
  const shake = ref(false);
  const showVerifyModal = ref(false);

  const fetchUser = () => {
    // login api 호출
    // try - catch => 아이디 / 비밀번호 분기해서 에러 띄우기
  };

  const goVerifyEmail = () => {
    showVerifyModal.value = false;
    // 회원 이메일 인증 api 호출
  };

  const goToFindId = () => {
    router.push('/find-id');
  };

  const goToFindPwd = () => {
    router.push('/find-pwd');
  };

  const goToSignup = () => {
    router.push('/signup');
  };
</script>
<style scoped>
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: var(--color-gray-50);
  }

  .login-box {
    background-color: var(--color-neutral-white);
    border: 1px solid var(--color-gray-200);
    border-radius: 8px;
    padding: 40px;
    width: 400px;
    box-shadow: var(--shadow-drop);
    transition: transform 0.2s ease-in-out;
  }

  .logo {
    display: block;
    margin: 0 auto 24px;
    height: 300px;
  }

  .login-links {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    font-size: 12px;
    color: var(--color-gray-600);
    margin-top: 8px;
    margin-bottom: 24px;
  }

  .login-links a {
    color: var(--color-gray-700);
    text-decoration: none;
  }

  .login-buttons {
    display: flex;
    justify-content: space-between;
    gap: 12px;
  }

  .input-reset-scope :deep(.input-error) {
    border-color: inherit !important;
    background-color: inherit !important;
    box-shadow: none !important;
  }

  .modal-text {
    text-align: center;
    font-size: 16px;
    font-weight: 700;
    line-height: 1.5;
    padding: 16px 0;
  }

  .modal-footer-buttons {
    display: flex;
    justify-content: center;
    gap: 12px;
  }
</style>
