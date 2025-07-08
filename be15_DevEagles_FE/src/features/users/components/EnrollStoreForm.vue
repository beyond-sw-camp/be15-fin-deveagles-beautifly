<template>
  <h2 class="font-section-title signup-title">매장 정보 등록</h2>

  <div class="form-fields">
    <div class="label-row">
      <label for="shopName">
        상점명
        <span v-if="isRequired('shopName')" class="required">*</span>
      </label>
    </div>
    <BaseForm
      id="shopName"
      v-model="form.shopName"
      placeholder="상점명을 입력해주세요."
      :error="errors.shopName"
      @blur="nameChecked"
      @focus="clearError('shopName')"
    />

    <AddressSearch
      :address="form.address"
      :detail-address="form.detailAddress"
      :is-required="props.isRequired"
      :error-address="errors.address"
      :error-detail="errors.detailAddress"
      @update:address="val => (form.address = val)"
      @update:detail-address="val => (form.detailAddress = val)"
      @validate:address="addressChecked"
      @validate:detail-address="detailAddressChecked"
      @clear-error="clearError"
    />

    <div class="label-row">
      <label for="industryId">
        업종
        <span v-if="isRequired('industryId')" class="required">*</span>
      </label>
    </div>
    <BaseForm
      v-model="form.industryId"
      type="select"
      :options="businessTypeOptions"
      placeholder="업종을 선택해주세요."
      :error="errors.industryId"
      @blur="industryChecked"
      @focus="clearError('industryId')"
    />

    <div class="label-row">
      <label for="phoneNumber"> 매장 전화번호 </label>
      <p class="inform-rule">'-' 제외</p>
    </div>
    <BaseForm
      v-model="form.phoneNumber"
      placeholder="매장 전화번호를 입력해주세요."
      :error="errors.phoneNumber"
      @blur="phoneNumberChecked"
      @focus="clearError('phoneNumber')"
    />

    <div class="label-row">
      <label for="businessNumber"> 사업자 등록번호 </label>
    </div>
    <BaseForm
      v-model="form.businessNumber"
      placeholder="사업자 등록번호를 입력해주세요."
      :error="errors.businessNumber"
      @blur="bizNumberChecked"
      @focus="clearError('businessNumber')"
    />
  </div>
</template>

<script setup>
  import BaseForm from '@/components/common/BaseForm.vue';
  import { computed, onMounted, ref } from 'vue';
  import AddressSearch from '@/features/users/components/AddressSearch.vue';
  import { getIndustry, validBizNumber } from '@/features/users/api/users.js';

  const businessTypeOptions = ref([]);

  onMounted(async () => {
    try {
      const res = await getIndustry();
      const industryArray = res.data.data.industryList;

      businessTypeOptions.value = industryArray.map(item => ({
        value: item.industryId,
        text: item.industryName,
      }));
    } catch (e) {
      console.error('업종 불러오기 실패:', e);
    }
  });

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
    shopName: '',
    industryId: '',
    phoneNumber: '',
    businessNumber: '',
    address: '',
    detailAddress: '',
  });

  const clearError = field => {
    errors.value[field] = '';
  };

  const nameCheckPassed = ref(false);
  const industryCheckPassed = ref(false);
  const phoneCheckPassed = ref(false);
  const bizCheckPassed = ref(false);
  const addressCheckPassed = ref(false);
  const detailAddressCheckPassed = ref(false);

  const nameChecked = () => {
    const name = form.value.shopName?.trim();
    if (!name) {
      errors.value.shopName = '상점명을 입력해주세요.';
      nameCheckPassed.value = false;
      return;
    } else {
      errors.value.shopName = '';
      nameCheckPassed.value = true;
    }
  };

  const addressChecked = () => {
    const address = form.value.address?.trim();
    if (!address) {
      errors.value.address = '매장 주소를 입력해주세요.';
      addressCheckPassed.value = false;
    } else {
      errors.value.address = '';
      addressCheckPassed.value = true;
    }
  };

  const detailAddressChecked = () => {
    const detail = form.value.detailAddress?.trim();
    if (!detail) {
      errors.value.detailAddress = '매장 상세주소를 입력해주세요.';
      detailAddressCheckPassed.value = false;
    } else {
      errors.value.detailAddress = '';
      detailAddressCheckPassed.value = true;
    }
  };

  const industryChecked = () => {
    const industry = form.value.industryId;
    if (!industry || isNaN(industry)) {
      errors.value.industryId = '업종을 선택해주세요.';
      industryCheckPassed.value = false;
    } else {
      errors.value.industryId = '';
      industryCheckPassed.value = true;
    }
  };

  const phoneNumberChecked = () => {
    const phone = form.value.phoneNumber?.trim();
    if (phone) {
      if (!/^[0-9]+$/.test(phone)) {
        errors.value.phoneNumber = '숫자만 입력해주세요.';
        phoneCheckPassed.value = false;
      } else if (form.value.phoneNumber.length > 12) {
        errors.value.phoneNumber = '최대 12자리까지 입력 가능합니다.';
        phoneCheckPassed.value = false;
      } else {
        errors.value.phoneNumber = '';
        phoneCheckPassed.value = true;
      }
    }
  };

  const bizNumberChecked = async () => {
    const bizNum = form.value.businessNumber?.trim();
    if (bizNum) {
      if (!/^[0-9]{10}$/.test(bizNum)) {
        errors.value.businessNumber = '숫자 10자리로 입력해주세요.';
        bizCheckPassed.value = false;
        return;
      }
      try {
        const res = await validBizNumber({ bizNum });
        if (res.data.data === true) {
          bizCheckPassed.value = true;
        } else {
          errors.value.businessNumber = '이미 사용 중인 등록번호입니다.';
          bizCheckPassed.value = false;
        }
      } catch (err) {
        errors.value.businessNumber = '중복 확인 중 오류가 발생했습니다.';
        bizCheckPassed.value = false;
      }
    }
  };

  const validate = () => {
    let valid = true;

    if (!form.value.shopName) {
      errors.value.shopName = '상점명을 입력해주세요.';
      valid = false;
    }

    if (!form.value.industryId) {
      errors.value.industryId = '업종을 선택해주세요.';
      valid = false;
    }

    if (form.value.phoneNumber) {
      const onlyNumberPattern = /^[0-9]+$/;
      if (!onlyNumberPattern.test(form.value.phoneNumber)) {
        errors.value.phoneNumber = '숫자만 입력해주세요.';
        valid = false;
      } else if (form.value.phoneNumber.length > 12) {
        errors.value.phoneNumber = '최대 12자리까지 입력 가능합니다.';
        valid = false;
      } else {
        errors.value.phoneNumber = ''; // 오류 없을 경우 초기화
      }
    }

    if (!form.value.address) {
      errors.value.address = '매장 주소를 입력해주세요.';
      valid = false;
    }

    if (!form.value.detailAddress) {
      errors.value.detailAddress = '매장 상세주소를 입력해주세요.';
      valid = false;
    }

    if (form.value.businessNumber && !/^[0-9]{10}$/.test(form.value.businessNumber)) {
      errors.value.businessNumber = '숫자 10자리로 입력해주세요.';
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
  .validation-msg {
    font-size: 12px;
    color: var(--color-error, red);
    margin-top: -8px;
    margin-bottom: 4px;
  }
  .inform-rule {
    font-size: 12px;
    font-weight: normal;
    color: var(--color-gray-500);
    margin-left: 8px;
  }
</style>
