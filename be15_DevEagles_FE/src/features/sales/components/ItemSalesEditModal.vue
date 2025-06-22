<template>
  <div class="overlay" @click.self="emit('close')">
    <div class="modal-panel">
      <div class="top-bar">
        <div class="info">
          <h2 class="title">상품 매출 수정</h2>
        </div>
        <div class="actions">
          <BaseButton class="close-button" @click="emit('close')">&times;</BaseButton>
        </div>
      </div>

      <div class="divider"></div>

      <div class="register-body">
        <!-- 좌측 -->
        <div class="form-left scrollable-left">
          <!-- 날짜/시간 -->
          <div class="datetime-row">
            <div class="date-time-group">
              <PrimeDatePicker v-model="date" class="date-picker" />
              <PrimeDatePicker
                v-model="time"
                time-only
                show-time
                hour-format="24"
                class="time-picker"
              />
            </div>
          </div>

          <!-- 상품 리스트 -->
          <div class="receipt-box">
            <template v-if="selectedItems.length === 0">
              <p class="placeholder"><span class="icon">➕</span><br />수정할 상품이 없습니다.</p>
            </template>
            <template v-else>
              <div v-for="(item, index) in selectedItems" :key="index" class="selected-detail">
                <div class="product-header">
                  <p>
                    <strong>{{ item.name }}</strong> ({{ formatPrice(item.price) }})
                  </p>
                </div>
                <div class="grid-container">
                  <BaseForm
                    v-model.number="item.quantity"
                    label="수량"
                    type="number"
                    min="1"
                    @input="recalculateItem(index)"
                  />
                  <BaseForm v-model="item.manager" label="담당자" type="text" />
                  <BaseForm
                    v-model.number="item.price"
                    label="정가"
                    type="number"
                    @input="recalculateItem(index)"
                  />
                  <BaseForm
                    v-model.number="item.discountRate"
                    label="할인율"
                    type="select"
                    :options="discountRateOptions"
                    @change="recalculateItem(index)"
                  />
                  <BaseForm
                    v-model.number="item.discountAmount"
                    label="할인금액"
                    type="number"
                    readonly
                  />
                  <BaseForm
                    v-model.number="item.finalPrice"
                    label="최종금액"
                    type="number"
                    readonly
                  />
                  <BaseForm
                    v-model="item.deduction"
                    label="정기권 차감"
                    type="select"
                    :options="deductionOptions"
                  />
                  <BaseForm v-model="item.couponCode" label="쿠폰번호입력" type="text" />
                  <BaseForm v-model="item.couponInfo" label="쿠폰정보" type="text" />
                </div>
              </div>
            </template>
          </div>

          <BaseForm v-model="memo" label="메모" type="textarea" />
        </div>

        <!-- 우측 -->
        <div class="form-right">
          <div class="search-row">
            <BaseForm type="text" placeholder="고객명 또는 연락처 검색" />
          </div>

          <div class="total-price-section">
            <label>최종 결제 금액</label>
            <div class="total-display">
              <div class="price-box">{{ formatPrice(finalTotalPrice) }}</div>
            </div>
          </div>

          <div class="discount-row">
            <div class="discount-rate-group">
              <label for="discountRate">할인율</label>
              <BaseForm
                id="discountRate"
                v-model="globalDiscountRate"
                type="select"
                :options="discountRateOptions"
                @change="applyGlobalDiscount"
              />
            </div>

            <div class="discount-amount-group">
              <label>할인금액</label>
              <BaseForm type="number" :model-value="globalDiscountAmount" readonly />
            </div>
          </div>

          <div v-for="method in methods" :key="method.key" class="method-group">
            <div class="checkbox-label">
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
    3
    <!-- ✅ Toast 컴포넌트 -->
    <BaseToast ref="toastRef" />
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  const toastRef = ref(null);
  const emit = defineEmits(['close', 'submit']);

  const props = defineProps({
    initialItems: Array,
    initialMemo: String,
    initialDate: String,
    initialTime: String,
    initialPayments: Array,
    initialDiscountRate: Number,
  });

  const selectedItems = ref([...props.initialItems]);
  const date = ref(props.initialDate || new Date().toISOString().substring(0, 10));
  const time = ref(props.initialTime || new Date().toTimeString().substring(0, 5));
  const memo = ref(props.initialMemo || '');

  const discountRates = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50];
  const discountRateOptions = discountRates.map(rate => ({ value: rate, text: `${rate}%` }));

  const deductionOptions = [
    { value: '', text: '정기권 선택' },
    { value: '정기권 차감', text: '정기권 차감' },
    { value: '차감 없음', text: '차감 없음' },
  ];

  const globalDiscountRate = ref(props.initialDiscountRate || 0);

  const methods = ref([
    { key: 'prepaid', label: '선불권' },
    { key: 'card', label: '카드 결제' },
    { key: 'cash', label: '현금 결제' },
    { key: 'naver', label: '네이버페이' },
    { key: 'local', label: '지역화폐' },
  ]);

  const selectedMethods = ref(props.initialPayments?.map(p => p.method) || []);
  const paymentAmounts = ref(
    Object.fromEntries((props.initialPayments || []).map(p => [p.method, p.amount]))
  );

  const totalPrice = computed(() =>
    selectedItems.value.reduce((sum, item) => sum + (item.finalPrice || 0), 0)
  );

  const globalDiscountAmount = computed(() => {
    const raw = totalPrice.value;
    const rate = globalDiscountRate.value;
    return Math.floor((raw * rate) / 100);
  });

  const finalTotalPrice = computed(() => {
    return totalPrice.value - globalDiscountAmount.value;
  });

  const recalculateItem = index => {
    const item = selectedItems.value[index];
    const unitPrice = item.price || 0;
    const quantity = item.quantity || 1;
    const rate = item.discountRate || 0;
    const total = unitPrice * quantity;
    const discount = Math.floor((total * rate) / 100);
    item.discountAmount = discount;
    item.finalPrice = total - discount;
  };

  const submit = () => {
    const payments = selectedMethods.value.map(key => ({
      method: key,
      amount: paymentAmounts.value[key] || 0,
    }));

    emit('submit', {
      date: date.value,
      time: time.value,
      items: selectedItems.value,
      memo: memo.value,
      payments,
      discountRate: globalDiscountRate.value,
      finalAmount: finalTotalPrice.value,
    });

    emit('close');
  };

  const formatPrice = val => (val || 0).toLocaleString('ko-KR') + '원';
</script>
