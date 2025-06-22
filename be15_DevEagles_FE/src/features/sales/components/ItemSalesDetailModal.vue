<template>
  <div class="overlay" @click.self="emit('close')">
    <div class="modal-panel">
      <!-- 상단 헤더 -->
      <div class="top-bar">
        <div class="info">
          <h2 class="title">상품 매출 상세 조회</h2>
        </div>
        <div class="actions">
          <BaseButton class="close-button" @click="emit('close')">&times;</BaseButton>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 양쪽 레이아웃 -->
      <div class="register-body">
        <!-- 좌측 -->
        <div class="form-left scrollable-left">
          <div class="receipt-box">
            <div class="receipt-header">
              <h3 class="receipt-title">결제 내역서</h3>
              <p class="receipt-sub">{{ date }} {{ time }}</p>
            </div>

            <div v-for="(item, index) in selectedItems" :key="index" class="sales-row">
              <div class="row-left">
                <strong>{{ item.item }}</strong> / 1개 / {{ item.staff || '담당자 없음' }}
              </div>
              <div class="row-right">
                <input type="checkbox" disabled :checked="false" />
                {{ formatPrice(item.salesTotal) }} - {{ formatPrice(item.discount || 0) }} =
                <strong>{{ formatPrice(item.netSales) }}</strong>
              </div>
            </div>

            <div class="receipt-row">
              <span>총 영업액</span>
              <span>{{ formatPrice(totalAmount) }}</span>
            </div>
            <div class="receipt-row">
              <span>[결제] {{ paymentLabel }}</span>
              <span>{{ formatPrice(totalAmount) }}</span>
            </div>
            <div class="receipt-row">
              <span>메모</span>
              <span>{{ memo || '비고 없음' }}</span>
            </div>
          </div>
        </div>

        <!-- 우측 -->
        <div class="form-right">
          <div class="search-row">
            <BaseForm
              type="text"
              :model-value="selectedItems[0]?.customer || ''"
              label="고객명"
              readonly
            />
          </div>

          <div class="footer-buttons">
            <BaseButton class="primary" outline @click="emit('close')">닫기</BaseButton>
            <div ref="dropdownRef" class="dropdown-wrapper">
              <BaseButton class="primary" @click="toggleDropdown">매출 수정</BaseButton>
              <div v-if="dropdownVisible" class="dropdown-menu">
                <BaseButton class="primary" outline @click="handleAction('refund')"
                  >매출 환불</BaseButton
                >
                <BaseButton class="primary" outline @click="openEditModal">매출 수정</BaseButton>
                <BaseButton class="primary" outline @click="handleAction('delete')"
                  >매출 삭제</BaseButton
                >
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 모달 연결 -->
      <ItemSalesDeleteModal v-model="showDeleteModal" @confirm="handleDeleteConfirm" />
      <ItemSalesRefundModal v-model="showRefundModal" @confirm="handleRefundConfirm" />
      <ItemSalesEditModal
        v-if="showEditModal"
        :initial-items="[selectedItems[0]]"
        :initial-memo="memo"
        :initial-date="date"
        :initial-time="time"
        @close="handleEditConfirm"
      />

      <!-- ✅ 토스트 컴포넌트 추가 -->
      <BaseToast ref="toastRef" />
    </div>
  </div>
</template>

<script setup>
  import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import ItemSalesDeleteModal from '@/features/sales/components/ItemSalesDeleteModal.vue';
  import ItemSalesRefundModal from '@/features/sales/components/ItemSalesRefundModal.vue';
  import ItemSalesEditModal from '@/features/sales/components/ItemSalesEditModal.vue';
  import '@/features/sales/styles/SalesDetailModal.css';

  const props = defineProps({ salesItem: Object });
  const emit = defineEmits(['close']);

  const dropdownVisible = ref(false);
  const showDeleteModal = ref(false);
  const showRefundModal = ref(false);
  const showEditModal = ref(false);
  const dropdownRef = ref(null);
  const toastRef = ref(null);

  const date = ref('');
  const time = ref('');
  const memo = ref('');
  const selectedMethod = ref('card');
  const selectedItems = ref([]);

  const mapMethodToKey = label => {
    const map = {
      카드: 'card',
      현금: 'cash',
      네이버페이: 'naver',
      지역화폐: 'local',
      계좌이체: 'transfer',
    };
    return map[label] || 'card';
  };

  watch(
    () => props.salesItem,
    val => {
      if (!val) return;
      const [d, t] = val.date.split(' ');
      date.value = d;
      time.value = t;
      memo.value = val.memo;
      selectedMethod.value = mapMethodToKey(val.paymentMethod);
      selectedItems.value = [val];
    },
    { immediate: true }
  );

  const paymentLabel = computed(() => {
    const method = {
      card: '카드 결제',
      cash: '현금 결제',
      naver: '네이버페이',
      local: '지역화폐',
      transfer: '계좌이체',
    };
    return method[selectedMethod.value] || '결제수단';
  });

  const totalAmount = computed(() =>
    selectedItems.value.reduce((sum, m) => sum + (m.netSales || 0), 0)
  );

  const toggleDropdown = () => {
    dropdownVisible.value = !dropdownVisible.value;
  };

  const handleClickOutside = e => {
    if (dropdownRef.value && !dropdownRef.value.contains(e.target)) {
      dropdownVisible.value = false;
    }
  };

  onMounted(() => {
    document.addEventListener('click', handleClickOutside);
  });

  onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside);
  });

  const handleAction = type => {
    dropdownVisible.value = false;
    switch (type) {
      case 'delete':
        showDeleteModal.value = true;
        break;
      case 'refund':
        showRefundModal.value = true;
        break;
    }
  };

  const openEditModal = () => {
    showEditModal.value = true;
  };

  const handleEditConfirm = () => {
    toastRef.value?.success('상품 매출이 수정되었습니다.');
    showEditModal.value = false;
  };

  const handleDeleteConfirm = () => {
    toastRef.value?.success('상품 매출이 삭제되었습니다.');
    showDeleteModal.value = false;
  };

  const handleRefundConfirm = () => {
    toastRef.value?.success('상품 매출이 환불되었습니다.');
    showRefundModal.value = false;
  };

  const formatPrice = val => (val || 0).toLocaleString('ko-KR') + '원';
</script>
