<template>
  <div class="overlay" @click.self="emit('close')">
    <div class="modal-panel">
      <!-- 상단 헤더 -->
      <div class="top-bar">
        <div class="info">
          <h2 class="title">환불 내역 상세 조회</h2>
        </div>
        <div class="actions">
          <BaseButton class="close-button" @click="emit('close')">&times;</BaseButton>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 양쪽 레이아웃 -->
      <div class="register-body">
        <!-- 좌측: 환불 내역 -->
        <div class="form-left scrollable-left">
          <div class="receipt-box">
            <div class="receipt-header">
              <h3 class="receipt-title">환불 내역서</h3>
              <p class="receipt-sub">{{ date }} {{ time }}</p>
            </div>

            <div v-for="(item, index) in selectedMemberships" :key="index" class="sales-row">
              <div class="row-left">
                <strong>{{ item.item }}</strong> / 1개 / {{ item.staff || '담당자 없음' }}
              </div>
              <div class="row-right">
                {{ formatPrice(item.salesTotal) }}
              </div>
            </div>

            <div class="receipt-row">
              <span>총 환불액</span>
              <span>{{ formatPrice(totalAmount) }}</span>
            </div>
            <div class="receipt-row">
              <span>[환불] {{ paymentLabel }}</span>
              <span>{{ formatPrice(totalAmount) }}</span>
            </div>
            <div class="receipt-row">
              <span>메모</span>
              <span>{{ memo || '비고 없음' }}</span>
            </div>
          </div>
        </div>

        <!-- 우측: 고객명 표시 및 닫기 버튼 -->
        <div class="form-right">
          <div class="search-row">
            <BaseForm
              type="text"
              :model-value="selectedMemberships[0]?.customer || ''"
              label="고객명"
              readonly
            />
          </div>

          <!-- 버튼 영역 -->
          <div class="footer-buttons">
            <BaseButton class="primary" outline @click="emit('close')">닫기</BaseButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, computed, watch } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import '@/features/sales/styles/SalesDetailModal.css';

  const props = defineProps({ salesItem: Object });
  const emit = defineEmits(['close', 'edit', 'delete', 'refund']);

  const date = ref('');
  const time = ref('');
  const memo = ref('');
  const selectedMethod = ref('card');
  const selectedMemberships = ref([]);

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
      selectedMemberships.value = [val];
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
    selectedMemberships.value.reduce((sum, m) => sum + (m.netSales || 0), 0)
  );

  const formatPrice = val => (val || 0).toLocaleString('ko-KR') + '원';
</script>
