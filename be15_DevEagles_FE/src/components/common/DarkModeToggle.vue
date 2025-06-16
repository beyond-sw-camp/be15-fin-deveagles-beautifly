<template>
  <div class="dark-mode-toggle-wrapper">
    <div class="dark-mode-switch">
      <label class="switch">
        <input type="checkbox" :checked="isDarkMode" @change="toggleDarkMode" />
        <span class="slider">
          <span class="slider-icon">
            {{ isDarkMode ? '☀️' : '🌙' }}
          </span>
        </span>
      </label>
      <span v-if="showLabel" class="switch-label">{{ label }}</span>
    </div>
  </div>
</template>

<script>
  import { useDarkMode } from '@/composables/useDarkMode.js';

  export default {
    name: 'DarkModeToggle',
    props: {
      showLabel: {
        type: Boolean,
        default: true,
      },
      label: {
        type: String,
        default: '다크모드',
      },
      size: {
        type: String,
        default: 'normal', // 'small', 'normal', 'large'
        validator: value => ['small', 'normal', 'large'].includes(value),
      },
    },
    setup() {
      const { isDarkMode, toggleDarkMode } = useDarkMode();

      return {
        isDarkMode,
        toggleDarkMode,
      };
    },
  };
</script>

<style scoped>
  .dark-mode-toggle-wrapper {
    @apply inline-block;
  }

  .dark-mode-switch {
    @apply flex items-center gap-3;
  }

  .switch {
    @apply relative inline-block cursor-pointer;
    width: 56px;
    height: 32px;
  }

  .switch.small {
    width: 44px;
    height: 24px;
  }

  .switch.large {
    width: 68px;
    height: 40px;
  }

  .switch input {
    @apply opacity-0 w-0 h-0;
  }

  .slider {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--color-gray-300);
    border-radius: 9999px;
    transition: all 0.3s ease-in-out;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .slider:before {
    position: absolute;
    content: '';
    background-color: var(--color-neutral-white);
    border-radius: 9999px;
    transition: all 0.3s ease-in-out;
    height: 24px;
    width: 24px;
    left: 4px;
    bottom: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .switch.small .slider:before {
    height: 16px;
    width: 16px;
    left: 4px;
    bottom: 4px;
  }

  .switch.large .slider:before {
    height: 32px;
    width: 32px;
    left: 4px;
    bottom: 4px;
  }

  .switch input:checked + .slider {
    background-color: var(--color-primary-main);
  }

  .switch input:checked + .slider:before {
    transform: translateX(24px);
  }

  .switch.small input:checked + .slider:before {
    transform: translateX(20px);
  }

  .switch.large input:checked + .slider:before {
    transform: translateX(28px);
  }

  .slider-icon {
    @apply absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 transition-all duration-300 pointer-events-none;
    font-size: 14px;
  }

  .switch.small .slider-icon {
    font-size: 10px;
  }

  .switch.large .slider-icon {
    font-size: 18px;
  }

  .switch input:checked + .slider .slider-icon {
    transform: translateX(-12px) translateY(-50%);
  }

  .switch.small input:checked + .slider .slider-icon {
    transform: translateX(-10px) translateY(-50%);
  }

  .switch.large input:checked + .slider .slider-icon {
    transform: translateX(-14px) translateY(-50%);
  }

  .switch input:not(:checked) + .slider .slider-icon {
    transform: translateX(12px) translateY(-50%);
  }

  .switch.small input:not(:checked) + .slider .slider-icon {
    transform: translateX(10px) translateY(-50%);
  }

  .switch.large input:not(:checked) + .slider .slider-icon {
    transform: translateX(14px) translateY(-50%);
  }

  .switch-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-gray-700);
    transition: color 0.2s ease-in-out;
  }

  .dark .switch-label {
    color: var(--color-gray-300);
  }

  .dark .slider {
    background-color: var(--color-gray-600);
  }

  .dark .switch input:checked + .slider {
    background-color: var(--color-primary-400);
  }

  /* 호버 효과 */
  .switch:hover .slider {
    box-shadow:
      0 4px 6px -1px rgba(0, 0, 0, 0.1),
      0 2px 4px -1px rgba(0, 0, 0, 0.06);
  }

  .switch:hover .slider:before {
    box-shadow:
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -2px rgba(0, 0, 0, 0.05);
  }

  /* 포커스 효과 */
  .switch input:focus + .slider {
    box-shadow:
      0 0 0 2px var(--color-primary-main),
      0 0 0 4px rgba(54, 79, 107, 0.1);
  }
</style>
