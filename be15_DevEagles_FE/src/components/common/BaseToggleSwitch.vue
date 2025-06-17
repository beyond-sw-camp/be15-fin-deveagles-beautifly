<template>
  <div class="toggle-wrapper" @click="toggle">
    <div :class="['toggle-button', { active: isChecked }]">
      <div class="circle" />
    </div>
  </div>
</template>

<script setup>
  import { computed } from 'vue';

  const props = defineProps({
    modelValue: {
      type: [Boolean, String],
      default: false,
    },
  });

  const emit = defineEmits(['update:modelValue']);

  const isChecked = computed(() => {
    return props.modelValue === true || props.modelValue === 'true';
  });

  const toggle = () => {
    emit('update:modelValue', !isChecked.value);
  };
</script>

<style scoped>
  .toggle-wrapper {
    display: inline-flex;
    align-items: center;
    cursor: pointer;
  }

  .toggle-button {
    width: 48px;
    height: 24px;
    background-color: #ccc;
    border-radius: 9999px;
    position: relative;
    transition: background-color 0.3s ease;
  }

  .toggle-button.active {
    background-color: #3fc1c9;
  }

  .circle {
    width: 18px;
    height: 18px;
    background-color: white;
    border-radius: 50%;
    position: absolute;
    top: 3px;
    left: 3px;
    transition: left 0.3s ease;
  }

  .toggle-button.active .circle {
    left: 27px;
  }
</style>
