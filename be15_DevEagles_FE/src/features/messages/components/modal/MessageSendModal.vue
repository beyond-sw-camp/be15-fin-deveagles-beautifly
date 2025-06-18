<script setup>
  import { ref } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import TemplateSelectDrawer from '@/features/messages/components/TemplateSelectDrawer.vue';
  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
  });
  const emit = defineEmits(['update:modelValue', 'request-send', 'request-reserve']);

  const messageContent = ref('');
  const showDrawer = ref(false);

  // 이후 axios 연결 시 대체
  const templateList = ref([
    { id: 1, name: '예약 안내', content: '고객님 예약이 확정되었습니다.' },
    { id: 2, name: '시술 전 안내', content: '시술 전 주의사항을 확인해주세요.' },
  ]);

  function close() {
    emit('update:modelValue', false);
    messageContent.value = '';
  }

  function confirmSend() {
    if (!messageContent.value.trim()) return;
    emit('request-send', messageContent.value);
    close();
  }

  function reserveSend() {
    if (!messageContent.value.trim()) return;
    emit('request-reserve', messageContent.value);
    close();
  }

  function onTemplateSelected(template) {
    messageContent.value = template.content;
    showDrawer.value = false;
  }
</script>

<template>
  <BaseModal
    :model-value="props.modelValue"
    title="새 메시지"
    @update:model-value="val => emit('update:modelValue', val)"
  >
    <div class="form-group">
      <div class="template-row">
        <BaseButton type="ghost" size="sm" @click="showDrawer = true"> 템플릿 가져오기 </BaseButton>
      </div>

      <label class="form-label mt-4">메시지 내용</label>
      <textarea
        v-model="messageContent"
        class="input input--md"
        rows="5"
        placeholder="메시지 내용을 입력하세요"
      />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <div class="left-buttons">
          <BaseButton type="primary" @click="confirmSend">보내기</BaseButton>
          <BaseButton type="secondary" @click="reserveSend">예약 보내기</BaseButton>
        </div>
        <BaseButton type="ghost" @click="close">취소</BaseButton>
      </div>
    </template>

    <TemplateSelectDrawer
      v-model="showDrawer"
      :templates="templateList"
      @select="onTemplateSelected"
    />
  </BaseModal>
</template>

<style scoped>
  .footer-buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  .left-buttons {
    display: flex;
    gap: 8px;
  }
  .template-row {
    display: flex;
    justify-content: flex-start;
  }
</style>
