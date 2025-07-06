<template>
  <div class="staff-create-page">
    <h2 class="page-title">직원 등록</h2>
    <p class="inform-rule">이미지를 제외한 모든 값을 입력해주세요.</p>

    <ProfileUpload v-model:file="profileFile" />

    <div class="form-wrapper">
      <BaseForm
        v-model="form.staffName"
        label="직원 이름"
        placeholder="직원 이름을 입력해주세요."
        :error="errors.staffName"
      />

      <BaseForm
        v-model="form.loginId"
        label="아이디"
        placeholder="아이디를 입력해주세요."
        :error="errors.loginId"
        @blur="idChecked"
        @focus="clearError('loginId')"
      />

      <BaseForm
        v-model="form.email"
        label="이메일 주소"
        type="email"
        placeholder="이메일 주소를 입력해주세요."
        :error="errors.email"
        @blur="emailChecked"
        @focus="clearError('email')"
      />

      <div class="form-group">
        <div class="form-label-row">
          <label for="password">비밀번호</label>
          <p class="inform-rule">특수문자, 영문자, 숫자를 포함한 8자리 이상</p>
        </div>
        <BaseForm
          v-model="form.password"
          type="password"
          placeholder="비밀번호를 입력해주세요."
          :error="errors.password"
          @blur="passwordChecked"
          @focus="clearError('password')"
        />
        <BaseForm
          v-model="form.checkPwd"
          placeholder="비밀번호 확인"
          type="password"
          :error="errors.checkPwd"
          @blur="checkPwdChecked"
          @focus="clearError('checkPwd')"
        />
      </div>
      <BaseForm
        v-model="form.phoneNumber"
        label="전화번호"
        placeholder="전화번호를 입력해주세요."
        :error="errors.phoneNumber"
        @blur="phoneChecked"
        @focus="clearError('phoneNumber')"
      />
      <BaseForm
        v-model="form.grade"
        label="직급"
        type="text"
        :error="errors.grade"
        @focus="clearError('grade')"
      />

      <BaseButton class="submit-btn" @click="handleSubmit">직원등록 ✔</BaseButton>
    </div>
  </div>

  <BaseModal v-model="showConfirmModal" title="직원 정보를 확인해주세요">
    <p class="modal-text">직원 정보를 등록하시겠습니까?</p>
    <template #footer>
      <BaseButton @click="showConfirmModal = false">취소</BaseButton>
      <BaseButton type="primary" @click="submit"> 확인 </BaseButton>
    </template>
  </BaseModal>

  <BaseToast ref="toastRef" />
</template>

<script setup>
  import { ref } from 'vue';
  import ProfileUpload from '@/features/users/components/ProfileUpload.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { validEmail, validId } from '@/features/users/api/users.js';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import { useRouter } from 'vue-router';
  import { createStaff } from '@/features/staffs/api/staffs.js';

  const router = useRouter();
  const toastRef = ref();

  const form = ref({
    profileUrl: '',
    staffName: '',
    loginId: '',
    email: '',
    password: '',
    checkPwd: '',
    phoneNumber: '',
    grade: '',
  });

  const showConfirmModal = ref(false);
  const profileFile = ref(null);

  const errors = ref({
    staffName: '',
    loginId: '',
    email: '',
    password: '',
    checkPwd: '',
    phoneNumber: '',
    grade: '',
  });

  const clearError = field => {
    errors.value[field] = '';
  };

  const idCheckPassed = ref(false);
  const emailCheckPassed = ref(false);
  const passwordPassed = ref(false);
  const checkPwdPassed = ref(false);
  const phonePassed = ref(false);

  const idChecked = async () => {
    const checkItem = form.value.loginId?.trim();
    if (!checkItem) {
      errors.value.loginId = '아이디를 입력해주세요.';
      idCheckPassed.value = false;
      return;
    }

    try {
      const res = await validId({ checkItem });
      if (res.data.data === true) {
        errors.value.loginId = '';
        idCheckPassed.value = true;
      } else {
        errors.value.loginId = '이미 사용 중인 아이디입니다.';
        idCheckPassed.value = false;
      }
    } catch (err) {
      errors.value.loginId = '중복 확인 중 오류가 발생했습니다.';
      idCheckPassed.value = false;
    }
  };

  const emailChecked = async () => {
    const checkItem = form.value.email?.trim();
    if (!checkItem) {
      errors.value.email = '이메일을 입력해주세요.';
      emailCheckPassed.value = false;
      return;
    }

    const isValidEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(checkItem);
    if (!isValidEmail) {
      errors.value.email = '올바른 이메일 형식을 입력해주세요.';
      emailCheckPassed.value = false;
      return;
    }

    try {
      const res = await validEmail({ checkItem });
      if (res.data.data === true) {
        errors.value.email = '';
        emailCheckPassed.value = true;
      } else {
        errors.value.email = '이미 사용 중인 이메일입니다.';
        emailCheckPassed.value = false;
      }
    } catch (err) {
      errors.value.email = '중복 확인 중 오류가 발생했습니다.';
      emailCheckPassed.value = false;
    }
  };

  const passwordChecked = () => {
    const password = form.value.password?.trim();
    if (!password) {
      errors.value.password = '비밀번호를 입력해주세요.';
      passwordPassed.value = false;
      return;
    }

    const isValidPwd =
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\\[\]{};':"\\|,.<>/?]).{8,}$/.test(password);
    if (!isValidPwd) {
      errors.value.password = '올바른 비밀번호 형식을 입력해주세요.';
      passwordPassed.value = true;
      return;
    }
    errors.value.password = '';
    passwordPassed.value = true;
  };

  const checkPwdChecked = () => {
    const checkPwd = form.value.checkPwd?.trim();
    const password = form.value.password?.trim();

    if (!checkPwd) {
      errors.value.checkPwd = '비밀번호를 한 번 더 입력해주세요.';
      checkPwdPassed.value = false;
      return;
    }

    if (checkPwd !== password) {
      errors.value.checkPwd = '비밀번호가 일치하지 않습니다.';
      checkPwdPassed.value = false;
      return;
    }
    errors.value.checkPwd = '';
    checkPwdPassed.value = true;
  };

  const phoneChecked = () => {
    const phoneNumber = form.value.phoneNumber?.trim();

    if (!phoneNumber) {
      errors.value.phoneNumber = '전화번호를 입력해주세요.';
      phonePassed.value = false;
      return;
    }

    const isValidPhone = /^[0-9]+$/.test(phoneNumber);

    if (!isValidPhone) {
      errors.value.phoneNumber = '올바른 전화번호 형식을 입력해주세요.';
      phonePassed.value = false;
      return;
    }

    if (phoneNumber.length === 11) {
      errors.value.phoneNumber = '';
      phonePassed.value = true;
    } else {
      errors.value.phoneNumber = '전화번호는 11자리 숫자만 입력 가능합니다.';
      phonePassed.value = false;
    }
  };

  const validate = () => {
    let valid = true;

    if (!form.value.loginId) {
      errors.value.loginId = '아이디를 입력해주세요.';
      valid = false;
    }

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

    if (!form.value.staffName) {
      errors.value.staffName = '대표자명을 입력해주세요.';
      valid = false;
    }

    if (!form.value.phoneNumber || form.value.phoneNumber.length < 10) {
      const onlyNumberPattern = /^[0-9]+$/;
      if (!onlyNumberPattern.test(form.value.phoneNumber)) {
        errors.value.phoneNumber = '숫자만 입력해주세요.';
      } else {
        errors.value.phoneNumber = '전화번호를 정확히 입력해주세요.';
      }
      valid = false;
    }

    return valid;
  };

  const handleSubmit = async () => {
    const isValid = validate();
    if (!isValid) {
      toastRef.value?.error?.('입력 정보를 다시 확인해주세요.');
      return;
    }
    showConfirmModal.value = true;
  };

  const submit = async () => {
    showConfirmModal.value = false;

    const request = {
      staffName: form.value.staffName,
      loginId: form.value.loginId,
      password: form.value.password,
      email: form.value.email,
      phoneNumber: form.value.phoneNumber,
      grade: form.value.grade,
    };

    const formData = new FormData();
    formData.append(
      'staffRequest',
      new Blob([JSON.stringify(request)], { type: 'application/json' })
    );

    if (profileFile.value !== null) {
      formData.append('profile', profileFile.value);
    }

    try {
      await createStaff(formData);
      toastRef.value?.success?.('직원이 등록되었습니다.');
      setTimeout(() => {
        router.push('/settings/staff');
      }, 1000);
    } catch (err) {
      toastRef.value?.error?.('직원 등록 중 오류가 발생했습니다.');
    }
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
  .inform-rule {
    font-size: 12px;
    color: #888;
  }
</style>
