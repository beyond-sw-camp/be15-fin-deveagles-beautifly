<template>
  <WorkflowFormBase
    title="워크플로우 수정"
    subtitle="기존 워크플로우를 수정하세요"
    save-button-text="수정"
    :initial-data="workflowData"
    @save="updateWorkflow"
    @cancel="goBack"
  />
</template>

<script>
  import { ref, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { useToast } from '@/composables/useToast';
  import WorkflowFormBase from '../components/WorkflowFormBase.vue';
  import { getWorkflow, updateWorkflow } from '@/features/workflows/api/workflows.js';
  import { useAuthStore } from '@/store/auth.js';

  export default {
    name: 'WorkflowEdit',
    components: {
      WorkflowFormBase,
    },
    setup() {
      const router = useRouter();
      const route = useRoute();
      const toast = useToast();
      const workflowData = ref(null);

      const fetchWorkflowData = async id => {
        const authStore = useAuthStore();
        const data = await getWorkflow(id, authStore.shopId);
        // backend returns triggerType/actionType etc; convert to frontend fields
        return {
          id: data.id,
          title: data.title,
          description: data.description,
          isActive: data.isActive,
          // parse JSON fields
          targetCustomerGrades: JSON.parse(data.targetCustomerGrades ?? '[]'),
          targetTags: JSON.parse(data.targetTags ?? '[]'),
          excludeDormantCustomers: data.excludeDormantCustomers,
          dormantPeriodMonths: data.dormantPeriodMonths,
          excludeRecentMessageReceivers: data.excludeRecentMessageReceivers,
          recentMessagePeriodDays: data.recentMessagePeriodDays,
          trigger: data.triggerType,
          triggerCategory: data.triggerCategory,
          triggerConfig: JSON.parse(data.triggerConfig ?? '{}'),
          action: data.actionType,
          actionConfig: JSON.parse(data.actionConfig ?? '{}'),
        };
      };

      const updateWorkflowRequest = async formData => {
        try {
          const authStore = useAuthStore();
          const payload = { ...formData, shopId: authStore.shopId, staffId: authStore.userId };
          await updateWorkflow(route.params.id, payload);

          toast.show({
            type: 'success',
            message: '워크플로우가 성공적으로 수정되었습니다.',
          });

          setTimeout(() => {
            router.push('/workflows');
          }, 1500);
        } catch (error) {
          console.error('Failed to update workflow:', error);
          toast.show({
            type: 'error',
            message: '워크플로우 수정 중 오류가 발생했습니다.',
          });
        }
      };

      const goBack = () => {
        router.push('/workflows');
      };

      onMounted(async () => {
        try {
          const workflowId = route.params.id;
          if (!workflowId) {
            toast.show({
              type: 'error',
              message: '워크플로우 ID가 올바르지 않습니다.',
            });
            router.push('/workflows');
            return;
          }

          workflowData.value = await fetchWorkflowData(workflowId);
        } catch (error) {
          toast.show({
            type: 'error',
            message: '워크플로우 데이터를 불러오는데 실패했습니다.',
          });
          router.push('/workflows');
        } finally {
          // Loading complete
        }
      });

      return {
        workflowData,
        updateWorkflow: updateWorkflowRequest,
        goBack,
      };
    },
  };
</script>
