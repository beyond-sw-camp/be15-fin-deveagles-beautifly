<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="className"
  >
    <path d="M4 6H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
    <path d="M4 12H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
    <path d="M4 18H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
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
