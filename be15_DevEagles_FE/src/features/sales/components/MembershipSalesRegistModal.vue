<template>
  <div class="mem-overlay" @click.self="emit('close')">
    <div class="mem-modal-panel">
      <!-- 상단 헤더 -->
      <div class="mem-top-bar">
        <div class="mem-info">
          <h2 class="mem-title">회원권 매출 등록</h2>
        </div>
        <div class="mem-actions">
          <button class="mem-close-button" @click="emit('close')">&times;</button>
        </div>
      </div>

      <div class="mem-divider"></div>

      <div class="mem-register-body">
        <!-- 좌측 -->
        <div class="mem-form-left scrollable-left">
          <div class="mem-datetime-row">
            <div class="mem-date-time-group">
              <PrimeDatePicker v-model="date" placeholder="날짜 선택" class="date-picker" />
              <PrimeDatePicker
                v-model="time"
                placeholder="시간 선택"
                time-only
                show-time
                hour-format="24"
                class="time-picker"
              />
            </div>
            <BaseButton class="mem-select-button" @click="selectMembership">회원권 선택</BaseButton>
          </div>

          <div
            class="mem-membership-box"
            :class="{ clickable: !selectedMembership }"
            @click="!selectedMembership && selectMembership()"
          >
            <template v-if="!selectedMembership">
              <p class="mem-placeholder">영역을 클릭해<br />판매할 회원권을 선택해주세요.</p>
            </template>
            <template v-else>
              <div class="mem-selected-detail">
                <div class="mem-product-header">
                  <p>
                    <strong>{{ selectedMembership.name }}</strong>
                    ({{ formatPrice(selectedMembership.price) }})
                  </p>
                  <button class="mem-x-button" @click.stop="clearMembership">×</button>
                </div>
                <div class="mem-grid-container">
                  <div class="mem-input-group">
                    <label>수량</label>
                    <BaseForm v-model.number="selectedMembership.quantity" type="number" min="1" />
                  </div>
                  <div class="mem-input-group">
                    <label>가격</label>
                    <BaseForm v-model.number="selectedMembership.price" type="number" />
                  </div>
                  <div class="mem-input-group">
                    <label>제공 혜택</label>
                    <BaseForm v-model="selectedMembership.benefit" type="text" />
                  </div>
                  <div v-if="selectedMembership.type === 'PREPAID'" class="mem-input-group">
                    <label>충전 금액</label>
                    <BaseForm v-model.number="selectedMembership.chargeAmount" type="number" />
                  </div>
                  <div v-if="selectedMembership.type === 'PREPAID'" class="mem-input-group">
                    <label>잔여 금액</label>
                    <BaseForm v-model.number="selectedMembership.remainAmount" type="number" />
                  </div>
                  <div v-if="selectedMembership.type === 'SESSION'" class="mem-input-group">
                    <label>충전 횟수</label>
                    <BaseForm v-model.number="selectedMembership.totalSESSION" type="number" />
                  </div>
                  <div v-if="selectedMembership.type === 'SESSION'" class="mem-input-group">
                    <label>잔여 횟수</label>
                    <BaseForm v-model.number="selectedMembership.remainSESSION" type="number" />
                  </div>
                  <div class="mem-input-group">
                    <label>유효 기간</label>
                    <PrimeDatePicker
                      v-if="selectedMembership"
                      v-model="selectedMembership.expirationDate"
                      class="date-picker"
                    />
                  </div>
                  <div class="mem-input-group">
                    <label>담당자</label>
                    <BaseForm v-model="selectedMembership.manager" type="text" />
                  </div>
                </div>
              </div>
            </template>
          </div>

          <label class="mem-memo-label">메모</label>
          <BaseForm v-model="memo" type="textarea" placeholder="메모 입력" class="mem-textarea" />
        </div>

        <div class="mem-form-right">
          <!-- 우측 바디 -->
          <div class="mem-form-right-body">
            <div class="mem-search-row">
              <BaseForm
                type="text"
                class="mem-customer-search"
                placeholder="고객명 또는 연락처 검색"
              />
            </div>

            <div class="mem-total-price-section">
              <label>결제 금액</label>
              <div class="mem-total-display">
                <div class="mem-price-box">
                  {{ selectedMembership ? formatPrice(selectedMembership.price) : '가격' }}
                </div>
              </div>
            </div>

            <div v-for="method in methods" :key="method.key" class="mem-method-group">
              <div class="mem-checkbox-label">
                <BaseForm
                  :id="`payment-${method.key}`"
                  v-model="selectedMethods"
                  type="checkbox"
                  :options="[{ value: method.key, text: method.label }]"
                />
              </div>
              <BaseForm
                v-model.number="paymentAmounts[method.key]"
                type="number"
                class="mem-method-price"
                placeholder="금액"
                :disabled="!selectedMethods.includes(method.key)"
                :class="{ disabled: !selectedMethods.includes(method.key) }"
              />
            </div>
          </div>

          <!-- 우측 하단 푸터 -->
          <div class="mem-form-right-footer">
            <div class="mem-footer-buttons">
              <BaseButton class="mem-cancel-button" outline @click="emit('close')">닫기</BaseButton>
              <BaseButton class="mem-submit-button" @click="submit">등록</BaseButton>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <Teleport to="body">
    <MembershipSelectModal
      v-if="showSelectModal"
      v-model="showSelectModal"
      @apply="handleMembershipSelected"
    />
  </Teleport>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import MembershipSelectModal from '@/features/sales/components/MembershipSelectModal.vue';
  import '@/features/sales/styles/SalesMembershipModal.css';

  const emit = defineEmits(['close', 'submit']);

  const date = ref(new Date().toISOString().substring(0, 10));
  const time = ref(new Date().toTimeString().substring(0, 5));
  const selectedMembership = ref(null);
  const memo = ref('');
  const showSelectModal = ref(false);

  const selectedMethods = ref([]);
  const paymentAmounts = ref({});

  const methods = ref([
    { key: 'card', label: '카드 결제' },
    { key: 'cash', label: '현금 결제' },
    { key: 'naver', label: '네이버페이' },
    { key: 'local', label: '지역화폐' },
  ]);

  const selectMembership = () => {
    showSelectModal.value = true;
  };

  const handleMembershipSelected = item => {
    const hasBonus = item.bonus != null && !isNaN(item.bonus);
    const hasDiscount = item.discountRate != null && !isNaN(item.discountRate);

    const benefit =
      hasBonus && hasDiscount
        ? `(추가제공) ${item.bonus}, (할인) ${item.discountRate}%`
        : hasBonus
          ? `(추가제공) ${item.bonus}`
          : hasDiscount
            ? `(할인) ${item.discountRate}%`
            : null;

    const discountedPrice = hasDiscount
      ? Math.floor(item.prepaidPassPrice * (1 - item.discountRate / 100))
      : item.prepaidPassPrice;

    const chargeAmount = hasBonus ? item.prepaidPassPrice + item.bonus : item.prepaidPassPrice;

    const now = new Date();
    const expirationDate = new Date(now);

    switch (item.expirationPeriodType) {
      case 'DAY':
        expirationDate.setDate(now.getDate() + item.expirationPeriod);
        break;
      case 'WEEK':
        expirationDate.setDate(now.getDate() + item.expirationPeriod * 7);
        break;
      case 'MONTH':
        expirationDate.setMonth(now.getMonth() + item.expirationPeriod);
        break;
      case 'YEAR':
        expirationDate.setFullYear(now.getFullYear() + item.expirationPeriod);
        break;
    }

    const formattedExpiration = expirationDate.toISOString().substring(0, 10);

    selectedMembership.value = {
      id: item.prepaidPassId,
      name: item.prepaidPassName,
      price: discountedPrice,
      chargeAmount,
      remainAmount: chargeAmount,
      benefit,
      expirationPeriod: item.expirationPeriod,
      expirationPeriodType: item.expirationPeriodType,
      expirationDate: formattedExpiration,
      memo: item.prepaidPassMemo,
      quantity: 1,
      type: item.type,
      manager: '',
    };

    showSelectModal.value = false;
  };

  const clearMembership = () => {
    selectedMembership.value = null;
  };

  const submit = () => {
    const payments = selectedMethods.value.map(key => ({
      method: key,
      amount: paymentAmounts.value[key] || 0,
    }));

    emit('submit', {
      date: date.value,
      time: time.value,
      membership: selectedMembership.value,
      memo: memo.value,
      payments,
    });

    emit('close');
  };

  const formatPrice = val => val?.toLocaleString('ko-KR') + '원';

  watch([selectedMembership, selectedMethods], ([membership, selected]) => {
    if (!membership) return;

    const total = membership.price || 0;

    // 자동 입력: 결제수단 1개만 선택됐을 때 자동 채움
    if (selected.length === 1) {
      paymentAmounts.value[selected[0]] = total;
    }

    // 체크 해제된 항목은 제거
    Object.keys(paymentAmounts.value).forEach(key => {
      if (!selected.includes(key)) {
        delete paymentAmounts.value[key];
      }
    });
  });
</script>
