import { getCurrentInstance } from 'vue';

export function useToast() {
  const instance = getCurrentInstance();

  if (!instance) {
    throw new Error('useToast must be called within a component setup function');
  }

  const showToast = (message, type = 'success', options = {}) => {
    const toast = instance.refs.toast;
    if (toast && typeof toast[type] === 'function') {
      return toast[type](message, options);
    } else {
      console.warn(`Toast method '${type}' not found or toast ref not available`);
    }
  };

  return {
    showSuccess: (message, options) => showToast(message, 'success', options),
    showError: (message, options) => showToast(message, 'error', options),
    showWarning: (message, options) => showToast(message, 'warning', options),
    showInfo: (message, options) => showToast(message, 'info', options),
    showToast,
  };
}
