<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="className"
  >
    <path d="M12 5V19" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
    <path d="M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
  </svg>
</template>

<script setup>
  defineProps({
    size: {
      type: [String, Number],
      default: 20,
    },
    className: {
      type: String,
      default: '',
    },
  });
</script>
