<template>
  <div class="store-settings-container">
    <h2 class="font-section-title setting-title">매장 설정</h2>
    <div v-if="shop" class="form-fields">
      <div class="label-row">
        <label for="shopName">상점명</label>
      </div>
      <BaseForm
        id="shopName"
        v-model="shop.shopName"
        placeholder="상점명을 입력해주세요."
        :error="errors.shopName"
        @focus="clearError('shopName')"
      />

      <AddressSearch
        v-model:address="shop.address"
        v-model:detail-address="shop.detailAddress"
        :error-address="errors.address"
        :error-detail="errors.detailAddress"
        @clear-error="clearError"
      />

      <div class="label-row">
        <label for="industry">업종</label>
      </div>
      <BaseForm
        id="industry"
        v-model="shop.industryId"
        type="select"
        :options="industryOptions"
        :error="errors.industryId"
        @focus="clearError('industryId')"
      />

      <div class="label-row">
        <label for="phoneNumber">매장 전화번호</label>
      </div>
      <BaseForm
        id="phoneNumber"
        v-model="shop.phoneNumber"
        placeholder="매장 전화번호를 입력해주세요."
        :error="errors.phoneNumber"
        @focus="clearError('phoneNumber')"
      />

      <div class="label-row">
        <label for="bizNumber">사업자 등록번호</label>
      </div>
      <BaseForm
        id="bizNumber"
        v-model="shop.bizNumber"
        placeholder="사업자 등록번호를 입력해주세요."
        :error="errors.businessNumber"
        @focus="clearError('businessNumber')"
      />

      <div class="label-row">
        <label for="description">매장 설명</label>
      </div>
      <BaseForm v-model="shop.description" type="textarea" :rows="4" />

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

  <BaseModal v-model="showConfirmModal" title="매장 정보 수정">
    <p class="modal-text">매장 정보를 수정하시겠습니까?</p>
    <template #footer>
      <BaseButton @click="showConfirmModal = false">취소</BaseButton>
      <BaseButton type="primary" @click="submit"> 확인 </BaseButton>
    </template>
  </BaseModal>

  <BaseToast ref="toastRef" />
</template>
<script setup>
  import { onMounted, ref } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import AddressSearch from '@/features/users/components/AddressSearch.vue';
  import { getShop, putShop } from '@/features/users/api/users.js';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseModal from '@/components/common/BaseModal.vue';

  const shop = ref({
    shopName: '',
    address: '',
    detailAddress: '',
    industryId: null,
    phoneNumber: '',
    bizNumber: '',
    description: '',
    snsList: [{ snsId: null, type: '', snsAddress: '' }],
  });

  const toastRef = ref();
  const showConfirmModal = ref(false);

  onMounted(() => {
    fetchShop();
  });

  const industryOptions = ref([]);

  const fetchShop = async () => {
    try {
      const res = await getShop();
      const data = res.data.data;

      if (!Array.isArray(data.snsList) || data.snsList.length === 0) {
        data.snsList = [{ snsId: null, type: '', snsAddress: '' }];
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
    shop.value.snsList.push({ snsId: null, type: '', snsAddress: '' });
  };

  const deletedSnsIds = ref([]);

  const removeSNS = index => {
    if (shop.value.snsList.length > 1) {
      const sns = shop.value.snsList[index];
      if (sns.snsId) {
        deletedSnsIds.value.push(sns.snsId);
      }
      shop.value.snsList.splice(index, 1);
    }
  };

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

  const validate = () => {
    let valid = true;

    if (!shop.value.shopName) {
      errors.value.shopName = '상점명을 입력해주세요.';
      valid = false;
    }

    if (!shop.value.industryId) {
      errors.value.industryId = '업종을 선택해주세요.';
      valid = false;
    }

    if (shop.value.phoneNumber) {
      const onlyNumberPattern = /^[0-9]+$/;
      if (!onlyNumberPattern.test(shop.value.phoneNumber)) {
        errors.value.phoneNumber = '숫자만 입력해주세요.';
        valid = false;
      } else if (shop.value.phoneNumber.length > 12) {
        errors.value.phoneNumber = '최대 12자리까지 입력 가능합니다.';
        valid = false;
      } else {
        errors.value.phoneNumber = ''; // 오류 없을 경우 초기화
      }
    }

    if (!shop.value.address) {
      errors.value.address = '매장 주소를 입력해주세요.';
      valid = false;
    }

    if (!shop.value.detailAddress) {
      errors.value.detailAddress = '매장 상세주소를 입력해주세요.';
      valid = false;
    }

    if (shop.value.bizNumber && !/^[0-9]{10}$/.test(shop.value.bizNumber)) {
      errors.value.businessNumber = '숫자 10자리로 입력해주세요.';
      valid = false;
    }
    return valid;
  };

  const handleEdit = async () => {
    const isValid = validate();
    if (!isValid) {
      toastRef.value?.error?.('매장 정보를 다시 확인해주세요.');
      return;
    }
    showConfirmModal.value = true;
  };

  const submit = async () => {
    showConfirmModal.value = false;

    try {
      const validSnsList = shop.value.snsList
        .filter(sns => sns.type && sns.snsAddress)
        .map(sns => ({
          snsId: sns.snsId ?? null,
          type: sns.type,
          snsAddress: sns.snsAddress,
        }));

      const payload = {
        ...shop.value,
        snsList: validSnsList.length > 0 ? validSnsList : [],
        deletedSnsIds: [...deletedSnsIds.value],
      };

      await putShop(payload);
      toastRef.value?.success?.('매장 정보가 저장되었습니다.');
      await fetchShop();
    } catch (err) {
      toastRef.value?.error?.('매장 정보 수정에 실패했습니다.');
    }
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
    align-items: stretch;
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
