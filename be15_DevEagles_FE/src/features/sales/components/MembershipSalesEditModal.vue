<template>
  <div class="overlay" @click.self="emit('close')">
    <div class="modal-panel">
      <!-- 상단 헤더 -->
      <div class="top-bar">
        <div class="info">
          <h2 class="title">회원권 매출 수정</h2>
        </div>
        <div class="actions">
          <button class="close-button" @click="emit('close')">&times;</button>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 양쪽 레이아웃 -->
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
          </div>

          <div class="membership-box">
            <div class="selected-detail">
              <div class="product-header">
                <p>
                  <strong>{{ selectedMembership.name }}</strong> ({{
                    formatPrice(selectedMembership.price)
                  }})
                </p>
              </div>
              <div class="grid-container">
                <BaseForm v-model="selectedMembership.quantity" label="수량" type="number" />
                <BaseForm v-model="selectedMembership.price" label="가격" type="number" />
                <BaseForm v-model="selectedMembership.benefit" label="제공 혜택" type="text" />

                <!-- PREPAID -->
                <BaseForm
                  v-if="selectedMembership.type === 'PREPAID'"
                  v-model="selectedMembership.chargeAmount"
                  label="충전 금액"
                  type="number"
                />
                <BaseForm
                  v-if="selectedMembership.type === 'PREPAID'"
                  v-model="selectedMembership.remainAmount"
                  label="잔여 금액"
                  type="number"
                />

                <!-- COUNT -->
                <BaseForm
                  v-if="selectedMembership.type === 'COUNT'"
                  v-model="selectedMembership.totalCount"
                  label="충전 횟수"
                  type="number"
                />
                <BaseForm
                  v-if="selectedMembership.type === 'COUNT'"
                  v-model="selectedMembership.remainCount"
                  label="잔여 횟수"
                  type="number"
                />

                <BaseForm v-model="selectedMembership.expireDate" label="유효 기간" type="date" />
                <BaseForm v-model="selectedMembership.manager" label="담당자" type="text" />
              </div>
            </div>
          </div>

          <BaseForm v-model="memo" label="메모" type="textarea" />
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
            <BaseButton type="primary" outline @click="emit('close')">닫기</BaseButton>
            <BaseButton type="primary" @click="submit">수정</BaseButton>
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
  import MembershipSelectModal from '@/features/sales/components/MembershipSelectModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import '@/features/sales/styles/SalesMembershipModal.css';

  const props = defineProps({
    initialMembership: Object,
    initialDate: String,
    initialTime: String,
    initialMemo: String,
    initialPayments: Array, // [{ method: 'card', amount: 10000 }]
  });

  const emit = defineEmits(['close', 'submit']);

  const date = ref(props.initialDate);
  const time = ref(props.initialTime);
  const memo = ref(props.initialMemo);
  const selectedMembership = ref({ ...props.initialMembership });
  const showSelectModal = ref(false);

  const methods = ref([
    { key: 'card', label: '카드 결제' },
    { key: 'cash', label: '현금 결제' },
    { key: 'naver', label: '네이버페이' },
    { key: 'local', label: '지역화폐' },
  ]);

  const selectedMethods = ref(props.initialPayments?.map(p => p.method) || []);
  const paymentAmounts = ref(
    Object.fromEntries((props.initialPayments || []).map(p => [p.method, p.amount]))
  );

  const handleMembershipSelected = item => {
    selectedMembership.value = {
      ...item,
      quantity: 1,
    };
    showSelectModal.value = false;
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
