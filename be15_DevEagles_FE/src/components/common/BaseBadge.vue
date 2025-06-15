<template>
  <span :class="['badge', `badge-${type}`, { 'badge-pill': pill }, { 'badge-dot': dot }]">
    <span v-if="dot" class="badge-dot-indicator"></span>
    <slot>{{ text }}</slot>
  </span>
</template>

<script>
  export default {
    name: 'BaseBadge',
    props: {
      type: {
        type: String,
        default: 'neutral',
        validator: value =>
          ['neutral', 'primary', 'secondary', 'success', 'warning', 'error', 'info'].includes(
            value
          ),
      },
      text: {
        type: String,
        default: '',
      },
      pill: {
        type: Boolean,
        default: false,
      },
      dot: {
        type: Boolean,
        default: false,
      },
    },
  };
</script>

<style scoped>
  .badge-pill {
    border-radius: 9999px;
  }

  .badge-dot {
    position: relative;
    padding-left: 1rem;
  }

  .badge-dot-indicator {
    position: absolute;
    left: 0.25rem;
    top: 50%;
    transform: translateY(-50%);
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background-color: currentColor;
  }
</style>
