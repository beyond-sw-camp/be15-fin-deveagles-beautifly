<template>
  <div class="item-manage-wrapper">
    <!-- 헤더 -->
    <div class="header-row">
      <h1 class="page-title">상품 관리</h1>
      <div class="register-wrapper relative">
        <BaseButton class="register-button" @click="toggleRegisterDropdown">상품 등록</BaseButton>
        <div v-if="showRegisterDropdown" class="dropdown-menu">
          <div class="dropdown-item" @click="openPrimaryRegisterModal">1차 상품 등록</div>
          <div class="dropdown-item" @click="openSecondaryRegisterModal">2차 상품 등록</div>
        </div>
      </div>
    </div>

    <!-- 탭 -->
    <div class="tab-filter-row">
      <div class="tab-group">
        <div class="tab" :class="{ active: activeTab === '시술' }" @click="activeTab = '시술'">
          시술
        </div>
        <div class="tab" :class="{ active: activeTab === '상품' }" @click="activeTab = '상품'">
          상품
        </div>
      </div>
    </div>

    <!-- 리스트 -->
    <div v-for="item in filteredItems" :key="item.primary_item_id" class="activity-section">
      <div class="card">
        <div class="table-header">
          <div class="header-cell">{{ item.primary_item_name }}</div>
          <div class="header-cell text-right">가격</div>
          <div class="header-cell text-right relative">
            <button class="icon-button" @click.stop="toggleDropdown(item.primary_item_id)">
              ···
            </button>
            <div v-if="showDropdownId === item.primary_item_id" class="dropdown-menu">
              <div class="dropdown-item" @click="openPrimaryEditModal(item)">1차 상품 수정</div>
              <div class="dropdown-item" @click="deletePrimaryItem(item)">1차 상품 삭제</div>
            </div>
          </div>
        </div>
        <div v-for="sub in item.subItems" :key="sub.secondary_item_id" class="item-row">
          <div class="row-cell clickable" @click="openSecondaryEditModal(item, sub)">
            {{ sub.secondary_item_name }}
          </div>
          <div class="row-cell text-right">{{ sub.secondary_item_price }}원</div>
          <div class="row-cell text-right"></div>
        </div>
      </div>
    </div>

    <!-- 모달 -->
    <PrimaryRegistModal
      v-if="showPrimaryRegisterModal"
      v-model="primaryForm"
      @submit="handlePrimarySubmit"
      @close="closeModals"
    />
    <SecondaryRegistModal
      v-if="showSecondaryRegisterModal"
      v-model="secondaryForm"
      :primary-options="primaryOptions"
      @submit="handleSecondarySubmit"
      @close="closeModals"
    />
    <PrimaryEditModal
      v-if="showPrimaryEditModal"
      v-model="primaryEditForm"
      @submit="handlePrimaryEdit"
      @close="closeModals"
    />
    <SecondaryEditModal
      v-if="showSecondaryEditModal"
      v-model="secondaryEditForm"
      :primary-options="primaryOptions"
      @submit="handleSecondaryEdit"
      @close="closeModals"
    />
    <PrimaryDeleteModal
      v-if="showPrimaryDeleteModal"
      v-model="showPrimaryDeleteModal"
      @confirm="handlePrimaryDelete"
    />

    <!-- 토스트 -->
    <BaseToast ref="toastRef" />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimaryRegistModal from '@/features/items/components/PrimaryRegistModal.vue';
  import SecondaryRegistModal from '@/features/items/components/SecondaryRegistModal.vue';
  import PrimaryEditModal from '@/features/items/components/PrimaryEditModal.vue';
  import SecondaryEditModal from '@/features/items/components/SecondaryEditModal.vue';
  import PrimaryDeleteModal from '@/features/items/components/PrimaryDeleteModal.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  const toastRef = ref(null);

  const activeTab = ref('시술');
  const showRegisterDropdown = ref(false);
  const showPrimaryRegisterModal = ref(false);
  const showSecondaryRegisterModal = ref(false);
  const showPrimaryEditModal = ref(false);
  const showSecondaryEditModal = ref(false);
  const showPrimaryDeleteModal = ref(false);
  const showDropdownId = ref(null);
  const selectedProduct = ref(null);

  const items = ref([
    {
      primary_item_id: 1,
      primary_item_name: '펌(남성)',
      category: 'SERVICE',
      subItems: [
        { secondary_item_id: 11, secondary_item_name: '히피펌', secondary_item_price: 158000 },
        {
          secondary_item_id: 12,
          secondary_item_name: '스핀스왈로펌',
          secondary_item_price: 168000,
        },
      ],
    },
    {
      primary_item_id: 2,
      primary_item_name: '헤어',
      category: 'PRODUCT',
      subItems: [
        { secondary_item_id: 21, secondary_item_name: '샴푸', secondary_item_price: 20000 },
        { secondary_item_id: 22, secondary_item_name: '트리트먼트', secondary_item_price: 35000 },
      ],
    },
  ]);

  const primaryForm = ref({ category: 'SERVICE', primaryName: '' });
  const secondaryForm = ref({ primaryItemId: '', secondaryName: '', price: '' });
  const primaryEditForm = ref({ category: '', primaryName: '' });
  const secondaryEditForm = ref({ primaryItemId: '', secondaryName: '', price: '' });

  const primaryOptions = computed(() =>
    items.value.map(item => ({ id: item.primary_item_id, name: item.primary_item_name }))
  );

  const filteredItems = computed(() =>
    items.value.filter(
      item => item.category === (activeTab.value === '시술' ? 'SERVICE' : 'PRODUCT')
    )
  );

  const toggleRegisterDropdown = () => (showRegisterDropdown.value = !showRegisterDropdown.value);
  const toggleDropdown = id => (showDropdownId.value = showDropdownId.value === id ? null : id);

  const openPrimaryRegisterModal = () => {
    showRegisterDropdown.value = false;
    showPrimaryRegisterModal.value = true;
  };

  const openSecondaryRegisterModal = () => {
    showRegisterDropdown.value = false;
    showSecondaryRegisterModal.value = true;
  };

  const openPrimaryEditModal = item => {
    primaryEditForm.value = { category: item.category, primaryName: item.primary_item_name };
    selectedProduct.value = item;
    showDropdownId.value = null;
    showPrimaryEditModal.value = true;
  };

  const openSecondaryEditModal = (item, sub) => {
    secondaryEditForm.value = {
      primaryItemId: item.primary_item_id,
      secondaryName: sub.secondary_item_name,
      price: sub.secondary_item_price,
    };
    selectedProduct.value = sub;
    showSecondaryEditModal.value = true;
  };

  const deletePrimaryItem = item => {
    selectedProduct.value = item;
    showDropdownId.value = null;
    showPrimaryDeleteModal.value = true;
  };

  const closeModals = () => {
    showPrimaryRegisterModal.value = false;
    showSecondaryRegisterModal.value = false;
    showPrimaryEditModal.value = false;
    showSecondaryEditModal.value = false;
    selectedProduct.value = null;
  };

  const handlePrimarySubmit = form => {
    console.log('등록된 1차 상품:', form);
    toastRef.value?.success('1차 상품이 등록되었습니다.');
    closeModals();
  };

  const handleSecondarySubmit = form => {
    console.log('등록된 2차 상품:', form);
    toastRef.value?.success('2차 상품이 등록되었습니다.');
    closeModals();
  };

  const handlePrimaryEdit = form => {
    console.log('수정된 1차 상품:', form);
    toastRef.value?.success('1차 상품이 수정되었습니다.');
    closeModals();
  };

  const handleSecondaryEdit = form => {
    console.log('수정된 2차 상품:', form);
    toastRef.value?.success('2차 상품이 수정되었습니다.');
    closeModals();
  };

  const handlePrimaryDelete = () => {
    console.log('삭제할 1차 상품:', selectedProduct.value);
    toastRef.value?.success('1차 상품이 삭제되었습니다.');
    showPrimaryDeleteModal.value = false;
    selectedProduct.value = null;
  };

  const handleClickOutside = event => {
    if (
      !event.target.closest('.dropdown-menu') &&
      !event.target.closest('.register-button') &&
      !event.target.closest('.icon-button')
    ) {
      showDropdownId.value = null;
      showRegisterDropdown.value = false;
    }
  };

  onMounted(() => window.addEventListener('click', handleClickOutside));
  onBeforeUnmount(() => window.removeEventListener('click', handleClickOutside));
</script>

<style scoped>
  .item-manage-wrapper {
    padding: 2rem;
  }
  .header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
  }
  .register-wrapper {
    position: relative;
  }
  .register-button {
    font-size: 14px;
  }
  .tab-filter-row {
    margin-bottom: 1rem;
  }
  .tab-group {
    display: flex;
    width: 220px;
    border: 1px solid #364f6b;
    border-radius: 6px;
    overflow: hidden;
  }
  .tab {
    flex: 1;
    text-align: center;
    padding: 0.5rem 0;
    font-size: 14px;
    cursor: pointer;
    background-color: white;
    color: #364f6b;
    font-weight: bold;
    border-right: 1px solid #364f6b;
  }
  .tab:last-child {
    border-right: none;
  }
  .tab.active {
    background-color: #364f6b;
    color: white;
  }
  .card {
    margin-top: 1rem;
    border: 1px solid #ddd;
    border-radius: 10px;
    overflow: hidden;
    background: #fff;
  }
  .table-header,
  .item-row {
    display: grid;
    grid-template-columns: 1fr 100px 50px;
    padding: 1rem;
    font-size: 14px;
    border-bottom: 1px solid #f0f0f0;
    align-items: center;
  }
  .text-right {
    text-align: right;
  }
  .relative {
    position: relative;
  }
  .icon-button {
    background: none;
    border: none;
    font-size: 18px;
    cursor: pointer;
  }
  .dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    background: white;
    z-index: 100;
    padding: 0.5rem 0;
  }
  .dropdown-item {
    padding: 0.5rem 1rem;
    font-size: 14px;
    border-radius: 6px;
    border: 1px solid #364f6b;
    cursor: pointer;
    white-space: nowrap;
  }
</style>
