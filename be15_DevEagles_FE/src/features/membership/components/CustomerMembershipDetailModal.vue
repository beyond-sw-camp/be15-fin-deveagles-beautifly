<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="modelValue" class="custom-modal-backdrop" @click.self="close">
        <div class="custom-modal">
          <div class="modal-header">
            <h2>고객 회원권 상세 조회</h2>
            <button class="modal-close" @click="close">&times;</button>
          </div>

          <div class="modal-content">
            <!-- 고객 기본 정보 -->
            <div class="section">
              <h3>고객 정보</h3>
              <br />
              <div class="info-grid">
                <div><h3>이름:</h3></div>
                <div>{{ customer.name }}</div>
                <div><h3>연락처:</h3></div>
                <div>{{ customer.phone }}</div>
              </div>
            </div>

            <hr class="divider" />

            <!-- 보유 회원권 -->
            <div class="section">
              <h3>보유 회원권</h3>
              <br />
              <div class="tab-row">
                <div
                  class="tab"
                  :class="{ active: activeTab === 'PREPAID' }"
                  @click="activeTab = 'PREPAID'"
                >
                  선불권
                </div>
                <div
                  class="tab"
                  :class="{ active: activeTab === 'COUNT' }"
                  @click="activeTab = 'COUNT'"
                >
                  횟수권
                </div>
              </div>

              <BaseTable :columns="membershipColumns" :data="filteredMemberships" row-key="id">
                <template #cell-name="{ value, item }">
                  <span class="text-blue-500 underline cursor-pointer" @click="openEditModal(item)">
                    {{ value }}
                  </span>
                </template>
              </BaseTable>
            </div>

            <hr class="divider" />

            <!-- 사용 내역 -->
            <div class="section">
              <h3>사용 내역</h3>
              <br />
              <BaseTable :columns="usageColumns" :data="customer.usageHistory || []" row-key="id" />
            </div>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>

  <!-- 수정 모달 -->
  <Teleport to="body">
    <div v-if="editModalVisible" class="edit-modal-wrapper">
      <CustomerMembershipEditModal
        :model-value="editModalVisible"
        :membership="selectedMembership"
        @update:model-value="editModalVisible = false"
        @close="editModalVisible = false"
      />
    </div>
  </Teleport>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import CustomerMembershipEditModal from '@/features/membership/components/CustomerMembershipEditModal.vue';

  const props = defineProps({
    modelValue: Boolean,
    customer: {
      type: Object,
      required: false,
      default: () => ({
        name: '홍길동',
        phone: '010-1234-5678',
        memberships: [
          {
            id: 1,
            name: '선불권 10만원권',
            type: 'PREPAID',
            remaining: 50000,
            create: '2024-06-01',
            expiry: '2025-06-01',
          },
          {
            id: 2,
            name: '시술 5회권',
            type: 'COUNT',
            remaining: 3,
            count: 5,
            create: '2024-05-15',
            expiry: '2024-11-15',
          },
          {
            id: 3,
            name: '선불권 5만원권',
            type: 'PREPAID',
            remaining: 20000,
            create: '2024-03-10',
            expiry: '2025-03-10',
          },
        ],
        usageHistory: [
          {
            id: 101,
            date: '2024-06-10',
            detail: '컷트',
            change: '-1회',
          },
          {
            id: 102,
            date: '2024-06-05',
            detail: '염색',
            change: '-30,000원',
          },
        ],
      }),
    },
  });

  const emit = defineEmits(['update:modelValue']);
  const close = () => emit('update:modelValue', false);

  const activeTab = ref('PREPAID');

  const filteredMemberships = computed(() =>
    props.customer.memberships.filter(m => m.type === activeTab.value)
  );

  const membershipColumns = [
    { key: 'name', title: '회원권명' },
    { key: 'type', title: '회원권종류' },
    { key: 'remaining', title: '잔여' },
    { key: 'create', title: '결제일' },
    { key: 'expiry', title: '만료일' },
  ];

  const usageColumns = [
    { key: 'date', title: '사용일' },
    { key: 'detail', title: '내용' },
    { key: 'change', title: '증감' },
  ];

  const editModalVisible = ref(false);
  const selectedMembership = ref(null);

  const openEditModal = membership => {
    selectedMembership.value = membership;
    editModalVisible.value = true;
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
    pointer-events: auto;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    border-bottom: 1px solid #eee;
  }

  .modal-close {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
  }

  .modal-content {
    padding: 1rem;
    flex: 1;
    overflow-y: auto;
  }

  .section {
    margin-top: 1.5rem;
  }

  .divider {
    margin: 1.5rem 0;
    border: none;
    border-top: 1px solid #ddd;
  }

  .info-grid {
    display: grid;
    grid-template-columns: 45px auto 55px auto;
    gap: 0.5rem 1rem;
    font-size: 15px;
    align-items: center;
  }

  .edit-modal-wrapper {
    position: fixed;
    inset: 0;
    z-index: 2000;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  /* 탭 스타일 */
  .tab-row {
    display: flex;
    gap: 1rem;
    margin-bottom: 1rem;
  }

  .tab {
    padding: 6px 16px;
    border: 1px solid #ccc;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.2s ease;
  }

  .tab.active {
    background-color: #333;
    color: white;
    border-color: #333;
  }
</style>
