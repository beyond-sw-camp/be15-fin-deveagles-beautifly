<template>
  <div class="analytics-stat-card" :class="variant">
    <div class="analytics-stat-header">
      <div class="analytics-stat-icon">{{ icon }}</div>
      <div v-if="trend" class="analytics-stat-trend" :class="trendType">
        {{ trend }}
      </div>
    </div>
    <div class="analytics-stat-content">
      <div class="analytics-stat-label">{{ label }}</div>
      <div class="analytics-stat-value">{{ value }}</div>
      <div v-if="subtitle" class="analytics-stat-subtitle">{{ subtitle }}</div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'StatCard',
    props: {
      icon: {
        type: String,
        required: true,
      },
      label: {
        type: String,
        required: true,
      },
      value: {
        type: [String, Number],
        required: true,
      },
      trend: {
        type: String,
        default: null,
      },
      trendType: {
        type: String,
        default: 'neutral',
        validator: value => ['positive', 'negative', 'neutral'].includes(value),
      },
      variant: {
        type: String,
        default: 'primary',
        validator: value => ['primary', 'success', 'info', 'warning', 'danger'].includes(value),
      },
      subtitle: {
        type: String,
        default: null,
      },
    },
  };
</script>

<style scoped>
  @import '../styles/analytics.css';

  /* StatCard 추가 스타일 */
  .analytics-stat-subtitle {
    @apply text-xs text-gray-500 mt-1 transition-colors duration-200;
  }

  .dark .analytics-stat-subtitle {
    @apply text-gray-400;
  }
</style>
