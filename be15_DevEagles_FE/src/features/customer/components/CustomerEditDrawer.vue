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
        <select v-model="form.staff_name" class="form-input">
          <option value="" disabled>담당자 선택</option>
          <option value="담당자 없음">담당자 없음</option>
          <option v-for="staff in staffOptions" :key="staff" :value="staff">{{ staff }}</option>
        </select>
      </div>
      <div class="form-row">
        <label class="form-label">유입경로</label>
        <select v-model="form.channel_id" class="form-input">
          <option :value="null" disabled>유입경로 선택</option>
          <option
            v-for="channel in acquisitionChannelOptions"
            :key="channel.id"
            :value="channel.id"
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
          label="tag_name"
          value-prop="tag_name"
          track-by="tag_name"
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
          v-model="form.grade"
          class="form-input"
          :class="{ 'input-error': errors.grade }"
          @blur="validateField('grade')"
        >
          <option value="" disabled>등급 선택</option>
          <option v-for="grade in gradeOptions" :key="grade" :value="grade">{{ grade }}</option>
        </select>
        <div v-if="errors.grade" class="error-message">{{ errors.grade }}</div>
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
  import { ref, watch, defineEmits, defineProps, nextTick, onMounted } from 'vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import Multiselect from '@vueform/multiselect';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import '@vueform/multiselect/themes/default.css';
  import { useAuthStore } from '@/store/auth.js';
  import tagsAPI from '../api/tags.js';
  import gradesAPI from '../api/grades.js';
  import { getStaff } from '@/features/staffs/api/staffs.js';
  import channelsAPI from '../api/channels.js';

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    customer: { type: Object, default: null },
    // [기능 추가] z-index prop 추가
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
      if (v) nextTick(resetForm);
    }
  );
  watch(visible, v => {
    emit('update:modelValue', v);
    if (!v) emit('close');
  });

  const handleAfterLeave = () => {
    emit('afterLeave');
  };

  const staffOptions = ref([]);
  const lastStaffFetchTime = ref(0);
  const STAFF_CACHE_DURATION = 5 * 60 * 1000; // 5분

  async function loadStaffs() {
    const now = Date.now();
    if (staffOptions.value.length > 0 && now - lastStaffFetchTime.value < STAFF_CACHE_DURATION) {
      return staffOptions.value;
    }

    try {
      const shopId = authStore.shopId?.value || authStore.shopId || 1;
      const res = await getStaff({ shopId });
      const list = res.data?.data?.staffList || [];
      staffOptions.value = list.map(s => s.staffName || s.staff_name || '');
      lastStaffFetchTime.value = now;
      return staffOptions.value;
    } catch (e) {
      console.warn('직원 목록 로드 실패:', e);
      return [];
    }
  }

  const tagOptions = ref([]);
  const gradeOptions = ref([]);
  const acquisitionChannelOptions = ref([]);

  async function loadChannels() {
    try {
      const shopId = authStore.shopId?.value || authStore.shopId || 1;
      const channels = await channelsAPI.getChannelsByShop(shopId);
      acquisitionChannelOptions.value = channels;
    } catch (e) {
      console.warn('유입채널 목록 로드 실패:', e);
      acquisitionChannelOptions.value = [];
    }
  }

  // 메타데이터 로드 (태그 / 등급 / 직원)
  const authStore = useAuthStore();

  async function loadMeta() {
    const shopId = authStore.shopId?.value || authStore.shopId || 1;

    try {
      const [tags, grades] = await Promise.all([
        tagsAPI.getTagsByShop(shopId),
        gradesAPI.getGradesByShop(shopId),
      ]);
      tagOptions.value = tags;
      gradeOptions.value = grades.map(g => g.gradeName || g.name);
    } catch (e) {
      console.warn('메타데이터 로드 실패:', e);
    }
  }

  onMounted(async () => {
    await Promise.all([loadMeta(), loadStaffs(), loadChannels()]);
  });

  watch(visible, async v => {
    emit('update:modelValue', v);
    if (!v) emit('close');
    if (v) await loadMeta();
  });

  const today = new Date().toISOString().slice(0, 10);

  function getInitialForm() {
    if (!props.customer) {
      return {
        name: '',
        phone: '',
        gender: '',
        birthdate: '',
        staff_name: '',
        channel_id: null,
        tags: [],
        memo: '',
        grade: '기본등급',
      };
    }
    return {
      name: props.customer.customer_name || '',
      phone: (props.customer.phone_number || '').replace(/-/g, ''),
      gender: props.customer.gender || '',
      birthdate: props.customer.birthdate ? new Date(props.customer.birthdate) : null,
      staff_name: props.customer.staff_name || '',
      channel_id: props.customer.channel_id ?? null,
      tags: (props.customer.tags || []).map(tag => tag.tag_name),
      memo: props.customer.memo || '',
      grade: props.customer.customer_grade_name || '기본등급',
    };
  }
  const form = ref(getInitialForm());
  const errors = ref({ name: '', phone: '', grade: '' });

  watch(
    () => props.customer,
    newVal => {
      if (visible.value && newVal) {
        form.value = getInitialForm();
        errors.value = { name: '', phone: '', grade: '' };
      }
    },
    { deep: true }
  );

  function validateField(field) {
    if (field === 'name') errors.value.name = !form.value.name.trim() ? '이름을 입력해주세요' : '';
    if (field === 'phone') {
      if (!form.value.phone.trim()) errors.value.phone = '연락처를 입력해주세요';
      else if (!/^01[016789]\d{7,8}$/.test(form.value.phone))
        errors.value.phone = '올바른 형식으로 작성해주세요';
      else errors.value.phone = '';
    }
    if (field === 'grade') errors.value.grade = !form.value.grade ? '등급을 선택해주세요' : '';
  }
  function validateAndSubmit() {
    validateField('name');
    validateField('phone');
    validateField('grade');
    if (!errors.value.name && !errors.value.phone && !errors.value.grade) {
      const payload = {
        ...props.customer,
        customer_name: form.value.name,
        phone_number: form.value.phone.replace(/-/g, ''),
        gender: form.value.gender,
        birthdate: form.value.birthdate
          ? new Date(form.value.birthdate).toISOString().split('T')[0]
          : null,
        staff_name: form.value.staff_name,
        channelId: form.value.channel_id,
        tags: form.value.tags.map(
          tagName =>
            tagOptions.value.find(opt => opt.tag_name === tagName) || {
              tag_name: tagName,
              color_code: '#ccc',
            }
        ),
        memo: form.value.memo,
        customer_grade_name: form.value.grade,
      };
      // gender enum 변환
      if (payload.gender === '남성') payload.gender = 'M';
      else if (payload.gender === '여성') payload.gender = 'F';
      emit('update', payload);
      visible.value = false;
      resetForm();
    }
  }
  function closeDrawer() {
    visible.value = false;
    resetForm();
    emit('close');
  }
  function resetForm() {
    form.value = getInitialForm();
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
</style>
