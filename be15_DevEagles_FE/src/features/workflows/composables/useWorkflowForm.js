import { ref, computed, reactive } from 'vue';
import {
  triggerCategories,
  triggerOptions,
  actionOptions,
  treatmentOptions,
  messageTemplateOptions,
  couponOptions,
} from '../constants/workflowOptions.js';

export function useWorkflowForm(initialData = null) {
  const sidebarOpen = ref(false);
  const currentSidebarType = ref('');
  const currentTriggerView = ref('category'); // 'category' | 'detail'

  // Form data with default values
  const defaultFormData = {
    title: '',
    description: '',
    isActive: true,
    targetCustomerGrades: [],
    targetTags: [],
    excludeDormantCustomers: false,
    dormantPeriodMonths: 6,
    excludeRecentMessageReceivers: false,
    recentMessagePeriodDays: 30,
    trigger: '',
    triggerCategory: '',
    triggerConfig: {
      visitCycleDays: 7,
      treatmentId: '',
      daysAfterTreatment: 7,
      daysAfterRegistration: 0,
      birthdayDaysBefore: 3,
      visitMilestone: 10,
      amountMilestone: 100000,
      daysAfterFirstVisit: 30,
      riskThresholdDays: 30,
      followupDays: 7,
    },
    action: '',
    actionConfig: {
      messageTemplateId: '',
      couponId: '',
      notificationTitle: '',
      notificationContent: '',
      sendTime: null,
    },
  };

  // Initialize form data (merge with initial data for edit mode)
  const formData = reactive({
    ...defaultFormData,
    ...initialData,
    triggerConfig: {
      ...defaultFormData.triggerConfig,
      ...initialData?.triggerConfig,
    },
    actionConfig: {
      ...defaultFormData.actionConfig,
      ...initialData?.actionConfig,
    },
  });

  // Computed properties
  const hasTargetConfig = computed(() => {
    return formData.targetCustomerGrades.length > 0 || formData.targetTags.length > 0;
  });

  const filteredTriggerOptions = computed(() => {
    if (!formData.triggerCategory) return [];
    return triggerOptions.filter(trigger => trigger.category === formData.triggerCategory);
  });

  // Step validation states
  const isBasicValid = computed(() => {
    return formData.title.trim() !== '';
  });

  const isTriggerValid = computed(() => {
    if (!formData.trigger) return false;

    if (formData.trigger === 'specific-treatment' && !formData.triggerConfig.treatmentId) {
      return false;
    }
    if (formData.trigger === 'visit-milestone' && !formData.triggerConfig.visitMilestone) {
      return false;
    }
    if (formData.trigger === 'amount-milestone' && !formData.triggerConfig.amountMilestone) {
      return false;
    }
    if (
      formData.trigger === 'first-visit-days-after' &&
      !formData.triggerConfig.daysAfterFirstVisit
    ) {
      return false;
    }

    return true;
  });

  const isActionValid = computed(() => {
    if (!formData.action) return false;
    if (formData.action === 'message-only') {
      if (!formData.actionConfig.messageTemplateId || !formData.actionConfig.sendTime) {
        return false;
      }
    }
    if (formData.action === 'coupon-message') {
      if (
        !formData.actionConfig.couponId ||
        !formData.actionConfig.messageTemplateId ||
        !formData.actionConfig.sendTime
      ) {
        return false;
      }
    }
    if (formData.action === 'system-notification') {
      if (!formData.actionConfig.notificationTitle || !formData.actionConfig.notificationContent) {
        return false;
      }
    }
    return true;
  });

  const isFormValid = computed(() => {
    return isBasicValid.value && isTriggerValid.value && isActionValid.value;
  });

  // Trigger methods
  const selectTriggerCategory = category => {
    formData.triggerCategory = category;
    formData.trigger = '';
    currentTriggerView.value = 'detail';
  };

  const goBackToCategory = () => {
    currentTriggerView.value = 'category';
    formData.trigger = '';
  };

  const getCurrentCategoryTitle = () => {
    const category = triggerCategories.find(cat => cat.value === formData.triggerCategory);
    return category ? category.title : '';
  };

  const selectTrigger = trigger => {
    formData.trigger = trigger;
  };

  const selectAction = action => {
    formData.action = action;
  };

  // Sidebar methods
  const openSidebar = type => {
    currentSidebarType.value = type;
    sidebarOpen.value = true;
    if (type === 'trigger') {
      currentTriggerView.value = 'category';
    }
  };

  const closeSidebar = () => {
    sidebarOpen.value = false;
    currentSidebarType.value = '';
    currentTriggerView.value = 'category';
  };

  const applySidebarConfig = () => {
    closeSidebar();
  };

  const getSidebarTitle = () => {
    switch (currentSidebarType.value) {
      case 'basic':
        return '워크플로우 정보';
      case 'target':
        return '타겟 고객 설정';
      case 'trigger':
        return '트리거 설정';
      case 'action':
        return '액션 설정';
      default:
        return '';
    }
  };

  // Helper methods for display
  const getTriggerIcon = trigger => {
    const option = triggerOptions.find(opt => opt.value === trigger);
    return option ? option.icon : '';
  };

  const getTriggerText = trigger => {
    const option = triggerOptions.find(opt => opt.value === trigger);
    return option ? option.title : trigger;
  };

  const getActionIcon = action => {
    const option = actionOptions.find(opt => opt.value === action);
    return option ? option.icon : '';
  };

  const getActionText = action => {
    const option = actionOptions.find(opt => opt.value === action);
    return option ? option.title : action;
  };

  const getTreatmentText = treatmentId => {
    const option = treatmentOptions.find(opt => opt.value === treatmentId);
    return option ? option.text : treatmentId;
  };

  const getMessageTemplateText = templateId => {
    const option = messageTemplateOptions.find(opt => opt.value === templateId);
    return option ? option.text : templateId;
  };

  const getCouponText = couponId => {
    const option = couponOptions.find(opt => opt.value === couponId);
    return option ? option.text : couponId;
  };

  const formatSendTime = time => {
    if (!time) return '';
    const date = new Date(time);
    return date.toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    });
  };

  // Reset form (useful for create mode)
  const resetForm = () => {
    Object.assign(formData, {
      ...defaultFormData,
      triggerConfig: { ...defaultFormData.triggerConfig },
      actionConfig: { ...defaultFormData.actionConfig },
    });
  };

  // Set form data (useful for edit mode)
  const setFormData = data => {
    Object.assign(formData, {
      ...data,
      triggerConfig: {
        ...defaultFormData.triggerConfig,
        ...data.triggerConfig,
      },
      actionConfig: {
        ...defaultFormData.actionConfig,
        ...data.actionConfig,
      },
    });
  };

  return {
    // State
    formData,
    sidebarOpen,
    currentSidebarType,
    currentTriggerView,

    // Computed
    hasTargetConfig,
    filteredTriggerOptions,
    isBasicValid,
    isTriggerValid,
    isActionValid,
    isFormValid,

    // Methods
    selectTriggerCategory,
    goBackToCategory,
    getCurrentCategoryTitle,
    selectTrigger,
    selectAction,
    openSidebar,
    closeSidebar,
    applySidebarConfig,
    getSidebarTitle,
    getTriggerIcon,
    getTriggerText,
    getActionIcon,
    getActionText,
    getTreatmentText,
    getMessageTemplateText,
    getCouponText,
    formatSendTime,
    resetForm,
    setFormData,
  };
}
