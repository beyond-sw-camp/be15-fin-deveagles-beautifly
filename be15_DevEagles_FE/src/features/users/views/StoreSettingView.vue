<template>
  <div class="store-settings-container">
    <h2 class="font-section-title setting-title">매장 설정</h2>
    <div v-if="shop" class="form-fields">
      <div class="label-row">
        <label for="shopName">상점명</label>
      </div>
      <BaseForm id="shopName" v-model="shop.shopName" placeholder="상점명을 입력해주세요." />

      <AddressSearch :address="shop.address" :detail-address="shop.detailAddress" />

      <div class="label-row">
        <label for="industry">업종</label>
      </div>
      <BaseForm id="industry" v-model="shop.industryId" type="select" :options="industryOptions" />

      <div class="label-row">
        <label for="phoneNumber">매장 전화번호</label>
      </div>
      <BaseForm
        id="phoneNumber"
        v-model="shop.phoneNumber"
        placeholder="매장 전화번호를 입력해주세요."
      />

      <div class="label-row">
        <label for="bizNumber">사업자 등록번호</label>
      </div>
      <BaseForm
        id="bizNumber"
        v-model="shop.bizNumber"
        placeholder="사업자 등록번호를 입력해주세요."
      />

      <div class="label-row">SNS 주소</div>
      <div class="sns-row">
        <BaseForm
          id="sns-type-0"
          v-model="shop.snsList[0].type"
          type="select"
          :options="snsOptions"
          placeholder="선택"
          class="sns-select"
        />
        <BaseForm
          id="sns-url-0"
          v-model="shop.snsList[0].snsAddress"
          placeholder="SNS 주소 입력"
          class="sns-input"
        />
        <button type="button" class="sns-action-button" @click="addSNS">+</button>
      </div>

      <div v-for="(sns, index) in shop.snsList.slice(1)" :key="sns.snsId || index" class="sns-row">
        <BaseForm
          :id="`sns-type-${index + 1}`"
          v-model="shop.snsList[index + 1].type"
          type="select"
          :options="snsOptions"
          placeholder="선택"
          class="sns-select"
        />
        <BaseForm
          :id="`sns-url-${index + 1}`"
          v-model="shop.snsList[index + 1].snsAddress"
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
  <BaseToast ref="toastRef" />
</template>
<script setup>
  import { onMounted, ref } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import AddressSearch from '@/features/users/components/AddressSearch.vue';
  import { getShop } from '@/features/users/api/users.js';
  import BaseToast from '@/components/common/BaseToast.vue';

  const shop = ref({
    shopName: '',
    address: '',
    detailAddress: '',
    industryId: null,
    phoneNumber: '',
    bizNumber: '',
    snsList: [{ type: '', snsAddress: '' }],
  });
  const toastRef = ref();

  onMounted(() => {
    fetchShop();
  });

  const industryOptions = ref([]);

  const fetchShop = async () => {
    try {
      const res = await getShop();
      const data = res.data.data;

      if (!Array.isArray(data.snsList) || data.snsList.length === 0) {
        data.snsList = [{ type: '', snsAddress: '' }];
      }

      shop.value = data;

      const industryArray = shop.value.industryList;
      industryOptions.value = industryArray.map(item => ({
        value: item.industryId,
        text: item.industryName,
      }));
    } catch (err) {
      toastRef.value?.error?.(err.message || '매장 정보를 조회할 수 없습니다.');
    }
  };

  const snsOptions = [
    { value: 'INSTA', text: 'Instagram' },
    { value: 'BLOG', text: '네이버 블로그' },
    { value: 'ETC', text: '기타' },
  ];

  const addSNS = () => {
    if (!Array.isArray(shop.value.snsList)) {
      shop.value.snsList = [];
    }
    shop.value.snsList.push({ type: '', snsAddress: '' });
  };

  const removeSNS = index => {
    if (shop.value.snsList.length > 1) {
      shop.value.snsList.splice(index, 1);
    }
  };

  const handleEdit = () => {
    console.log(`변경할 데이터: ${shop.value}`);
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
  :deep(select.sns-select) {
    width: 200px !important;
    min-width: 200px;
    max-width: 200px;
    display: inline-block;
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
