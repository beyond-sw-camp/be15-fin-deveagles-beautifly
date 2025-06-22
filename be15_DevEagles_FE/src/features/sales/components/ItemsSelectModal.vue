<template>
  <Teleport to="body">
    <div class="overlay" @click.self="closeModal">
      <div class="modal-box">
        <div class="modal-header">
          <h2 class="title">상품 선택</h2>
          <BaseButton type="ghost" size="sm" @click="closeModal">×</BaseButton>
        </div>

        <div class="modal-content">
          <!-- 탭 -->
          <div class="tab-buttons">
            <BaseButton
              v-for="tab in tabs"
              :key="tab"
              :type="selectedTab === tab ? 'primary' : 'ghost'"
              class="tab-button"
              @click="selectedTab = tab"
            >
              {{ tab === 'SERVICE' ? '시술' : '제품' }}
            </BaseButton>
          </div>

          <!-- 상품 목록 -->
          <div class="product-list scrollable">
            <div
              v-for="product in filteredProducts"
              :key="product.id"
              :class="['product', selectedIds.includes(product.id) ? 'selected' : '']"
              @click="toggleSelection(product)"
            >
              <div class="name">{{ product.name }}</div>
              <div class="price">{{ formatPrice(product.price) }}</div>
            </div>
          </div>

          <!-- 선택된 상품 뱃지 -->
          <div class="selected-products scrollable">
            <div v-for="product in selectedProducts" :key="product.id" class="selected-badge">
              {{ product.name }}
              <span class="remove-btn" @click.stop="toggleSelection(product)">×</span>
            </div>
          </div>
        </div>

        <!-- 하단 버튼 -->
        <div class="footer-buttons">
          <BaseButton type="primary" outline @click="closeModal">취소</BaseButton>
          <BaseButton type="primary" @click="applySelection">상품 선택 완료</BaseButton>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
  import { ref, computed, defineEmits } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const emit = defineEmits(['close', 'apply']);

  const tabs = ['SERVICE', 'PRODUCT'];
  const selectedTab = ref('SERVICE');
  const selectedIds = ref([]);

  const productList = ref([
    { id: 1, type: 'SERVICE', name: '커트', price: 35000 },
    { id: 2, type: 'SERVICE', name: '펌(남자)', price: 158000 },
    { id: 5, type: 'SERVICE', name: '커트', price: 35000 },
    { id: 6, type: 'SERVICE', name: '펌(남자)', price: 158000 },
    { id: 7, type: 'SERVICE', name: '커트', price: 35000 },
    { id: 8, type: 'SERVICE', name: '펌(남자)', price: 158000 },
    { id: 9, type: 'SERVICE', name: '커트', price: 35000 },
    { id: 10, type: 'SERVICE', name: '펌(남자)', price: 158000 },
    { id: 11, type: 'SERVICE', name: '커트', price: 35000 },
    { id: 12, type: 'SERVICE', name: '펌(남자)', price: 158000 },
    { id: 3, type: 'PRODUCT', name: '샴푸', price: 22000 },
    { id: 4, type: 'PRODUCT', name: '트리트먼트', price: 44000 },
  ]);

  const filteredProducts = computed(() =>
    productList.value.filter(p => p.type === selectedTab.value)
  );

  const selectedProducts = computed(() =>
    productList.value.filter(p => selectedIds.value.includes(p.id))
  );

  const toggleSelection = product => {
    const index = selectedIds.value.indexOf(product.id);
    if (index > -1) selectedIds.value.splice(index, 1);
    else selectedIds.value.push(product.id);
  };

  const applySelection = () => {
    const selectedItems = productList.value.filter(p => selectedIds.value.includes(p.id));
    emit('apply', selectedItems);
    emit('close');
  };

  const closeModal = () => {
    emit('close');
  };

  const formatPrice = price => `${price.toLocaleString()}원`;
</script>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }

  .modal-box {
    width: 500px;
    height: 600px;
    background: white;
    border-radius: 8px;
    overflow: hidden;
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

  .modal-content {
    flex: 1;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    overflow: hidden;
  }

  .tab-buttons {
    display: flex;
    gap: 8px;
  }

  .tab-buttons button {
    flex: 1;
  }

  .tab-buttons button.active {
    background: #364f6b;
    color: white;
  }

  .scrollable {
    overflow-y: auto;
    max-height: 150px;
  }

  .product-list {
    flex: none;
    max-height: 280px;
    overflow-y: auto;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .product {
    width: calc(50% - 4px);
    border: 2px solid #ccc;
    border-radius: 6px;
    padding: 8px;
    cursor: pointer;
  }

  .product.selected {
    border: 2px solid #364f6b;
    background: #f0f0f0;
  }

  .product .name {
    font-size: 14px;
    margin-bottom: 4px;
  }

  .product .price {
    font-size: 12px;
    color: #666;
  }

  .selected-products {
    overflow-y: auto;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 8px;
  }

  .selected-badge {
    background: #ff6b91;
    color: white;
    border-radius: 16px;
    padding: 4px 12px;
    font-size: 13px;
    display: flex;
    align-items: center;
  }

  .selected-badge .remove-btn {
    margin-left: 8px;
    font-weight: bold;
    cursor: pointer;
  }

  .footer-buttons {
    padding: 16px;
    display: flex;
    justify-content: flex-end;
    border-top: 1px solid #ddd;
  }
</style>
