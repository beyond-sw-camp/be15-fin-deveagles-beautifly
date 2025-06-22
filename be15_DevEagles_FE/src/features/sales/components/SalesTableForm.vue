<template>
  <div class="base-table-wrapper">
    <!-- 매출 테이블 -->
    <BaseTable :columns="columns" :data="filteredSales" striped @row-click="openModal" />

    <!-- 상품 상세 모달 -->
    <ItemSalesDetailModal
      v-if="detailModalVisible && selectedSalesItem?.type === '상품'"
      :sales-item="selectedSalesItem"
      @close="closeModal"
    />

    <!-- 회원권 상세 모달 -->
    <MembershipSalesDetailModal
      v-if="detailModalVisible && selectedSalesItem?.type === '회원권'"
      :sales-item="selectedSalesItem"
      @close="closeModal"
    />

    <!-- 환불 상세 모달 -->
    <RefundDetailModal
      v-if="detailModalVisible && selectedSalesItem?.type === '환불'"
      :sales-item="selectedSalesItem"
      @close="closeModal"
    />
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import ItemSalesDetailModal from '@/features/sales/components/ItemSalesDetailModal.vue';
  import MembershipSalesDetailModal from '@/features/sales/components/MembershipSalesDetailModal.vue';
  import RefundDetailModal from '@/features/sales/components/RefundDetailModal.vue';

  const props = defineProps({
    searchKeyword: String,
    filterState: Object,
  });

  const allSales = ref([
    {
      date: '2025-07-01 09:00',
      type: '상품',
      item: '샴푸',
      staff: '김경민',
      customer: '홍길동',
      memo: '여름용',
      productType: '상품',
      salesTotal: 20000,
      discount: 2000,
      netSales: 18000,
      paymentMethod: '카드',
    },
    {
      date: '2025-07-02 10:00',
      type: '회원권',
      item: '이용권',
      staff: '김민지',
      customer: '박보검',
      memo: '정기 등록',
      productType: '패키지',
      salesTotal: 100000,
      discount: 10000,
      netSales: 90000,
      paymentMethod: '현금',
    },
    {
      date: '2025-07-03 12:00',
      type: '환불',
      item: '스파',
      staff: '홍길동',
      customer: '아이유',
      memo: '변심',
      productType: '시술',
      salesTotal: -50000,
      discount: 0,
      netSales: -50000,
      paymentMethod: '계좌이체',
    },
  ]);

  const parseDate = str => new Date(str);

  const filteredSales = computed(() => {
    return allSales.value.filter(item => {
      const keyword = props.searchKeyword?.toLowerCase() || '';
      const filter = props.filterState || {};

      const matchKeyword =
        keyword === '' ||
        item.customer.toLowerCase().includes(keyword) ||
        item.memo?.toLowerCase().includes(keyword);

      const matchDate =
        !filter.startDate ||
        !filter.endDate ||
        (parseDate(item.date) >= new Date(filter.startDate) &&
          parseDate(item.date) <= new Date(filter.endDate));

      const matchType = !filter.types?.length || filter.types.includes(item.type);
      const matchProduct = !filter.products?.length || filter.products.includes(item.productType);
      const matchStaff = !filter.staff || filter.staff === item.staff;

      return matchKeyword && matchDate && matchType && matchProduct && matchStaff;
    });
  });

  const columns = [
    { key: 'date', title: '판매일시', width: '160px' },
    { key: 'type', title: '매출 유형', width: '100px' },
    { key: 'staff', title: '담당자', width: '100px' },
    { key: 'customer', title: '고객명', width: '100px' },
    { key: 'item', title: '판매목록', width: '150px' },
    { key: 'salesTotal', title: '총 영업액', width: '100px' },
    { key: 'discount', title: '할인액', width: '100px' },
    { key: 'netSales', title: '실매출액', width: '100px' },
    { key: 'paymentMethod', title: '결제수단', width: '100px' },
  ];

  const detailModalVisible = ref(false);
  const selectedSalesItem = ref(null);

  const openModal = item => {
    selectedSalesItem.value = item;
    detailModalVisible.value = true;
  };

  const closeModal = () => {
    detailModalVisible.value = false;
    selectedSalesItem.value = null;
  };
</script>

<style>
  .base-table-wrapper {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    padding: 24px;
    margin-bottom: 24px;
  }
</style>
