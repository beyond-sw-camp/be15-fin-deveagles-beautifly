<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    :class="className"
  >
    <line x1="18" y1="6" x2="6" y2="18"></line>
    <line x1="6" y1="6" x2="18" y2="18"></line>
  </svg>
</template>

<script>
  export default {
    name: 'XIcon',
    props: {
      size: {
        type: [String, Number],
        default: 24,
      },
      className: {
        type: String,
        default: '',
      },
    },
  };
</script>
