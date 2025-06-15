<template>
  <div v-if="show" :class="['alert', `alert-${type}`, { 'alert-dismissible': dismissible }]">
    <!-- Alert Icon -->
    <div v-if="showIcon" class="alert-icon">
      <component :is="iconComponent" :size="20" />
    </div>

    <!-- Alert Content -->
    <div class="alert-content">
      <div v-if="title" class="alert-title">{{ title }}</div>
      <div class="alert-message">
        <slot>{{ message }}</slot>
      </div>
    </div>

    <!-- Dismiss Button -->
    <button v-if="dismissible" class="close" aria-label="닫기" @click="handleDismiss">
      &times;
    </button>
  </div>
</template>

<script>
  export default {
    name: 'BaseAlert',
    props: {
      type: {
        type: String,
        default: 'info',
        validator: value => ['info', 'success', 'warning', 'error'].includes(value),
      },
      title: {
        type: String,
        default: '',
      },
      message: {
        type: String,
        default: '',
      },
      dismissible: {
        type: Boolean,
        default: false,
      },
      showIcon: {
        type: Boolean,
        default: true,
      },
      show: {
        type: Boolean,
        default: true,
      },
    },
    emits: ['dismiss'],
    computed: {
      iconComponent() {
        const iconMap = {
          info: 'InfoIcon',
          success: 'CheckCircleIcon',
          warning: 'AlertTriangleIcon',
          error: 'XCircleIcon',
        };
        return iconMap[this.type] || 'InfoIcon';
      },
    },
    methods: {
      handleDismiss() {
        this.$emit('dismiss');
      },
    },
  };
</script>

<style scoped>
  .alert {
    display: flex;
    align-items: flex-start;
  }

  .alert-icon {
    flex-shrink: 0;
    margin-right: 0.75rem;
  }

  .alert-content {
    flex: 1;
  }

  .alert-title {
    font-weight: 600;
    margin-bottom: 0.25rem;
  }

  .alert-message {
    line-height: 1.5;
  }
</style>
