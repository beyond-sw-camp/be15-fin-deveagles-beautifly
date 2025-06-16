<template>
  <h2 class="font-section-title signup-title">회원가입</h2>

  <div class="form-fields">
    <div class="label-row">
      <label for="email">
        E-mail
        <span v-if="isRequired('email')" class="required">*</span>
      </label>
    </div>
    <BaseForm
      id="email"
      v-model="form.email"
      placeholder="가입하실 E-mail 주소를 입력해주세요."
      type="email"
      :error="errors.email"
      @blur="emailChecked"
      @focus="clearError('email')"
    />
    <p v-if="emailCheckMessage" class="validation-msg">{{ emailCheckMessage }}</p>

    <div class="label-row">
      <label for="password">
        비밀번호
        <span v-if="isRequired('password')" class="required">*</span>
      </label>
      <p class="password-rule">특수문자, 영문자, 숫자를 포함한 8자리 이상</p>
    </div>
    <BaseForm
      id="password"
      v-model="form.password"
      placeholder="비밀번호를 입력해주세요."
      type="password"
      :error="errors.password"
      @focus="clearError('password')"
    />
    <BaseForm
      id="checkPwd"
      v-model="form.checkPwd"
      placeholder="비밀번호를 한 번 더 입력해주세요."
      type="password"
      :error="errors.checkPwd"
      @focus="clearError('checkPwd')"
    />

    <div class="label-row">
      <label for="userName">
        대표자명
        <span v-if="isRequired('userName')" class="required">*</span>
      </label>
    </div>
    <BaseForm
      id="userName"
      v-model="form.userName"
      placeholder="대표자명을 입력해주세요."
      type="text"
      :error="errors.userName"
      @focus="clearError('userName')"
    />

    <div class="label-row">
      <label for="phoneNumber">
        전화번호
        <span v-if="isRequired('phoneNumber')" class="required">*</span>
      </label>
    </div>
    <BaseForm
      id="phoneNumber"
      v-model="form.phoneNumber"
      placeholder="전화번호를 입력해주세요."
      type="text"
      :error="errors.phoneNumber"
      @blur="phoneChecked"
      @focus="clearError('phoneNumber')"
    />
    <p v-if="phoneCheckMessage" class="validation-msg">{{ phoneCheckMessage }}</p>
  </div>
</template>
<script setup>
  import BaseForm from '@/components/common/BaseForm.vue';
  import { computed, ref } from 'vue';

  const props = defineProps({
    modelValue: Object,
    isRequired: Function,
  });
  const emit = defineEmits(['update:modelValue']);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const errors = ref({
    email: '',
    password: '',
    checkPwd: '',
    userName: '',
    phoneNumber: '',
  });

  const emailCheckMessage = ref('');
  const phoneCheckMessage = ref('');

  const emailChecked = ref(false);
  const phoneChecked = ref(false);

  const clearError = field => {
    errors.value[field] = '';
  };

  const validate = () => {
    let valid = true;

    if (!form.value.email || !form.value.email.includes('@')) {
      errors.value.email = '올바른 이메일 형식을 입력해주세요.';
      valid = false;
    }

    const password = form.value.password;
    if (!password) {
      errors.value.password = '비밀번호를 입력해주세요.';
      valid = false;
    } else if (
      !/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\\[\]{};':"\\|,.<>/?]).{8,}$/.test(password)
    ) {
      errors.value.password = '사용할 수 없는 비밀번호입니다.';
      valid = false;
    }

    if (password !== form.value.checkPwd) {
      errors.value.checkPwd = '비밀번호가 일치하지 않습니다.';
      valid = false;
    }

    if (!form.value.userName) {
      errors.value.userName = '대표자명을 입력해주세요.';
      valid = false;
    }

    if (!form.value.phoneNumber || form.value.phoneNumber.length < 10) {
      const onlyNumberPattern = /^[0-9]+$/;
      if (!onlyNumberPattern.test(form.value.storePhone)) {
        errors.value.storePhone = '숫자만 입력해주세요.';
      } else {
        errors.value.phoneNumber = '전화번호를 정확히 입력해주세요.';
      }
      valid = false;
    }

    return valid;
  };

  defineExpose({ validate });
</script>
<style scoped>
  .signup-title {
    text-align: center;
    margin-bottom: 24px;
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
  .form-fields {
    width: 360px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .required {
    color: red;
    margin-left: 4px;
  }
  .check-text {
    font-size: 12px;
    color: var(--color-gray-500);
    cursor: pointer;
    font-weight: 500;
  }
  .validation-msg {
    font-size: 12px;
    color: var(--color-error, red);
    margin-top: -8px;
    margin-bottom: 4px;
  }
  .password-rule {
    font-size: 12px;
    font-weight: normal;
    color: var(--color-gray-500);
    margin-left: 8px;
  }
</style>
