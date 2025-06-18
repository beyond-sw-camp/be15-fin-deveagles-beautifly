<template>
  <div class="overlay" @click.self="close">
    <div class="modal-panel">
      <!-- 상단 헤더 -->
      <div class="top-bar">
        <div class="info">
          <h2 class="title">회원권 관리</h2>
        </div>
        <div class="actions">
          <button class="close-button" @click="close">&times;</button>
        </div>
      </div>

      <div class="divider"></div>

      <div class="info-action-row">
        <div class="info">
          <p class="description">판매할 회원권(횟수권, 기간권)을 등록해보세요!</p>
        </div>
        <div class="actions">
          <button class="create-button" @click="openRegistModal">회원권 등록</button>
        </div>
      </div>

      <!-- 선불권 리스트 -->
      <section class="card-section">
        <BaseCard title="선불액">
          <div class="card-table">
            <div class="card-row header">
              <div>이름</div>
              <div>충전금액</div>
              <div>판매금액</div>
            </div>
            <div v-for="item in prepaidList" :key="item.id" class="card-row">
              <div @click="openEditModal({ ...item, type: 'PREPAID' })">{{ item.name }}</div>
              <div @click="openEditModal({ ...item, type: 'PREPAID' })">
                {{ formatPrice(item.charge) }}
              </div>
              <div @click="openEditModal({ ...item, type: 'PREPAID' })">
                {{ formatPrice(item.price) }}
              </div>
            </div>
          </div>
        </BaseCard>
      </section>

      <!-- 횟수권 리스트 -->
      <section class="card-section">
        <BaseCard title="횟수권">
          <div class="card-table">
            <div class="card-row header">
              <div>이름</div>
              <div>횟수</div>
              <div>가격</div>
            </div>
            <div v-for="item in countList" :key="item.id" class="card-row">
              <div @click="openEditModal({ ...item, type: 'COUNT' })">{{ item.name }}</div>
              <div @click="openEditModal({ ...item, type: 'COUNT' })">{{ item.count }}회</div>
              <div @click="openEditModal({ ...item, type: 'COUNT' })">
                {{ formatPrice(item.price) }}
              </div>
            </div>
          </div>
        </BaseCard>
      </section>

      <!-- 등록 모달 -->
      <MembershipRegistModal
        v-if="showRegistModal"
        v-model="membershipForm"
        @submit="handleRegistSubmit"
        @close="closeRegistModal"
      />

      <!-- 수정 모달 -->
      <MembershipEditModal
        v-if="showEditModal"
        v-model="membershipForm"
        @submit="handleEditSubmit"
        @close="closeEditModal"
      />
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import MembershipRegistModal from '@/features/membership/components/MembershipRegistModal.vue';
  import MembershipEditModal from '@/features/membership/components/MembershipEditModal.vue';

  const emit = defineEmits(['close']);
  const close = () => emit('close');

  const prepaidList = ref([{ id: 1, name: '선불권', charge: 1100000, price: 1000000 }]);
  const countList = ref([{ id: 2, name: '(상품) 시술 10회 이용권', count: 10, price: 150000 }]);

  const formatPrice = val => `${val.toLocaleString('ko-KR')} 원`;

  // 등록 모달 제어
  const showRegistModal = ref(false);
  const openRegistModal = () => {
    membershipForm.value = {
      type: 'PREPAID',
      name: '',
      count: null,
      price: null,
      bonusType: '',
      extraCount: null,
      expireValue: null,
      expireUnit: 'MONTH',
      memo: '',
    };
    showRegistModal.value = true;
  };
  const closeRegistModal = () => {
    showRegistModal.value = false;
  };
  const handleRegistSubmit = form => {
    console.log('등록된 데이터:', form);
    closeRegistModal();
  };

  // 수정 모달 제어
  const showEditModal = ref(false);
  const openEditModal = item => {
    membershipForm.value = {
      ...item,
      bonusType: item.bonusType ?? '',
      expireUnit: item.expireUnit ?? 'MONTH',
    };
    showEditModal.value = true;
  };
  const closeEditModal = () => {
    showEditModal.value = false;
  };
  const handleEditSubmit = form => {
    console.log('수정된 데이터:', form);
    closeEditModal();
  };

  // 공통 form 상태
  const membershipForm = ref({});
</script>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    z-index: 1000;
  }

  .modal-panel {
    position: fixed;
    top: 0;
    left: 240px;
    width: calc(100% - 240px);
    height: 100vh;
    background: white;
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow-y: auto;
    z-index: 1001;
  }

  .top-bar {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 6px;
  }

  .description {
    font-size: 14px;
    color: #666;
  }

  .actions {
    display: flex;
    align-items: flex-start;
    gap: 8px;
  }

  .close-button {
    font-size: 24px;
    background: transparent;
    border: none;
    cursor: pointer;
    line-height: 1;
  }

  .create-button {
    padding: 8px 16px;
    background-color: white;
    color: black;
    border: black solid 1px;
    border-radius: 12px;
    font-size: 14px;
    cursor: pointer;
  }

  .divider {
    margin: 16px 0;
    height: 1px;
    background-color: #e2e2e2;
  }

  .info-action-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .card-section {
    margin-bottom: 24px;
  }

  .card-table {
    width: 100%;
    display: flex;
    flex-direction: column;
  }

  .card-row {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    padding: 12px 8px;
    font-size: 14px;
    border-bottom: 1px solid #eee;
  }

  .card-row.header {
    font-weight: bold;
    background-color: #f9f9f9;
  }
</style>
