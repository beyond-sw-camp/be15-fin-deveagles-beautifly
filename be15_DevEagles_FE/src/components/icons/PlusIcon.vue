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
    <line x1="12" y1="5" x2="12" y2="19"></line>
    <line x1="5" y1="12" x2="19" y2="12"></line>
  </svg>
</template>

<script>
  export default {
    name: 'PlusIcon',
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
