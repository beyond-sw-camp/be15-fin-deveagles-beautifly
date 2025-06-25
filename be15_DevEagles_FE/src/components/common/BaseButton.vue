<template>
  <button
    :type="htmlType"
    :class="[
      'btn',
      `btn-${type}`,
      { 'btn-outline': outline },
      { 'btn-xs': size === 'xs' },
      { 'btn-sm': size === 'sm' },
      { 'btn-lg': size === 'lg' },
      { 'btn-icon': variant === 'icon' },
      { 'btn-icon-sm': variant === 'icon' && size === 'sm' },
      { 'btn-icon-lg': variant === 'icon' && size === 'lg' },
    ]"
    :disabled="disabled"
    @click="$emit('click', $event)"
  >
    <slot></slot>
  </button>
</template>

<script>
  export default {
    name: 'BaseButton',
    props: {
      type: {
        type: String,
        default: 'primary',
        validator: value =>
          [
            'primary',
            'secondary',
            'success',
            'error',
            'warning',
            'info',
            'ghost',
            'cancel',
          ].includes(value),
      },
      outline: {
        type: Boolean,
        default: false,
      },
      size: {
        type: String,
        default: '',
        validator: value => ['', 'xs', 'sm', 'lg'].includes(value),
      },
      variant: {
        type: String,
        default: 'default',
        validator: value => ['default', 'icon'].includes(value),
      },
      disabled: {
        type: Boolean,
        default: false,
      },
      htmlType: {
        type: String,
        default: 'button',
        validator: value => ['button', 'submit', 'reset'].includes(value),
      },
    },
    emits: ['click'],
  };
</script>

<style scoped>
  /* 기본 버튼 스타일 */
  .btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.375rem;
    padding: 0.625rem 1.25rem;
    border-radius: 0.5rem;
    font-size: 14px;
    font-weight: 700;
    line-height: 16px;
    font-family: 'Noto Sans KR', sans-serif;
    text-decoration: none;
    cursor: pointer;
    transition:
      background 160ms ease,
      box-shadow 160ms ease,
      color 160ms ease;
    border: 1px solid transparent;
  }

  /* Primary */
  .btn-primary {
    background: var(--color-primary-main);
    color: var(--color-neutral-white);
  }
  .btn-primary:hover {
    background: var(--color-primary-400);
  }
  .btn-primary:active {
    background: var(--color-primary-500);
  }
  .btn-primary:disabled {
    background: var(--color-gray-200);
    color: var(--color-gray-500);
    cursor: not-allowed;
  }

  /* Secondary */
  .btn-secondary {
    background: var(--color-secondary-main);
    color: var(--color-neutral-white);
  }
  .btn-secondary:hover {
    background: var(--color-secondary-400);
  }
  .btn-secondary:active {
    background: var(--color-secondary-500);
  }

  /* Ghost */
  .btn-ghost {
    background: transparent;
    border-color: var(--color-gray-300);
    color: var(--color-gray-600);
  }
  .btn-ghost:hover {
    background: var(--color-gray-50);
    border-color: var(--color-gray-400);
    color: var(--color-gray-700);
  }

  /* Cancel */
  .btn-cancel {
    background: var(--color-neutral-white);
    border-color: var(--color-gray-300);
    color: var(--color-gray-700);
  }
  .btn-cancel:hover {
    background: var(--color-gray-50);
    border-color: var(--color-gray-400);
    color: var(--color-gray-800);
  }

  /* Semantic buttons */
  .btn-success {
    background: var(--color-success-300);
    color: var(--color-neutral-white);
  }
  .btn-success:hover {
    background: var(--color-success-400);
  }
  .btn-error {
    background: var(--color-error-300);
    color: var(--color-neutral-white);
  }
  .btn-error:hover {
    background: var(--color-error-400);
  }
  .btn-warning {
    background: var(--color-warning-300);
    color: var(--color-neutral-dark);
  }
  .btn-warning:hover {
    background: var(--color-warning-400);
    color: var(--color-neutral-white);
  }
  .btn-info {
    background: var(--color-info-300);
    color: var(--color-neutral-dark);
  }
  .btn-info:hover {
    background: var(--color-info-400);
  }

  /* Button Sizes */
  .btn-xs {
    padding: 0.25rem 0.5rem;
    font-size: 11px;
    line-height: 14.3px;
  }
  .btn-sm {
    padding: 0.375rem 0.75rem;
    font-size: 12px;
    line-height: 15.6px;
  }
  .btn-lg {
    padding: 0.75rem 1.5rem;
    font-size: 16px;
    line-height: 20.8px;
  }

  /* 아이콘 버튼 스타일 */
  .btn-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 8px !important;
    min-width: 32px;
    border-radius: 6px;
    transition: all 0.2s ease;
    background: none;
    border: none;
    cursor: pointer;
    color: inherit;
  }

  .btn-icon:hover {
    background: var(--color-gray-100);
  }

  .btn-icon:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .btn-icon:disabled:hover {
    background: none;
  }

  /* 아이콘 버튼 크기 */
  .btn-icon-sm {
    padding: 4px !important;
    min-width: 24px;
  }

  .btn-icon-lg {
    padding: 12px !important;
    min-width: 40px;
  }

  /* 아이콘 버튼 타입별 스타일 */
  .btn-icon.btn-primary:hover {
    background: var(--color-primary-50);
    color: var(--color-primary-main);
  }

  .btn-icon.btn-secondary:hover {
    background: var(--color-secondary-50);
    color: var(--color-secondary-main);
  }

  .btn-icon.btn-error:hover {
    background: var(--color-error-50);
    color: var(--color-error-300);
  }

  .btn-icon.btn-success:hover {
    background: var(--color-success-50);
    color: var(--color-success-500);
  }

  .btn-icon.btn-warning:hover {
    background: var(--color-warning-50);
    color: var(--color-warning-300);
  }

  .btn-icon.btn-cancel:hover {
    background: var(--color-gray-100);
    color: var(--color-gray-700);
  }
</style>
