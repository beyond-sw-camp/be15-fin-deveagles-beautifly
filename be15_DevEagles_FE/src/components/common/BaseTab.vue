<template>
  <div class="base-tab">
    <button
      v-for="tab in tabs"
      :key="tab"
      :class="['tab-button', { active: modelValue === tab }]"
      @click="$emit('update:modelValue', tab)"
    >
      {{ tab }}
    </button>
  </div>
</template>

<script setup>
  defineProps({
    modelValue: String,
    tabs: {
      type: Array,
      required: true,
    },
  });

  defineEmits(['update:modelValue']);
</script>

<style scoped>
  .base-tab {
    display: flex;
    gap: 12px;
  }

  .tab-button {
    padding: 8px 16px;
    font-weight: 500;
    border: 1px solid #ccc;
    border-radius: 6px;
    background-color: #f9f9f9;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .tab-button:hover {
    background-color: #f0f0f0;
  }

  .tab-button.active {
    background-color: var(--color-primary-main, #3fc1c9);
    color: white;
    border-color: var(--color-primary-main, #3fc1c9);
  }
</style>
