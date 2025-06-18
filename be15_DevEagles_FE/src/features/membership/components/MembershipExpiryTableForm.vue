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
  import { ref, computed } from 'vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import CustomerMembershipDetailModal from '@/features/membership/components/CustomerMembershipDetailModal.vue';

  const props = defineProps({
    loading: { type: Boolean, default: false },
    type: { type: String, default: '선불' }, // or '횟수권'
  });

  const searchKeyword = ref('');
  const filterState = ref(null);
  const detailModalVisible = ref(false);
  const selectedCustomer = ref(null);

  const openModal = item => {
    selectedCustomer.value = item;
    detailModalVisible.value = true;
  };

  const rawData = computed(() =>
    props.type === '횟수권'
      ? [
          {
            id: 1,
            name: '유관순',
            phone: '010-0000-0000',
            membershipName: '시술 10회 이용권',
            remaining: 10,
            used: 5,
            expiryDate: '2025-06-20',
            memberships: [
              {
                id: 11,
                name: '시술 10회 이용권',
                type: 'COUNT',
                remaining: 5,
                create: '2024-04-01',
                expiry: '2025-06-20',
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
        ]
      : [
          {
            id: 2,
            name: '홍길동',
            phone: '010-1234-5678',
            membershipName: '선불권 5만원',
            remaining: 50000,
            used: 20000,
            expiryDate: '2025-07-10',
            memberships: [
              {
                id: 21,
                name: '선불권 5만원',
                type: 'PREPAID',
                remaining: 50000,
                create: '2024-01-15',
                expiry: '2025-07-10',
              },
            ],
            usageHistory: [
              {
                id: 2,
                date: '2025-06-10',
                detail: '사용 2만원',
                change: '-20000원',
              },
            ],
          },
        ]
  );

  const filteredData = computed(() => {
    const base = rawData.value.filter(m => m.name.includes(searchKeyword.value.trim()));
    if (!filterState.value) return base;

    const { min, max, startDate, endDate } = filterState.value || {};
    return base.filter(m => {
      const inRange = (!min || m.remaining >= min) && (!max || m.remaining <= max);
      const inDate =
        (!startDate || m.expiryDate >= startDate) && (!endDate || m.expiryDate <= endDate);
      return inRange && inDate;
    });
  });

  const columns = computed(() =>
    props.type === '선불'
      ? [
          { key: 'name', title: '이름', width: '50px' },
          { key: 'phone', title: '연락처', width: '70px' },
          { key: 'membershipName', title: '선불권명', width: '180px' },
          { key: 'remaining', title: '잔여', width: '180px' },
          { key: 'expiryDate', title: '만료일', width: '160px' },
        ]
      : [
          { key: 'name', title: '이름', width: '50px' },
          { key: 'phone', title: '연락처', width: '70px' },
          { key: 'membershipName', title: '횟수권명', width: '180px' },
          { key: 'remaining', title: '잔여', width: '180px' },
          { key: 'expiryDate', title: '만료일', width: '160px' },
        ]
  );

  const isExpiringSoon = dateStr => {
    const today = new Date();
    const expiry = new Date(dateStr);
    const diff = (expiry - today) / (1000 * 60 * 60 * 24);
    return diff >= 0 && diff <= 7;
  };
</script>
