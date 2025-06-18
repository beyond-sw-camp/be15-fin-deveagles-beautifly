<script setup>
  import BaseButton from '@/components/common/BaseButton.vue';
  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import EditIcon from '@/components/icons/EditIcon.vue';

  const props = defineProps({
    template: {
      type: Object,
      required: true,
    },
    columnWidths: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['edit', 'delete']);

  function handleEdit() {
    emit('edit', props.template);
  }

  function handleDelete() {
    emit('delete', props.template);
  }
</script>

<template>
  <tr>
    <td class="text-center" :style="{ width: columnWidths.name }">{{ template.name }}</td>
    <td class="text-center truncate" :style="{ width: columnWidths.content }">
      {{ template.content }}
    </td>
    <td class="text-center" :style="{ width: columnWidths.createdAt }">{{ template.createdAt }}</td>
    <td class="text-center" :style="{ width: columnWidths.actions }">
      <div class="action-cell">
        <BaseButton size="xs" icon aria-label="수정" @click="handleEdit">
          <EditIcon class="icon" />
        </BaseButton>
        <BaseButton size="xs" icon aria-label="삭제" @click="handleDelete">
          <TrashIcon class="icon" />
        </BaseButton>
      </div>
    </td>
  </tr>
</template>

<style scoped>
  .truncate {
    max-width: 400px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .action-cell {
    display: flex;
    justify-content: center;
    gap: 0.5rem;
  }
</style>
