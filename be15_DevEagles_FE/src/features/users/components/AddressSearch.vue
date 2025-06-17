<template>
  <div>
    <div class="label-row">
      <label for="address">매장 주소</label>
    </div>
    <div class="address-row">
      <BaseForm
        id="address"
        v-model="localModel.base"
        class="address-input"
        placeholder="주소 검색"
        readonly
      />
      <BaseButton class="search-button" @click="openAddressSearch">주소 검색</BaseButton>
    </div>

    <BaseForm v-model="localModel.detail" placeholder="상세 주소를 입력해주세요." />
  </div>
</template>

<script setup>
  import { reactive, watch } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: {
      type: Object,
      default: () => ({ base: '', detail: '' }),
    },
  });

  const emit = defineEmits(['update:modelValue']);

  const localModel = reactive({
    base: props.modelValue.base,
    detail: props.modelValue.detail,
  });

  // 상위 데이터 바인딩
  watch(
    () => props.modelValue,
    val => {
      localModel.base = val.base;
      localModel.detail = val.detail;
    },
    { immediate: true, deep: true }
  );

  // 변동 데이터 바인딩
  watch(
    () => ({ ...localModel }),
    val => emit('update:modelValue', val),
    { deep: true }
  );

  const openAddressSearch = () => {
    new window.daum.Postcode({
      oncomplete: data => {
        localModel.base = data.roadAddress || data.jibunAddress || '';
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
</style>
