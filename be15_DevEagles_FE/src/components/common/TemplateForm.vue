<template>
  <div class="template-form">
    <form @submit.prevent="handleSubmit">
      <BaseForm
        v-model="formData.name"
        label="템플릿명*"
        type="text"
        placeholder="템플릿명을 입력하세요"
        :error="errors.name"
        required
      />

      <BaseForm
        v-model="formData.type"
        label="템플릿 유형*"
        type="select"
        placeholder="템플릿 유형을 선택하세요"
        :options="typeOptions"
        :error="errors.type"
        required
      />

      <BaseForm
        v-model="formData.content"
        label="메시지 내용*"
        type="textarea"
        placeholder="메시지 내용을 입력하세요"
        :error="errors.content"
        required
        rows="4"
      />

      <div class="form-actions">
        <BaseButton type="secondary" outline @click="handleCancel"> 취소 </BaseButton>
        <BaseButton type="primary" html-type="submit"> 생성 </BaseButton>
      </div>
    </form>
  </div>
</template>

<script>
  import { ref } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  export default {
    name: 'TemplateForm',
    components: {
      BaseForm,
      BaseButton,
    },
    emits: ['save', 'cancel'],
    setup(props, { emit }) {
      const formData = ref({
        name: '',
        type: '',
        content: '',
      });

      const errors = ref({});

      const typeOptions = [
        { value: 'welcome', text: '환영 메시지' },
        { value: 'revisit', text: '재방문 유도 메시지' },
        { value: 'coupon', text: '쿠폰 발송 메시지' },
        { value: 'birthday', text: '생일 축하 메시지' },
        { value: 'follow-up', text: '사후관리 메시지' },
      ];

      const validateForm = () => {
        errors.value = {};

        if (!formData.value.name.trim()) {
          errors.value.name = '템플릿명은 필수입니다.';
        }

        if (!formData.value.type) {
          errors.value.type = '템플릿 유형 선택은 필수입니다.';
        }

        if (!formData.value.content.trim()) {
          errors.value.content = '메시지 내용은 필수입니다.';
        }

        return Object.keys(errors.value).length === 0;
      };

      const handleSubmit = () => {
        if (validateForm()) {
          const templateData = {
            value: `custom-${Date.now()}`,
            text: formData.value.name,
            type: formData.value.type,
            content: formData.value.content,
            createdAt: new Date().toISOString(),
          };
          emit('save', templateData);
        }
      };

      const handleCancel = () => {
        emit('cancel');
      };

      return {
        formData,
        errors,
        typeOptions,
        handleSubmit,
        handleCancel,
      };
    },
  };
</script>

<style scoped>
  .template-form {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .form-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    margin-top: 1rem;
  }
</style>
