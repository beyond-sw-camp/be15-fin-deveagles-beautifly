<template>
  <div class="password-edit-view">
    <img :src="Logo" alt="로고" class="logo center-logo" />
    <h1 class="page-title">비밀번호 재설정</h1>
    <PasswordEditForm :redirect-path="'/login'" :on-cancel="handleCancel" />
  </div>
</template>

<script setup>
  import { useRouter } from 'vue-router';
  import PasswordEditForm from '@/features/users/components/PasswordEditForm.vue';
  import Logo from '@/images/logo_name_navy.png';
  const router = useRouter();
  const handleCancel = () => router.push('/login');
</script>

<style scoped>
  .page-title {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 40px;
  }

  .password-edit-view {
    padding: 40px 20px;
    max-width: 440px;
    margin: 0 auto;
    background: white;
    border: 1px solid var(--color-gray-200);
    border-radius: 10px;
    margin-top: 90px;
  }

  .page-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 24px;
    text-align: center;
  }

  .logo {
    height: 150px;
    margin-bottom: 24px;
  }

  .center-logo {
    display: block;
    margin-left: auto;
    margin-right: auto;
  }
</style>
