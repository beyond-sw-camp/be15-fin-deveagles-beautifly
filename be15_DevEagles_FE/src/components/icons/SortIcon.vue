<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 20 20"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="className"
  >
    <!-- 위/아래 화살표 모두 표시 -->
    <path d="M10 4L6 8H14L10 4Z" :fill="direction === 'asc' ? '#3fc1c9' : '#ccc'" />
    <path d="M10 16L14 12H6L10 16Z" :fill="direction === 'desc' ? '#3fc1c9' : '#ccc'" />
  </svg>
</template>

<script setup>
  defineProps({
    size: {
      type: [String, Number],
      default: 18,
    },
    direction: {
      type: String, // 'asc', 'desc', ''
      default: '',
    },
    className: {
      type: String,
      default: '',
    },
  });
</script>
