<template>
  <div class="signup-container">
    <div class="signup-box">
      <component
        :is="currentStep === 'user' ? EnrollUserForm : EnrollStoreForm"
        ref="stepForm"
        v-model="form[currentStep]"
        :is-required="isRequired"
      />

      <div class="button-row">
        <BaseButton
          v-if="currentStep === 'user'"
          type="primary"
          class="next-button"
          @click="goToNext"
        >
          다음으로
        </BaseButton>

        <BaseButton v-else type="primary" class="submit-button" @click="handleSubmit">
          회원가입
        </BaseButton>
      </div>
    </div>
  </div>

  <BaseModal v-model="showConfirmModal" title="">
    <p class="modal-text">
      입력하신 이메일로 인증을<br />
      완료하시면 회원가입이 완료됩니다,
    </p>
    <template #footer>
      <div class="modal-footer-buttons">
        <BaseButton type="error" @click="showConfirmModal = false">취소</BaseButton>
        <BaseButton type="primary" @click="submit">확인</BaseButton>
      </div>
    </template>
  </BaseModal>

  <BaseModal v-model="showUserConfirmModal" title="회원 정보를 확인해주세요">
    <template #default>
      <p>입력하신 회원 정보는 저장 후 수정할 수 없습니다. 다음 단계로 진행할까요?</p>
    </template>
    <template #footer>
      <BaseButton @click="showUserConfirmModal = false">취소</BaseButton>
      <BaseButton type="primary" @click="confirmUserAndGoNext"> 다음으로 </BaseButton>
    </template>
  </BaseModal>

  <BaseToast ref="toastRef" />
</template>
<script setup>
  import { ref } from 'vue';
  import EnrollUserForm from '@/features/users/components/EnrollUserForm.vue';

  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import EnrollStoreForm from '@/features/users/components/EnrollStoreForm.vue';
  import { signUp } from '@/features/users/api/users.js';
  import BaseToast from '@/components/common/BaseToast.vue';
  import { useRouter } from 'vue-router';

  const form = ref({
    user: {
      loginId: '',
      email: '',
      password: '',
      staffName: '',
      phoneNumber: '',
    },
    shop: {
      shopName: '',
      address: '',
      detailAddress: '',
      industryId: '',
      phoneNumber: '',
      businessNumber: '',
    },
  });

  const isRequired = fieldName => {
    return [
      'loginId',
      'email',
      'password',
      'checkPwd',
      'staffName',
      'phoneNumber',
      'shopName',
      'industryId',
      'address',
    ].includes(fieldName);
  };

  const router = useRouter();

  const currentStep = ref('user');
  const showConfirmModal = ref(false);

  const stepForm = ref();
  const toastRef = ref();

  const showUserConfirmModal = ref(false);

  const goToNext = async () => {
    const isValid = await stepForm.value?.validate?.();
    if (!isValid) {
      toastRef.value?.error?.('입력 정보를 다시 확인해주세요.');
      return;
    }
    showUserConfirmModal.value = true;
  };

  const confirmUserAndGoNext = () => {
    showUserConfirmModal.value = false;
    currentStep.value = 'shop';
  };

  const handleSubmit = async () => {
    const isValid = await stepForm.value?.validate?.();
    if (!isValid) {
      toastRef.value?.error?.('입력 정보를 다시 확인해주세요.');
      return;
    }
    showConfirmModal.value = true;
  };

  const submit = async () => {
    showConfirmModal.value = false;
    const payload = {
      user: { ...form.value.user },
      shop: { ...form.value.shop },
    };

    try {
      const res = await signUp(payload);
      toastRef.value?.success?.('회원가입이 완료되었습니다.');
      router.push('/login');
    } catch (err) {
      toastRef.value?.error?.('회원가입 중 오류가 발생했습니다.');
    }
  };
</script>
<style scoped>
  .signup-box {
    background: white;
    padding: 40px;
    border-radius: 16px;
    box-shadow: var(--shadow-drop);
    width: 560px;
    border: 1px solid var(--color-gray-200);
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .signup-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 60px 20px;
    background-color: var(--color-gray-50);
  }
  .submit-button,
  .next-button {
    margin-top: 32px;
    width: 100%;
  }

  .full-width {
    width: 100%;
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
