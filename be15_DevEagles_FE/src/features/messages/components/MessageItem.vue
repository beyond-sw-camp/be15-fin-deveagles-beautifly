<script setup>
  import BaseButton from '@/components/common/BaseButton.vue';
  import { TrashIcon, EditIcon } from 'lucide-vue-next';

  const props = defineProps({
    message: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['delete']);
</script>

<template>
  <tr>
    <td class="text-center">{{ props.message.title }}</td>
    <td class="text-center">{{ props.message.content }}</td>
    <td class="text-center">
      {{ props.message.status === 'sent' ? '발송 완료' : '예약 문자' }}
    </td>
    <td class="text-center">{{ props.message.date }}</td>
    <td class="text-center">
      <div v-if="props.message.status === 'reserved'" class="action-buttons">
        <BaseButton type="ghost" size="sm" class="icon-button">
          <EditIcon :size="16" />
        </BaseButton>
        <BaseButton
          type="ghost"
          size="sm"
          class="icon-button"
          @click="emit('delete', props.message, $event)"
        >
          <TrashIcon :size="16" color="var(--color-error-600)" />
        </BaseButton>
      </div>
    </td>
  </tr>
</template>

<style scoped>
  .text-center {
    text-align: center;
  }
  .action-buttons {
    display: flex;
    gap: 8px;
    justify-content: center;
  }
</style>
