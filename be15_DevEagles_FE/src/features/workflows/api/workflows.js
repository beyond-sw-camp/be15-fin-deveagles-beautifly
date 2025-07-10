import api from '@/plugins/axios';
import { useAuthStore } from '@/store/auth.js';

// 워크플로우 생성
export const createWorkflow = async formData => {
  const {
    title,
    description,
    isActive,
    targetCustomerGrades,
    targetTags,
    excludeDormantCustomers,
    dormantPeriodMonths,
    excludeRecentMessageReceivers,
    recentMessagePeriodDays,
    trigger,
    triggerCategory,
    triggerConfig,
    action,
    actionConfig,
    shopId,
    staffId,
  } = formData;

  const authStore = useAuthStore();
  const payload = {
    title,
    description,
    shopId: shopId ?? authStore.shopId,
    staffId: staffId ?? authStore.userId,
    targetCustomerGrades: JSON.stringify(targetCustomerGrades ?? []),
    targetTags: JSON.stringify(targetTags ?? []),
    excludeDormantCustomers,
    dormantPeriodMonths,
    excludeRecentMessageReceivers,
    recentMessagePeriodDays,
    triggerType: trigger,
    triggerCategory,
    triggerConfig: JSON.stringify(triggerConfig ?? {}),
    actionType: action,
    actionConfig: JSON.stringify(actionConfig ?? {}),
    isActive,
  };

  const res = await api.post('/workflows', payload);
  return res.data?.data;
};

// 워크플로우 수정
export const updateWorkflow = async (workflowId, formData) => {
  const {
    title,
    description,
    isActive,
    targetCustomerGrades,
    targetTags,
    excludeDormantCustomers,
    dormantPeriodMonths,
    excludeRecentMessageReceivers,
    recentMessagePeriodDays,
    trigger,
    triggerCategory,
    triggerConfig,
    action,
    actionConfig,
    shopId,
    staffId,
  } = formData;

  const authStore = useAuthStore();
  const payload = {
    workflowId,
    title,
    description,
    shopId: shopId ?? authStore.shopId,
    staffId: staffId ?? authStore.userId,
    targetCustomerGrades: JSON.stringify(targetCustomerGrades ?? []),
    targetTags: JSON.stringify(targetTags ?? []),
    excludeDormantCustomers,
    dormantPeriodMonths,
    excludeRecentMessageReceivers,
    recentMessagePeriodDays,
    triggerType: trigger,
    triggerCategory,
    triggerConfig: JSON.stringify(triggerConfig ?? {}),
    actionType: action,
    actionConfig: JSON.stringify(actionConfig ?? {}),
    isActive,
  };

  const res = await api.put(`/workflows/${workflowId}`, payload);
  return res.data?.data;
};

// 워크플로우 삭제
export const deleteWorkflow = async (workflowId, shopId, staffId) => {
  // Delete API 설계에 따라 body 필요할 수 있음. 현재 컨트롤러는 @RequestBody DeleteWorkflowCommand 필요
  const authStore = useAuthStore();
  const payload = {
    workflowId,
    shopId: shopId ?? authStore.shopId,
    staffId: staffId ?? authStore.userId,
  };
  const res = await api.delete(`/workflows/${workflowId}`, { data: payload });
  return res.data?.data;
};

// 워크플로우 상태 토글
export const toggleWorkflowStatus = async workflowId => {
  const res = await api.patch(`/workflows/${workflowId}/toggle`);
  return res.data?.data;
};

// 워크플로우 상세 조회
export const getWorkflow = async (workflowId, shopId) => {
  const authStore = useAuthStore();
  const res = await api.get(`/workflows/${workflowId}`, {
    params: { shopId: shopId ?? authStore.shopId },
  });
  return res.data?.data;
};

// 워크플로우 검색 (리스트)
export const searchWorkflows = async params => {
  // params: { shopId, searchQuery, statusFilter, triggerCategory, triggerType, actionType, isActive, page, size, sortBy, sortDirection }
  const authStore = useAuthStore();
  const merged = { shopId: authStore.shopId, page: 0, size: 20, ...params };
  const res = await api.get('/workflows', { params: merged });
  return res.data?.data;
};
