<template>
  <BaseTable
    :columns="columns"
    :data="filteredData"
    :striped="true"
    :hover="true"
    :loading="loading"
    row-key="id"
  >
    <!-- 이름 클릭 시 모달 열기 -->
    <template #cell-name="{ value, item }">
      <span class="text-blue-500 underline cursor-pointer" @click="openModal(item)">
        {{ value }}
      </span>
    </template>

    <!-- 만료일 강조 표시 -->
    <template #cell-expiryDate="{ value }">
      <span :class="{ 'text-red-500': isExpiringSoon(value) }">
        {{ value }}
      </span>
    </template>
  </BaseTable>

  <!-- 상세 모달 -->
  <CustomerMembershipDetailModal
    v-if="detailModalVisible"
    v-model="detailModalVisible"
    :customer="selectedCustomer"
  />
</template>

<script setup>
  import BaseTable from '@/components/common/BaseTable.vue';
  import CustomerMembershipDetailModal from '@/features/membership/components/CustomerMembershipDetailModal.vue';
  import { ref, computed } from 'vue';

  const props = defineProps({
    loading: {
      type: Boolean,
      default: false,
    },
  });

  const searchKeyword = ref('');
  const filterState = ref(null);

  const fullList = ref([
    {
      id: 1,
      name: '유관순',
      phone: '010-0000-0000',
      membershipName: '선불권',
      remaining: 50000,
      used: '(상품) 시술 10회 이용권',
      expiryDate: '2025-06-20',
      memberships: [
        {
          id: 1,
          name: '선불권 10만원권',
          type: 'PREPAID',
          remaining: 50000,
          create: '2024-06-01',
          expiry: '2025-06-20',
        },
        {
          id: 2,
          name: '시술 10회 이용권',
          type: 'COUNT',
          remaining: 8,
          create: '2024-04-01',
          expiry: '2024-12-01',
        },
      ],
      usageHistory: [
        {
          id: 1,
          date: '2025-06-15',
          detail: '시술 1회 사용',
          change: '-1회',
        },
      ],
    },
    {
      id: 2,
      name: '홍길동',
      phone: '010-1234-5678',
      membershipName: '선불권2',
      remaining: 0,
      used: '(상품) 시술 15회 이용권',
      expiryDate: '2025-07-10',
      memberships: [
        {
          id: 3,
          name: '선불권 5만원권',
          type: 'PREPAID',
          remaining: 0,
          create: '2024-01-15',
          expiry: '2025-07-10',
        },
        {
          id: 4,
          name: '시술 15회 이용권',
          type: 'COUNT',
          remaining: 2,
          create: '2024-01-01',
          expiry: '2024-12-31',
        },
      ],
      usageHistory: [
        {
          id: 1,
          date: '2025-06-10',
          detail: '시술 3회 사용',
          change: '-3회',
        },
      ],
    },
  ]);

  const filteredData = computed(() => {
    let base = fullList.value.filter(member => member.name.includes(searchKeyword.value.trim()));

    if (!filterState.value) return base;

    const { min, max } = filterState.value;
    return base.filter(
      m => (min == null || m.remaining >= min) && (max == null || m.remaining <= max)
    );
  });

  const columns = computed(() => [
    { key: 'name', title: '이름', width: '50px' },
    { key: 'phone', title: '연락처', width: '70px' },
    { key: 'remaining', title: '잔여선불권', width: '70px' },
    { key: 'used', title: '보유횟수권', width: '370px' },
  ]);

  const isExpiringSoon = dateStr => {
    const today = new Date();
    const expiry = new Date(dateStr);
    const diff = (expiry - today) / (1000 * 60 * 60 * 24);
    return diff >= 0 && diff <= 7;
  };

  const detailModalVisible = ref(false);
  const selectedCustomer = ref(null);

  const openModal = item => {
    selectedCustomer.value = item;
    detailModalVisible.value = true;
  };
</script>
