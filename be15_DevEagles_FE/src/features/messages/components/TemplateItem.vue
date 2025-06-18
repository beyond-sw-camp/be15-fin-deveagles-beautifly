<script setup>
  import BaseButton from '@/components/common/BaseButton.vue';
  import { TrashIcon } from 'lucide-vue-next';

  const props = defineProps({
    template: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['delete']);
</script>

<template>
  <tr>
    <td class="text-center">{{ props.template.name }}</td>
    <td class="text-center truncate">{{ props.template.content }}</td>
    <td class="text-center">{{ props.template.createdAt }}</td>
    <td class="text-center">
      <BaseButton size="xs" icon @click="emit('delete', props.template)">
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
