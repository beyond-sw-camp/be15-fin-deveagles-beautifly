<template>
  <div>
    <div class="label-row">
      <label for="address">매장 주소</label>
      <span v-if="isRequired('address')" class="required">*</span>
    </div>
    <div class="address-row">
      <BaseForm
        :model-value="props.address"
        :error="props.errorAddress"
        placeholder="주소 검색"
        @update:model-value="val => emit('update:address', val)"
        @keydown.enter="$emit('blur')"
        @blur="$emit('validate:address')"
        @focus="emit('clearError', 'address')"
      />
      <BaseButton class="search-button" @click="openAddressSearch">주소 검색</BaseButton>
    </div>

    <BaseForm
      :model-value="props.detailAddress"
      :error="props.errorDetail"
      placeholder="상세 주소 입력"
      @update:model-value="val => emit('update:detailAddress', val)"
      @blur="$emit('validate:detailAddress')"
      @focus="emit('clearError', 'detailAddress')"
    />
  </div>
</template>

<script setup>
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    address: String,
    detailAddress: String,
    isRequired: {
      type: Function,
      default: () => {},
    },
    errorAddress: {
      type: String,
      default: '',
    },
    errorDetail: {
      type: String,
      default: '',
    },
  });

  const emit = defineEmits([
    'update:address',
    'update:detailAddress',
    'validate:address',
    'validate:detailAddress',
    'clearError',
  ]);

  const openAddressSearch = () => {
    new window.daum.Postcode({
      oncomplete: data => {
        const roadAddr = data.roadAddress || data.jibunAddress || '';
        emit('update:address', roadAddr); // ✅ 부모에게 전달!
      },
    }).open();
  };
</script>

<style scoped>
  .address-row .address-input {
    flex: 1;
  }
  .address-row .search-button {
    width: 120px;
    height: 40px;
    font-size: 14px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .address-row {
    display: flex;
    gap: 8px;
    width: 100%;
    margin-bottom: 12px;
  }
  .address-input {
    flex: 1;
  }
  .label-row {
    margin-bottom: 16px;
    font-size: 15px;
    font-weight: 600;
    color: #111;
  }
  .required {
    color: red;
    margin-left: 4px;
  }
</style>
