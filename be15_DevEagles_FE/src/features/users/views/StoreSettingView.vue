<template>
  <div class="store-settings-container">
    <h2 class="font-section-title setting-title">매장 설정</h2>
    <div class="form-fields">
      <div class="label-row">
        <label for="storeName">상점명</label>
      </div>
      <BaseForm id="storeName" v-model="store.name" placeholder="상점명을 입력해주세요." />

      <AddressSearch v-model="store.address" />

      <div class="label-row">
        <label for="category">업종</label>
      </div>
      <BaseForm id="category" v-model="store.category" type="select" :options="categoryOptions" />

      <div class="label-row">
        <label for="phone">매장 전화번호</label>
      </div>
      <BaseForm id="phone" v-model="store.phone" placeholder="매장 전화번호를 입력해주세요." />

      <div class="label-row">
        <label for="bizNumber">사업자 등록번호</label>
      </div>
      <BaseForm
        id="bizNumber"
        v-model="store.bizNumber"
        placeholder="사업자 등록번호를 입력해주세요."
      />

      <div class="label-row">SNS 주소</div>
      <div class="sns-row">
        <BaseForm
          id="sns-type-0"
          v-model="store.snsList[0].type"
          type="select"
          :options="snsOptions"
          placeholder="선택"
          class="sns-select"
        />
        <BaseForm
          id="sns-url-0"
          v-model="store.snsList[0].url"
          placeholder="SNS 주소 입력"
          class="sns-input"
        />
        <button type="button" class="sns-action-button" @click="addSNS">+</button>
      </div>

      <div v-for="(sns, index) in store.snsList.slice(1)" :key="index + 1" class="sns-row">
        <BaseForm
          :id="`sns-type-${index + 1}`"
          v-model="store.snsList[index + 1].type"
          type="select"
          :options="snsOptions"
          placeholder="선택"
          class="sns-select"
        />
        <BaseForm
          :id="`sns-url-${index + 1}`"
          v-model="store.snsList[index + 1].url"
          placeholder="SNS 주소 입력"
          class="sns-input"
        />
        <button type="button" class="sns-action-button" @click="removeSNS(index + 1)">-</button>
      </div>

      <BaseButton class="save-button" type="primary" @click="handleEdit">
        변경사항 저장
      </BaseButton>
    </div>
  </div>
</template>
<script setup>
  import { onMounted, ref } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import AddressSearch from '@/features/users/components/AddressSearch.vue';

  const store = ref({
    name: '',
    address: {
      base: '',
      detail: '',
    },
    category: '',
    phone: '',
    bizNumber: '',
    snsList: [{ type: '', url: '' }],
  });

  onMounted(() => {
    store.value = {
      name: '이글스샵',
      address: {
        base: '서울시 강남구 ...',
        detail: 'A빌딩 2층',
      },
      category: 1,
      phone: '021111234',
      bizNumber: '0123456789',
      snsList: [{ type: '1', url: 'https://sample.com' }],
    };
  });

  const categoryOptions = [
    { value: 1, text: '미용실' },
    { value: 2, text: '네일샵' },
    { value: 3, text: '피부관리실' },
    { value: 4, text: '왁싱샵' },
  ];

  const snsOptions = [
    { value: '1', text: 'Instagram' },
    { value: '2', text: '네이버 블로그' },
    { value: '3', text: '기타' },
  ];

  const addSNS = () => {
    store.value.snsList.push({ type: '', url: '' });
  };

  const removeSNS = index => {
    if (store.value.snsList.length > 1) {
      store.value.snsList.splice(index, 1);
    }
  };

  const handleEdit = () => {
    console.log(`변경할 데이터: ${store.value}`);
    //todo 변경 api 연동
  };
</script>
<style scoped>
  .setting-title {
    text-align: center;
    margin-bottom: 24px;
  }
  .store-settings-container {
    max-width: 560px;
    margin: 40px auto;
    padding: 40px;
    border-radius: 16px;
    background-color: #fff;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  }
  .label-row {
    margin-bottom: 6px;
    font-size: 15px;
    font-weight: 600;
    color: #111;
  }
  .form-fields {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .sns-row {
    display: flex;
    align-items: stretch; /* ✅ 핵심! center가 아니라 stretch */
    gap: 8px;
  }
  .sns-select {
    width: 140px;
  }
  .sns-input {
    flex: 1;
  }
  .sns-row :deep(.input) {
    margin-bottom: 0 !important;
    height: 40px;
    line-height: 1.2; /* input 내부 텍스트 기준 맞춤 */
    padding: 0 12px;
    box-sizing: border-box;
  }

  .sns-action-button {
    width: 40px;
    height: 40px;
    padding: 0;
    margin: 0;
    border: none;
    background-color: #ddd;
    border-radius: 6px;
    font-size: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
  }
  .save-button {
    width: 100%;
    height: 48px;
    background-color: #2f466c;
    color: white;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
  }
  .save-button:hover {
    background-color: #1e3250;
  }
  .save-button:active {
    transform: scale(0.97); /* 눌린 듯한 느낌 */
  }
</style>
