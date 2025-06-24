<script setup>
  import BaseButton from '@/components/common/BaseButton.vue';
  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import EditIcon from '@/components/icons/EditIcon.vue';

  const props = defineProps({
    message: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['delete', 'show-detail', 'edit']);
</script>

<template>
  <tr>
    <td>
      <div class="cell-content">{{ props.message.title }}</div>
    </td>
    <td>
      <div class="cell-content text-ellipsis" @click="$emit('show-detail', props.message)">
        {{ props.message.content }}
      </div>
    </td>
    <td>
      <div class="cell-content">{{ props.message.receiver }}</div>
    </td>
    <td>
      <div class="cell-content">
        {{ props.message.status === 'sent' ? '발송 완료' : '예약 문자' }}
      </div>
    </td>
    <td>
      <div class="cell-content">{{ props.message.date }}</div>
    </td>
    <td>
      <div class="cell-content">
        <div v-if="props.message.status === 'reserved'" class="action-buttons">
          <BaseButton
            type="ghost"
            size="sm"
            class="icon-button"
            @click="$emit('edit', props.message)"
          >
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
      </div>
    </td>
  </tr>
</template>

<style scoped>
  .cell-content {
    text-align: center;
    font-size: 14px;
    padding: 6px 4px;
    line-height: 1.4;
    white-space: nowrap;
    color: var(--color-gray-800);
  }

  /* 말줄임 + 가운데 정렬 */
  .text-ellipsis {
    max-width: 240px;
    margin: 0 auto;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: center;
    cursor: pointer;
  }

  .action-buttons {
    display: flex;
    justify-content: center;
    gap: 8px;
  }
</style>
