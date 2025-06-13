<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="className"
  >
    <path
      d="M3 11L18 5V19L3 13V11Z"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
    <path
      d="M11.6 16.8C12.1 15.8 12.4 14.9 12.4 13.9C12.4 12.9 12.1 12 11.6 11"
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
