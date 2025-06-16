<script setup>
  import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
  import PrimaryRegistModal from '@/features/item/components/PrimaryRegistModal.vue';
  import SecondaryRegistModal from '@/features/item/components/SecondaryRegistModal.vue';
  import PrimaryEditModal from '@/features/item/components/PrimaryEditModal.vue';
  import SecondaryEditModal from '@/features/item/components/SecondaryEditModal.vue';
  import PrimaryDeleteModal from '@/features/item/components/PrimaryDeleteModal.vue';

  const activeTab = ref('시술');

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

  const showDropdownId = ref(null);
  const selectedProduct = ref(null);

  const showRegisterDropdown = ref(false);
  const showPrimaryRegisterModal = ref(false);
  const showSecondaryRegisterModal = ref(false);
  const showPrimaryEditModal = ref(false);
  const showSecondaryEditModal = ref(false);
  const showPrimaryDeleteModal = ref(false);

  const primaryForm = ref({ category: 'SERVICE', primaryName: '' });
  const secondaryForm = ref({ primaryItemId: '', secondaryName: '', price: '' });
  const primaryEditForm = ref({ category: '', primaryName: '' });
  const secondaryEditForm = ref({ primaryItemId: '', secondaryName: '', price: '' });

  const primaryOptions = computed(() =>
    items.value.map(item => ({
      id: item.primary_item_id,
      name: item.primary_item_name,
    }))
  );

  const toggleDropdown = itemId => {
    showDropdownId.value = showDropdownId.value === itemId ? null : itemId;
  };

  const toggleRegisterDropdown = () => {
    showRegisterDropdown.value = !showRegisterDropdown.value;
  };

  const openPrimaryRegisterModal = () => {
    showRegisterDropdown.value = false;
    showPrimaryRegisterModal.value = true;
  };

  const openSecondaryRegisterModal = () => {
    showRegisterDropdown.value = false;
    showSecondaryRegisterModal.value = true;
  };

  const openPrimaryEditModal = item => {
    primaryEditForm.value = {
      category: item.category,
      primaryName: item.primary_item_name,
    };
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
    showDropdownId.value = null;
    selectedProduct.value = null;
  };

  const handlePrimarySubmit = form => {
    console.log('1차 상품 등록:', form);
    closeModals();
  };

  const handleSecondarySubmit = form => {
    console.log('2차 상품 등록:', form);
    closeModals();
  };

  const handlePrimaryEdit = form => {
    console.log('1차 상품 수정:', form);
    closeModals();
  };

  const handleSecondaryEdit = form => {
    console.log('2차 상품 수정:', form);
    closeModals();
  };

  const handlePrimaryDelete = () => {
    console.log('✅ 삭제 확정된 상품:', selectedProduct.value);
    showPrimaryDeleteModal.value = false;
    selectedProduct.value = null;
  };

  const handleClickOutside = event => {
    const dropdownButtons = document.querySelectorAll('.icon-button, .register-button');
    const dropdownMenus = document.querySelectorAll('.dropdown-menu');
    let clickedInside = false;

    dropdownButtons.forEach(btn => {
      if (btn.contains(event.target)) clickedInside = true;
    });
    dropdownMenus.forEach(menu => {
      if (menu.contains(event.target)) clickedInside = true;
    });

    if (!clickedInside) {
      showDropdownId.value = null;
      showRegisterDropdown.value = false;
    }
  };

  onMounted(() => window.addEventListener('click', handleClickOutside));
  onBeforeUnmount(() => window.removeEventListener('click', handleClickOutside));

  const filteredItems = computed(() =>
    items.value.filter(item =>
      activeTab.value === '시술' ? item.category === 'SERVICE' : item.category === 'PRODUCT'
    )
  );
</script>

<template>
  <div class="item-manage-wrapper">
    <div class="header-row">
      <h2 class="page-title">상품관리</h2>
      <div class="header-actions">
        <div class="tab-group">
          <div class="tab" :class="{ active: activeTab === '시술' }" @click="activeTab = '시술'">
            시술
          </div>
          <div class="tab" :class="{ active: activeTab === '상품' }" @click="activeTab = '상품'">
            상품
          </div>
        </div>
        <div class="register-wrapper relative">
          <button class="register-button" @click="toggleRegisterDropdown">상품 등록</button>
          <div v-if="showRegisterDropdown" class="dropdown-menu">
            <div class="dropdown-item" @click="openPrimaryRegisterModal">1차 상품 등록</div>
            <div class="dropdown-item" @click="openSecondaryRegisterModal">2차 상품 등록</div>
          </div>
        </div>
      </div>
    </div>

    <div v-for="item in filteredItems" :key="item.primary_item_id" class="activity-section">
      <div class="card">
        <div class="table-header">
          <div class="header-cell">{{ item.primary_item_name }}</div>
          <div class="header-cell text-right">가격</div>
          <div class="header-cell text-right relative">
            <button class="icon-button" @click.stop="toggleDropdown(item.primary_item_id)">
              ⋯
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

    <!-- 모달 등록 -->
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
  </div>
</template>

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
  .page-title {
    font-size: 20px;
    font-weight: bold;
  }
  .header-actions {
    display: flex;
    gap: 1rem;
    align-items: center;
  }
  .tab-group {
    display: flex;
    border: 1px solid var(--color-neutral-black);
    border-radius: 6px;
    overflow: hidden;
  }
  .tab {
    padding: 0.5rem 1.25rem;
    font-size: 14px;
    cursor: pointer;
    background-color: white;
    color: black;
  }
  .tab.active {
    background-color: black;
    color: white;
  }
  .register-button {
    padding: 0.5rem 1rem;
    font-size: 14px;
    border: 1px solid var(--color-gray-400);
    background-color: white;
    border-radius: 4px;
    cursor: pointer;
  }
  .register-wrapper {
    position: relative;
  }
  .activity-section {
    margin-top: 2rem;
  }
  .card {
    background: var(--color-neutral-white);
    border: 1px solid var(--color-gray-200);
    border-radius: 0.75rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    overflow: hidden;
    padding: 0;
    margin-bottom: 2rem;
  }
  .table-header {
    display: grid;
    grid-template-columns: 1fr 100px 50px;
    background-color: var(--color-gray-100);
    padding: 1rem;
    font-weight: 600;
    font-size: 14px;
    border-bottom: 1px solid var(--color-gray-200);
  }
  .item-row {
    display: grid;
    grid-template-columns: 1fr 100px 50px;
    padding: 1rem;
    align-items: center;
    border-bottom: 1px solid var(--color-gray-100);
  }
  .row-cell,
  .header-cell {
    font-size: 14px;
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
    color: var(--color-gray-500);
    padding: 0;
  }
  .dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    background: white;
    border: 1px solid var(--color-gray-200);
    border-radius: 4px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    z-index: 10;
  }
  .dropdown-item {
    padding: 0.5rem 1rem;
    font-size: 14px;
    cursor: pointer;
    white-space: nowrap;
  }
  .dropdown-item:hover {
    background-color: var(--color-gray-100);
  }
  .clickable {
    cursor: pointer;
    color: var(--color-blue-600);
  }
  .clickable:hover {
    opacity: 0.8;
  }
</style>
