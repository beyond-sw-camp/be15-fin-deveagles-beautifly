<template>
  <BaseDrawer
    v-model="visible"
    title="고객 수정"
    position="right"
    size="md"
    :closable="true"
    :mask-closable="true"
    :z-index="zIndex"
    @after-leave="handleAfterLeave"
  >
    <form
      class="customer-create-form"
      autocomplete="off"
      novalidate
      @submit.prevent="validateAndSubmit"
    >
      <div class="form-row">
        <label class="form-label"> 이름<span class="required">*</span> </label>
        <input
          v-model="form.name"
          type="text"
          class="form-input"
          :class="{ 'input-error': errors.name }"
          placeholder="고객명"
          @blur="validateField('name')"
        />
        <div v-if="errors.name" class="error-message">{{ errors.name }}</div>
      </div>
      <div class="form-row">
        <label class="form-label"> 연락처<span class="required">*</span> </label>
        <input
          v-model="form.phone"
          type="text"
          class="form-input"
          :class="{ 'input-error': errors.phone }"
          placeholder="01000000000"
          @blur="validateField('phone')"
        />
        <div v-if="errors.phone" class="error-message">{{ errors.phone }}</div>
      </div>
      <div class="form-row">
        <label class="form-label">성별</label>
        <select v-model="form.gender" class="form-input">
          <option value="" disabled>성별 선택</option>
          <option value="남성">남성</option>
          <option value="여성">여성</option>
        </select>
      </div>
      <div class="form-row">
        <PrimeDatePicker
          v-model="form.birthdate"
          label="생일"
          :max-date="new Date(today)"
          placeholder="생일을 선택하세요"
          :disabled="false"
          :error="''"
          :base-z-index="zIndex + 1"
        />
      </div>
      <div class="form-row">
        <label class="form-label">담당자</label>
        <select v-model="form.staffId" class="form-input">
          <option :value="null" disabled>담당자 선택</option>
          <option :value="null">담당자 없음</option>
          <option v-for="staff in staffOptions" :key="staff.staffId" :value="staff.staffId">
            {{ staff.staffName }}
          </option>
        </select>
      </div>
      <div class="form-row">
        <label class="form-label">유입경로</label>
        <select v-model="form.channelId" class="form-input">
          <option :value="null" disabled>유입경로 선택</option>
          <option
            v-for="channel in acquisitionChannelOptions"
            :key="channel.channelId"
            :value="channel.channelId"
          >
            {{ channel.channelName }}
          </option>
        </select>
      </div>
      <div class="form-row">
        <label class="form-label">태그</label>
        <Multiselect
          v-model="form.tags"
          :options="tagOptions"
          mode="tags"
          :close-on-select="false"
          :searchable="true"
          :create-option="false"
          :hide-selected="false"
          label="tagName"
          value-prop="tagId"
          track-by="tagId"
          placeholder="태그 선택"
          class="multiselect-custom"
        />
      </div>
      <div class="form-row">
        <label class="form-label">메모</label>
        <textarea
          v-model="form.memo"
          class="form-input"
          rows="2"
          placeholder="메모 입력"
        ></textarea>
      </div>
      <div class="form-row">
        <label class="form-label"> 등급<span class="required">*</span> </label>
        <select
          v-model="form.customerGradeId"
          class="form-input"
          :class="{ 'input-error': errors.grade }"
          @blur="validateField('grade')"
        >
          <option :value="null" disabled>등급 선택</option>
          <option v-for="grade in gradeOptions" :key="grade.gradeId" :value="grade.gradeId">
            {{ grade.gradeName }}
          </option>
        </select>
        <div v-if="errors.grade" class="error-message">{{ errors.grade }}</div>
      </div>
      <div class="form-row form-row-checkbox">
        <label class="form-label-checkbox">
          <input v-model="form.marketingConsent" type="checkbox" class="form-checkbox" />
          <span>마케팅 정보 수신 동의</span>
        </label>
      </div>
      <div class="form-row form-row-checkbox">
        <label class="form-label-checkbox">
          <input v-model="form.notificationConsent" type="checkbox" class="form-checkbox" />
          <span>알림(예약 등) 수신 동의</span>
        </label>
      </div>
    </form>
    <template #footer>
      <div class="drawer-footer-actions">
        <BaseButton type="secondary" size="sm" outline @click="closeDrawer">취소</BaseButton>
        <BaseButton type="primary" size="sm" native-type="submit" @click="validateAndSubmit"
          >저장</BaseButton
        >
      </div>
    </template>
  </BaseDrawer>
</template>

<script setup>
  import { ref, watch, defineEmits, defineProps, onMounted } from 'vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import Multiselect from '@vueform/multiselect';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import '@vueform/multiselect/themes/default.css';
  import { useAuthStore } from '@/store/auth.js';
  import customersAPI from '../api/customers.js';
  import { useMetadataStore } from '@/store/metadata.js';
  import { storeToRefs } from 'pinia';

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    customer: { type: Object, default: null },
    zIndex: {
      type: Number,
      default: 1000,
    },
  });
  const emit = defineEmits(['update:modelValue', 'update', 'close', 'afterLeave']);

  const visible = ref(props.modelValue);
  watch(
    () => props.modelValue,
    v => {
      visible.value = v;
    }
  );
  watch(visible, v => {
    emit('update:modelValue', v);
    if (!v) emit('close');
  });

  const handleAfterLeave = () => {
    resetForm();
    emit('afterLeave');
  };

  const authStore = useAuthStore();
  const metadataStore = useMetadataStore();
  const {
    grades: gradeOptions,
    tags: tagOptions,
    staff: staffOptions,
    channels: acquisitionChannelOptions,
  } = storeToRefs(metadataStore);

  onMounted(() => {
    metadataStore.loadMetadata();
  });

  const today = new Date().toISOString().slice(0, 10);

  function getBlankForm() {
    return {
      name: '',
      phone: '',
      gender: '',
      birthdate: '',
      staffId: null,
      channelId: null,
      tags: [],
      memo: '',
      customerGradeId: null,
      marketingConsent: false,
      notificationConsent: false,
    };
  }

  function setFormFromProp() {
    if (!props.customer) {
      resetForm();
      return;
    }
    const c = props.customer;

    form.value = {
      name: c.customerName || '',
      phone: c.phoneNumber || '',
      gender: { M: '남성', F: '여성' }[c.gender] || '',
      birthdate: c.birthdate || '',
      staffId: c.staffId,
      channelId: c.acquisitionChannelId,
      tags: Array.isArray(c.tags) ? c.tags.map(t => t.tagId) : [],
      memo: c.memo || '',
      customerGradeId: c.customerGradeId,
      marketingConsent: c.marketingConsent || false,
      notificationConsent: c.notificationConsent || false,
    };
    errors.value = { name: '', phone: '', grade: '' };
  }

  const form = ref(getBlankForm());
  const errors = ref({ name: '', phone: '', grade: '' });

  watch(
    () => props.customer,
    newVal => {
      if (visible.value && newVal) {
        setFormFromProp();
      } else {
        resetForm();
      }
    },
    { immediate: true, deep: true }
  );

  function validateField(field) {
    if (field === 'name') errors.value.name = !form.value.name.trim() ? '이름을 입력해주세요' : '';
    if (field === 'phone') {
      if (!form.value.phone.trim()) errors.value.phone = '연락처를 입력해주세요';
      else if (!/^01[016789]\d{7,8}$/.test(form.value.phone))
        errors.value.phone = '올바른 형식으로 작성해주세요';
      else errors.value.phone = '';
    }
    if (field === 'grade')
      errors.value.grade = !form.value.customerGradeId ? '등급을 선택해주세요' : '';
  }

  async function validateAndSubmit() {
    validateField('name');
    validateField('phone');
    validateField('grade');

    if (Object.values(errors.value).some(e => e)) {
      return;
    }

    try {
      const shopId = authStore.shopId;
      if (!shopId) {
        throw new Error('Shop ID가 유효하지 않습니다.');
      }

      // 전화번호 하이픈 제거
      const cleanedPhone = (form.value.phone || '').replace(/-/g, '');

      const payload = {
        ...form.value,
        phone: cleanedPhone,
        gender: form.value.gender === '남성' ? 'M' : form.value.gender === '여성' ? 'F' : null,
      };

      await customersAPI.updateCustomer(props.customer.customerId, payload, shopId);

      emit('update', { ...payload, customerId: props.customer.customerId });
      visible.value = false;
      resetForm();
    } catch (error) {
      console.error('고객 정보 업데이트 실패:', error);
      alert('고객 정보 업데이트에 실패했습니다.');
    }
  }

  function closeDrawer() {
    visible.value = false;
  }

  function resetForm() {
    form.value = getBlankForm();
    errors.value = { name: '', phone: '', grade: '' };
  }
</script>

<style>
  .multiselect-custom {
    --ms-font-size: 15px;
    --ms-line-height: 1.4;
    --ms-border-color: #ddd;
    --ms-radius: 8px;
    --ms-py: 8px;
    --ms-px: 12px;
    --ms-ring-width: 0px;
    --ms-ring-color: transparent;
    --ms-tag-bg: #e6f9fa;
    --ms-tag-color: #1b7f8c;
    --ms-tag-radius: 14px;
  }
</style>
<style scoped>
  .customer-create-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
    padding: 0;
    min-height: 100%;
    box-sizing: border-box;
    flex: 1 1 auto;
  }
  .form-row {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-bottom: 20px;
  }
  .form-label {
    font-size: 14px;
    font-weight: 500;
    color: #222;
  }
  .required {
    color: #e53935;
    margin-left: 2px;
    font-size: 15px;
    font-weight: bold;
  }
  .form-input {
    height: 40px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 15px;
    padding: 0 12px;
    background: #fff;
    transition: border 0.2s;
  }
  .form-input:focus {
    outline: none;
    border-color: #364f6b;
    background: #f8fafd;
  }
  .input-error {
    border-color: #e53935 !important;
  }
  .error-message {
    color: #e53935;
    font-size: 12px;
    margin-top: 2px;
  }
  .form-input[type='date'] {
    padding-right: 0;
  }
  .drawer-footer-actions {
    display: flex;
    gap: 0.75rem;
  }
  .form-row-checkbox {
    flex-direction: row;
    align-items: center;
    gap: 8px;
  }
  .form-label-checkbox {
    display: flex;
    align-items: center;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    color: #333;
  }
  .form-checkbox {
    width: 16px;
    height: 16px;
    margin-right: 8px;
    accent-color: #364f6b;
  }
</style>
<style src="@vueform/multiselect/themes/default.css"></style>
