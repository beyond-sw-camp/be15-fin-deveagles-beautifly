<script setup>
  import BaseButton from '@/components/common/BaseButton.vue';
  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import EditIcon from '@/components/icons/EditIcon.vue';

  const props = defineProps({
    template: {
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
    <td class="text-center">{{ template.name }}</td>
    <td class="text-center truncate">{{ template.content }}</td>
    <td class="text-center">{{ template.createdAt }}</td>
    <td class="text-center d-flex justify-content-center gap-2">
      <BaseButton size="xs" icon aria-label="수정" @click="handleEdit">
        <EditIcon class="icon" />
      </BaseButton>
      <BaseButton size="xs" icon aria-label="삭제" @click="handleDelete">
        <TrashIcon class="icon" />
      </BaseButton>
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
  .text-center {
    text-align: center;
  }
</style>
