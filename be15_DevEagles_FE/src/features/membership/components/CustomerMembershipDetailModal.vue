<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="modelValue" class="custom-modal-backdrop" @click.self="close">
        <div class="custom-modal">
          <div class="modal-header">
            <h2>고객 회원권 상세 조회</h2>
            <BaseButton class="modal-close" size="sm" icon @click="close">×</BaseButton>
          </div>

          <div class="modal-content">
            <!-- 고객 정보 -->
            <div class="section">
              <h3>보유 회원권</h3>
              <div class="info-grid">
                <div><BaseForm label="이름" :model-value="customer.name" readonly /></div>
                <div><BaseForm label="연락처" :model-value="customer.phone" readonly /></div>
              </div>
            </div>

            <!-- 보유 회원권 -->
            <div class="section">
              <h3>보유 회원권</h3>
              <div class="tab-row">
                <BaseButton
                  v-for="tab in ['PREPAID', 'COUNT']"
                  :key="tab"
                  :type="activeTab === tab ? 'primary' : 'ghost'"
                  @click="activeTab = tab"
                >
                  {{ tab === 'PREPAID' ? '선불권' : '횟수권' }}
                </BaseButton>
              </div>

              <div class="base-table-wrapper scrollable-table">
                <BaseTable
                  :columns="membershipColumns"
                  :data="filteredMemberships"
                  row-key="id"
                  @row-click="openEditModal"
                >
                  <template #cell-type="{ value }">
                    {{ value === 'PREPAID' ? '선불권' : value === 'COUNT' ? '횟수권' : value }}
                  </template>
                </BaseTable>
              </div>
            </div>

            <!-- 사용 내역 -->
            <div class="section">
              <h3>사용 내역</h3>
              <div class="base-table-wrapper scrollable-table">
                <BaseTable :columns="usageColumns" :data="customer.usageHistory" row-key="id" />
              </div>
            </div>
          </div>

          <!-- ✅ Toast 컴포넌트 위치 여기 -->
          <BaseToast ref="toastRef" />
        </div>
      </div>
    </transition>
  </Teleport>

  <!-- 수정 모달 -->
  <CustomerMembershipEditModal
    v-if="editModalVisible"
    v-model="editModalVisible"
    :membership="selectedMembership"
    @updated="handleUpdated"
  />
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import CustomerMembershipEditModal from '@/features/membership/components/CustomerMembershipEditModal.vue';

  const props = defineProps({
    modelValue: Boolean,
    customer: {
      type: Object,
      required: true,
      default: () => ({
        name: '',
        phone: '',
        memberships: [],
        usageHistory: [],
      }),
    },
  });

  const emit = defineEmits(['update:modelValue']);
  const close = () => emit('update:modelValue', false);

  const toastRef = ref(null);

  const activeTab = ref('PREPAID');
  const editModalVisible = ref(false);
  const selectedMembership = ref(null);

  const filteredMemberships = computed(() =>
    props.customer.memberships.filter(m => m.type === activeTab.value)
  );

  const membershipColumns = [
    { key: 'name', title: '회원권명', width: '180px' },
    { key: 'type', title: '종류', width: '100px' },
    { key: 'remaining', title: '잔여', width: '80px' },
    { key: 'create', title: '결제일', width: '120px' },
    { key: 'expiry', title: '만료일', width: '120px' },
  ];

  const usageColumns = [
    { key: 'date', title: '사용일', width: '120px' },
    { key: 'detail', title: '내용', width: '300px' },
    { key: 'change', title: '증감', width: '100px' },
  ];

  const openEditModal = row => {
    selectedMembership.value = row;
    editModalVisible.value = true;
  };

  const handleUpdated = () => {
    toastRef.value?.success('고객 회원권 정보가 수정되었습니다.');
  };
</script>

<style scoped>
  .custom-modal-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .custom-modal {
    background: white;
    width: 800px;
    height: 800px;
    border-radius: 8px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
  }
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #eee;
  }
  .modal-close {
    background: none;
    border: none;
    font-size: 20px;
    padding: 0 8px;
    line-height: 1;
  }
  .modal-content {
    padding: 1.5rem;
    flex: 1;
    overflow-y: auto;
  }
  .section {
    margin-top: 1.5rem;
  }
  .info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }
  .tab-row {
    display: flex;
    gap: 0.5rem;
    margin: 1rem 0;
  }
  .base-table-wrapper {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    padding: 16px;
    margin-top: 0.5rem;
  }
  .scrollable-table {
    max-height: 200px;
    overflow-y: auto;
  }
</style>
