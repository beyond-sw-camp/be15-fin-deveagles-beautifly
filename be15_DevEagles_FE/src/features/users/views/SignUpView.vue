<template>
  <div class="signup-container">
    <div class="signup-box">
      <component
        :is="currentStep === 'user' ? EnrollUserForm : EnrollStoreForm"
        v-if="form[currentStep]"
        :ref="currentStep === 'user' ? 'userForm' : 'storeForm'"
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
</template>
<script setup>
  import { ref } from 'vue';
  import EnrollUserForm from '@/features/users/components/EnrollUserForm.vue';

  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import EnrollStoreForm from '@/features/users/components/EnrollStoreForm.vue';

  const form = ref({
    user: {
      email: '',
      password: '',
      checkPwd: '',
      userName: '',
      phoneNumber: '',
    },
    store: {
      storeName: '',
      address: '',
      detailAddress: '',
      category: '',
      storePhone: '',
      bizNumber: '',
    },
  });

  const isRequired = fieldName => {
    return [
      'email',
      'password',
      'checkPwd',
      'userName',
      'phoneNumber',
      'storeName',
      'category',
    ].includes(fieldName);
  };

  const currentStep = ref('user');
  const showConfirmModal = ref(false);

  const userForm = ref();
  const storeForm = ref();

  const goToNext = () => {
    console.log('클릭');
    if (userForm.value?.validate()) {
      currentStep.value = 'store';
    } else {
      //
    }
  };

  const handleSubmit = () => {
    if (!storeForm.value?.validate()) return;
    showConfirmModal.value = true;
  };

  const submit = () => {
    showConfirmModal.value = false;
    const payload = {
      user: { ...form.value.user },
      store: { ...form.value.store },
    };

    //todo : api 연결
    console.log('회원가입 요청 payload:', payload);
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
