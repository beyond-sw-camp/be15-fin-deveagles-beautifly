<template>
  <div class="item-manage-wrapper">
    <!-- 헤더 -->
    <div class="header-row">
      <h1 class="page-title">상품 관리</h1>
      <div class="register-wrapper relative">
        <BaseButton class="register-button" @click="toggleRegisterDropdown"> 상품 등록 </BaseButton>
        <div v-if="showRegisterDropdown" class="dropdown-menu below-left">
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
    <div v-for="item in filteredItems" :key="item.primary_item_id" class="card-section">
      <BaseCard>
        <div class="card-title-row">
          <span>{{ item.primary_item_name }}</span>
          <div class="text-right relative">
            <button class="icon-button" @click.stop="toggleDropdown(item.primary_item_id)">
              ···
            </button>
            <div v-if="showDropdownId === item.primary_item_id" class="dropdown-menu below-left">
              <div class="dropdown-item" @click="openPrimaryEditModal(item)">1차 상품 수정</div>
              <div class="dropdown-item" @click="deletePrimaryItem(item)">1차 상품 삭제</div>
            </div>
          </div>
        </div>

        <div class="card-table">
          <div class="card-row header">
            <div>2차 상품명</div>
            <div class="text-right">가격</div>
            <div class="text-right">시술 시간</div>
          </div>
          <div v-for="sub in item.subItems" :key="sub.secondary_item_id" class="card-row">
            <div class="clickable" @click="openSecondaryEditModal(item, sub)">
              {{ sub.secondary_item_name }}
            </div>
            <div class="text-right clickable" @click="openSecondaryEditModal(item, sub)">
              {{ formatPrice(sub.secondary_item_price) }}
            </div>
            <div class="text-right clickable" @click="openSecondaryEditModal(item, sub)">
              {{ sub.duration ? sub.duration + '분' : '-' }}
            </div>
          </div>
        </div>
      </BaseCard>
    </div>

    <!-- 모달들 -->
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

    <BaseToast ref="toastRef" />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import PrimaryRegistModal from '@/features/items/components/PrimaryRegistModal.vue';
  import SecondaryRegistModal from '@/features/items/components/SecondaryRegistModal.vue';
  import PrimaryEditModal from '@/features/items/components/PrimaryEditModal.vue';
  import SecondaryEditModal from '@/features/items/components/SecondaryEditModal.vue';
  import PrimaryDeleteModal from '@/features/items/components/PrimaryDeleteModal.vue';

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
        {
          secondary_item_id: 11,
          secondary_item_name: '히피펌',
          secondary_item_price: 158000,
          duration: 90,
        },
        {
          secondary_item_id: 12,
          secondary_item_name: '스핀스왈로펌',
          secondary_item_price: 168000,
          duration: 80,
        },
      ],
    },
    {
      primary_item_id: 2,
      primary_item_name: '염색',
      category: 'SERVICE',
      subItems: [
        {
          secondary_item_id: 21,
          secondary_item_name: '전체염색',
          secondary_item_price: 120000,
          duration: 60,
        },
        {
          secondary_item_id: 22,
          secondary_item_name: '탈색',
          secondary_item_price: 150000,
          duration: 70,
        },
      ],
    },
    {
      primary_item_id: 3,
      primary_item_name: '커트',
      category: 'SERVICE',
      subItems: [
        {
          secondary_item_id: 31,
          secondary_item_name: '남성커트',
          secondary_item_price: 25000,
          duration: 20,
        },
        {
          secondary_item_id: 32,
          secondary_item_name: '여성커트',
          secondary_item_price: 30000,
          duration: 30,
        },
      ],
    },
    {
      primary_item_id: 4,
      primary_item_name: '헤어제품',
      category: 'PRODUCT',
      subItems: [
        { secondary_item_id: 41, secondary_item_name: '샴푸', secondary_item_price: 20000 },
        { secondary_item_id: 42, secondary_item_name: '트리트먼트', secondary_item_price: 35000 },
      ],
    },
    {
      primary_item_id: 5,
      primary_item_name: '스타일링',
      category: 'SERVICE',
      subItems: [
        {
          secondary_item_id: 51,
          secondary_item_name: '드라이',
          secondary_item_price: 15000,
          duration: 15,
        },
        {
          secondary_item_id: 52,
          secondary_item_name: '아이롱',
          secondary_item_price: 20000,
          duration: 20,
        },
      ],
    },
    {
      primary_item_id: 6,
      primary_item_name: '두피케어',
      category: 'SERVICE',
      subItems: [
        {
          secondary_item_id: 61,
          secondary_item_name: '스케일링',
          secondary_item_price: 40000,
          duration: 30,
        },
        {
          secondary_item_id: 62,
          secondary_item_name: '마사지',
          secondary_item_price: 50000,
          duration: 35,
        },
      ],
    },
    {
      primary_item_id: 7,
      primary_item_name: '기초화장품',
      category: 'PRODUCT',
      subItems: [
        { secondary_item_id: 71, secondary_item_name: '스킨', secondary_item_price: 18000 },
        { secondary_item_id: 72, secondary_item_name: '로션', secondary_item_price: 22000 },
      ],
    },
    {
      primary_item_id: 8,
      primary_item_name: '메이크업',
      category: 'SERVICE',
      subItems: [
        {
          secondary_item_id: 81,
          secondary_item_name: '데일리 메이크업',
          secondary_item_price: 60000,
          duration: 45,
        },
        {
          secondary_item_id: 82,
          secondary_item_name: '혼주 메이크업',
          secondary_item_price: 100000,
          duration: 60,
        },
      ],
    },
    {
      primary_item_id: 9,
      primary_item_name: '클렌징',
      category: 'PRODUCT',
      subItems: [
        { secondary_item_id: 91, secondary_item_name: '클렌징폼', secondary_item_price: 15000 },
        { secondary_item_id: 92, secondary_item_name: '클렌징오일', secondary_item_price: 20000 },
      ],
    },
    {
      primary_item_id: 10,
      primary_item_name: '속눈썹',
      category: 'SERVICE',
      subItems: [
        {
          secondary_item_id: 101,
          secondary_item_name: '속눈썹 펌',
          secondary_item_price: 30000,
          duration: 25,
        },
        {
          secondary_item_id: 102,
          secondary_item_name: '속눈썹 연장',
          secondary_item_price: 50000,
          duration: 40,
        },
      ],
    },
  ]);

  const primaryForm = ref({ category: 'SERVICE', primaryName: '' });
  const secondaryForm = ref({ primaryItemId: '', secondaryName: '', price: '' });
  const primaryEditForm = ref({ category: '', primaryName: '' });
  const secondaryEditForm = ref({ primaryItemId: '', secondaryName: '', price: '' });

  const primaryOptions = computed(() =>
    items.value.map(item => ({
      id: item.primary_item_id,
      name: item.primary_item_name,
      category: item.category,
    }))
  );

  const filteredItems = computed(() =>
    items.value.filter(
      item => item.category === (activeTab.value === '시술' ? 'SERVICE' : 'PRODUCT')
    )
  );

  const formatPrice = val => `${val.toLocaleString('ko-KR')} 원`;

  const toggleRegisterDropdown = () => (showRegisterDropdown.value = !showRegisterDropdown.value);
  const toggleDropdown = id => (showDropdownId.value = showDropdownId.value === id ? null : id);

  const openPrimaryRegisterModal = () => {
    showRegisterDropdown.value = false;
    showDropdownId.value = null;
    showRegisterDropdown.value = false;
    showPrimaryRegisterModal.value = true;
  };

  const openSecondaryRegisterModal = () => {
    showRegisterDropdown.value = false;
    showDropdownId.value = null;
    showRegisterDropdown.value = false;
    showSecondaryRegisterModal.value = true;
  };

  const openPrimaryEditModal = item => {
    primaryEditForm.value = { category: item.category, primaryName: item.primary_item_name };
    selectedProduct.value = item;
    showDropdownId.value = null;
    showRegisterDropdown.value = false;
    showPrimaryEditModal.value = true;
  };

  const openSecondaryEditModal = (item, sub) => {
    secondaryEditForm.value = {
      primaryItemId: item.primary_item_id,
      secondaryName: sub.secondary_item_name,
      price: sub.secondary_item_price,
    };
    showDropdownId.value = null;
    showRegisterDropdown.value = false;
    selectedProduct.value = sub;
    showSecondaryEditModal.value = true;
  };

  const deletePrimaryItem = item => {
    showRegisterDropdown.value = false;
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

  .clickable {
    cursor: pointer;
  }

  .text-right {
    text-align: right;
  }

  .relative {
    position: relative;
    display: inline-block;
  }

  .icon-button {
    background: none;
    border: none;
    font-size: 25px;
    cursor: pointer;
  }

  .dropdown-menu {
    position: absolute;
    min-width: 120px;
    background-color: #fff;
    border: 1px solid #364f6b;
    border-radius: 12px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    z-index: 1000;
    padding: 0;
  }

  .dropdown-menu.below-left {
    top: calc(100% + 4px);
    right: 0;
  }

  .dropdown-item {
    padding: 10px 16px;
    font-size: 14px;
    color: #364f6b;
    background-color: white;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .dropdown-item:hover {
    background-color: #f5f5f5;
  }

  .card-title-row {
    font-size: 20px;
    font-weight: bold;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
</style>
