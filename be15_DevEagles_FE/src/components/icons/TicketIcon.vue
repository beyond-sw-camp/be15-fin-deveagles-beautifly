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
    <path
      d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v2z"
    ></path>
    <path d="M13 5v2"></path>
    <path d="M13 17v2"></path>
    <path d="M13 11v2"></path>
  </svg>
</template>

<script>
  export default {
    name: 'TicketIcon',
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
