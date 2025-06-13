<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="className"
  >
    <polyline
      points="9,18 15,12 9,6"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
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
