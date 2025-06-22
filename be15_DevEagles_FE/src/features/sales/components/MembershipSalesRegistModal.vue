<template>
  <div class="overlay" @click.self="emit('close')">
    <div class="modal-panel">
      <!-- 상단 헤더 -->
      <div class="top-bar">
        <div class="info">
          <h2 class="title">회원권 매출 등록</h2>
        </div>
        <div class="actions">
          <button class="close-button" @click="emit('close')">&times;</button>
        </div>
      </div>

      <div class="divider"></div>

      <div class="register-body">
        <!-- 좌측 -->
        <div class="form-left scrollable-left">
          <div class="datetime-row">
            <div class="date-time-group">
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
            <BaseButton class="select-button" @click="selectMembership">회원권 선택</BaseButton>
          </div>

          <div
            class="membership-box"
            :class="{ clickable: !selectedMembership }"
            @click="!selectedMembership && selectMembership()"
          >
            <template v-if="!selectedMembership">
              <p class="placeholder">영역을 클릭해<br />판매할 회원권을 선택해주세요.</p>
            </template>
            <template v-else>
              <div class="selected-detail">
                <div class="product-header">
                  <p>
                    <strong>{{ selectedMembership.name }}</strong> ({{
                      formatPrice(selectedMembership.price)
                    }})
                  </p>
                  <button class="x-button" @click.stop="clearMembership">×</button>
                </div>
                <div class="grid-container">
                  <div class="input-group">
                    <label>수량</label>
                    <BaseForm v-model.number="selectedMembership.quantity" type="number" min="1" />
                  </div>
                  <div class="input-group">
                    <label>가격</label>
                    <BaseForm v-model.number="selectedMembership.price" type="number" />
                  </div>
                  <div class="input-group">
                    <label>제공 혜택</label>
                    <BaseForm v-model="selectedMembership.benefit" type="text" />
                  </div>
                  <!-- PREPAID 타입 -->
                  <div v-if="selectedMembership.type === 'PREPAID'" class="input-group">
                    <label>충전 금액</label>
                    <BaseForm v-model.number="selectedMembership.chargeAmount" type="number" />
                  </div>
                  <div v-if="selectedMembership.type === 'PREPAID'" class="input-group">
                    <label>잔여 금액</label>
                    <BaseForm v-model.number="selectedMembership.remainAmount" type="number" />
                  </div>

                  <!-- COUNT 타입 -->
                  <div v-if="selectedMembership.type === 'COUNT'" class="input-group">
                    <label>충전 횟수</label>
                    <BaseForm v-model.number="selectedMembership.totalCount" type="number" />
                  </div>
                  <div v-if="selectedMembership.type === 'COUNT'" class="input-group">
                    <label>잔여 횟수</label>
                    <BaseForm v-model.number="selectedMembership.remainCount" type="number" />
                  </div>
                  <div class="input-group">
                    <label>유효 기간</label>
                    <PrimeDatePicker v-model="date" class="date-picker" />
                  </div>
                  <div class="input-group">
                    <label>담당자</label>
                    <BaseForm v-model="selectedMembership.manager" type="text" />
                  </div>
                </div>
              </div>
            </template>
          </div>

          <label class="memo-label">메모</label>
          <BaseForm v-model="memo" type="textarea" placeholder="메모 입력" />
        </div>

        <!-- 우측 -->
        <div class="form-right">
          <div class="search-row">
            <BaseForm type="text" class="customer-search" placeholder="고객명 또는 연락처 검색" />
          </div>

          <div class="total-price-section">
            <label>결제 금액</label>
            <div class="total-display">
              <div class="price-box">
                {{ selectedMembership ? formatPrice(selectedMembership.price) : '가격' }}
              </div>
            </div>
          </div>

          <div v-for="method in methods" :key="method.key" class="method-group">
            <!-- 왼쪽: 체크박스 라벨 -->
            <div class="checkbox-label">
              <BaseForm
                :id="`payment-${method.key}`"
                v-model="selectedMethods"
                type="checkbox"
                :options="[{ value: method.key, text: method.label }]"
              />
            </div>

            <!-- 오른쪽: 금액 입력 필드 -->
            <BaseForm
              v-model.number="paymentAmounts[method.key]"
              type="number"
              class="method-price"
              placeholder="금액"
              :disabled="!selectedMethods.includes(method.key)"
            />
          </div>

          <div class="footer-buttons">
            <BaseButton class="cancel-button" outline @click="emit('close')">닫기</BaseButton>
            <BaseButton class="submit-button" @click="submit">등록</BaseButton>
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
  import { ref } from 'vue';
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

  const selectedMethods = ref([]); // 다중 선택 가능
  const paymentAmounts = ref({}); // 각 결제수단에 입력된 금액

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
    selectedMembership.value = {
      ...item,
      quantity: 1,
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
</script>
