<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M15 18L9 12L15 6"
      :stroke="color"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
  </svg>
</template>

<script>
  export default {
    name: 'ArrowLeftIcon',
    props: {
      size: {
        type: [Number, String],
        default: 24,
      },
      color: {
        type: String,
        default: 'currentColor',
      },
    },
  };
</script>
