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

      // Mock API call to fetch workflow data
      const fetchWorkflowData = async id => {
        try {
          // TODO: Replace with actual API call
          // const response = await workflowApi.getWorkflow(id);
          // return response.data;

          // Mock data for demonstration
          return {
            id: id,
            title: '장기 미방문 고객 리타겟팅',
            description: '60일 이상 미방문 고객을 위한 자동 재방문 유도 워크플로우',
            isActive: true,
            targetCustomerGrades: ['신규 고객', '성장 고객'],
            targetTags: ['VIP'],
            excludeDormantCustomers: true,
            dormantPeriodMonths: 6,
            excludeRecentMessageReceivers: true,
            recentMessagePeriodDays: 30,
            trigger: 'visit-cycle',
            triggerCategory: 'periodic',
            triggerConfig: {
              visitCycleDays: 14,
              treatmentId: 'facial-basic',
              daysAfterTreatment: 7,
              daysAfterRegistration: 0,
              birthdayDaysBefore: 3,
              visitMilestone: 10,
              amountMilestone: 100000,
              riskThresholdDays: 30,
              followupDays: 7,
            },
            action: 'coupon-message',
            actionConfig: {
              messageTemplateId: 'revisit',
              couponId: 'discount-20',
              notificationTitle: '',
              notificationContent: '',
              sendTime: new Date('2024-01-01T10:00:00'),
            },
            createdAt: '2024-01-15T09:00:00Z',
            updatedAt: '2024-01-20T14:30:00Z',
          };
        } catch (error) {
          console.error('Failed to fetch workflow:', error);
          throw error;
        }
      };

      const updateWorkflow = async formData => {
        try {
          // TODO: Replace with actual API call
          // await workflowApi.updateWorkflow(route.params.id, formData);

          console.log('Updating workflow:', route.params.id, formData);

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
        updateWorkflow,
        goBack,
      };
    },
  };
</script>
