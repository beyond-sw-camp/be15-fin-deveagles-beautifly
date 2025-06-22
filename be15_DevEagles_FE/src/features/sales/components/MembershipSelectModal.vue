<template>
  <Teleport to="body">
    <div class="overlay" @click.self="closeModal">
      <div class="modal-box">
        <div class="modal-header">
          <h2 class="title">회원권 선택</h2>
          <BaseButton type="ghost" size="sm" @click="closeModal">×</BaseButton>
        </div>

        <div class="content">
          <!-- 탭 -->
          <div class="tab-buttons">
            <BaseButton
              v-for="tab in tabs"
              :key="tab"
              :type="selectedTab === tab ? 'primary' : 'ghost'"
              class="tab-button"
              @click="
                selectedTab = tab;
                selectedItem = null;
              "
            >
              {{ tab === 'PREPAID' ? '선불권' : '횟수권' }}
            </BaseButton>
          </div>

          <!-- 리스트 or 상세 -->
          <div v-if="!selectedItem" class="item-list">
            <div
              v-for="item in filteredList"
              :key="item.id"
              :class="['item', selectedItem?.id === item.id ? 'selected' : '']"
              @click="selectItem(item)"
            >
              {{ item.name }} : {{ formatPrice(item.price) }}
            </div>
          </div>

          <div v-else class="item-detail">
            <h3 class="detail-title">{{ selectedItem.name }}</h3>
            <p class="detail-price">{{ formatPrice(selectedItem.price) }}</p>

            <div class="field-row">
              <label>제공 혜택</label>
              <div>{{ selectedItem.benefit }}</div>
            </div>
            <div class="field-row">
              <label>충전 금액</label>
              <div>{{ formatPrice(selectedItem.chargeAmount) }}</div>
            </div>
            <div class="field-row">
              <label>잔여 금액</label>
              <div>{{ formatPrice(selectedItem.remainAmount) }}</div>
            </div>
            <div class="field-row">
              <label>유효 기간</label>
              <div>{{ selectedItem.expireDate }}</div>
            </div>
            <div class="field-row">
              <label>담당자</label>
              <div>{{ selectedItem.manager }}</div>
            </div>

            <BaseButton type="ghost" size="sm" class="back-button" @click="selectedItem = null">
              ← 목록으로
            </BaseButton>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
  import { ref, computed, defineProps, defineEmits, watch } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: Boolean,
  });

  const emit = defineEmits(['update:modelValue', 'apply', 'close']);

  const visible = ref(props.modelValue);
  watch(
    () => props.modelValue,
    val => (visible.value = val)
  );
  watch(visible, val => emit('update:modelValue', val));

  const tabs = ['PREPAID', 'COUNT'];
  const selectedTab = ref('PREPAID');
  const selectedItem = ref(null);

  const membershipList = ref([
    {
      id: 1,
      type: 'PREPAID',
      name: '선불권 A',
      price: 1000000,
      benefit: '(추가) 100000원',
      chargeAmount: 1100000,
      remainAmount: 1100000,
      expireDate: '2026-06-18',
      manager: '김경민',
    },
    {
      id: 2,
      type: 'PREPAID',
      name: '선불권 B',
      price: 500000,
      benefit: '(추가) 50000원',
      chargeAmount: 550000,
      remainAmount: 550000,
      expireDate: '2025-12-31',
      manager: '박보검',
    },
    {
      id: 3,
      type: 'COUNT',
      name: '횟수권 A',
      price: 70000,
      benefit: '-',
      chargeAmount: 70000,
      remainAmount: 70000,
      expireDate: '2025-11-30',
      manager: '이지은',
    },
  ]);

  const filteredList = computed(() =>
    membershipList.value.filter(item => item.type === selectedTab.value)
  );

  const selectItem = item => {
    selectedItem.value = item;
    emit('apply', item);
  };

  const formatPrice = price => `${price.toLocaleString()} 원`;

  const closeModal = () => {
    visible.value = false;
    emit('close');
  };
</script>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 2000;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .modal-box {
    width: 500px;
    height: 600px;
    background: white;
    border-radius: 8px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #ddd;
  }

  .title {
    font-size: 18px;
    font-weight: bold;
  }

  .content {
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .tab-buttons {
    display: flex;
    gap: 8px;
  }

  .tab-button {
    flex: 1;
  }

  .item-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .item {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 6px;
    cursor: pointer;
  }

  .item.selected {
    background-color: #f0f0f0;
    border-color: black;
  }

  .item-detail {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .detail-title {
    font-size: 16px;
    font-weight: bold;
  }

  .detail-price {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
  }

  .field-row {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    border-bottom: 1px solid #eee;
    padding: 4px 0;
  }

  .back-button {
    margin-top: 16px;
    text-align: left;
    padding: 0;
  }
</style>
