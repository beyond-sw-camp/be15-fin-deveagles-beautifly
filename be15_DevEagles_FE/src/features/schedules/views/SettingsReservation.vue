<template>
  <div class="reservation-wrapper">
    <div class="reservation-settings">
      <h1 class="font-screen-title">예약 설정</h1>

      <!-- 점심시간 설정 라디오 -->
      <div class="section">
        <p class="section-title">점심시간 설정</p>
        <div class="radio-horizontal inline-radio">
          <label><input v-model="lunchEnabled" type="radio" :value="true" /> 설정</label>
          <label><input v-model="lunchEnabled" type="radio" :value="false" /> 비설정</label>
        </div>
      </div>

      <div class="section">
        <p class="section-title">예약 가능 시간 & 점심 시간 설정</p>
        <div class="time-grid">
          <div :class="['header-row', lunchEnabled ? 'lunch-on' : 'lunch-off']">
            <div>요일</div>
            <div>예약 시작</div>
            <div>~</div>
            <div>예약 마감</div>
            <template v-if="lunchEnabled">
              <div></div>
              <div>점심 시작</div>
              <div>~</div>
              <div>점심 마감</div>
            </template>
          </div>

          <!-- 요일별 row -->
          <div
            v-for="day in days"
            :key="day"
            :class="['time-row', lunchEnabled ? 'lunch-on' : 'lunch-off']"
          >
            <button
              class="day-button"
              :class="{ active: activeDays.includes(day) }"
              @click="toggleDay(day)"
            >
              {{ day }}
            </button>

            <template v-if="activeDays.includes(day)">
              <!-- 예약 시작/마감 -->
              <PrimeDatePicker
                v-model="availableTimes[day].start"
                :time-only="true"
                :show-time="true"
                hour-format="24"
                placeholder="시작"
                :manual-input="true"
              />
              <div class="time-separator">~</div>
              <PrimeDatePicker
                v-model="availableTimes[day].end"
                :time-only="true"
                :show-time="true"
                hour-format="24"
                placeholder="마감"
                :manual-input="true"
              />

              <!-- 점심 시작/마감 -->
              <template v-if="lunchEnabled">
                <div></div>
                <PrimeDatePicker
                  v-model="lunchTimes[day].start"
                  :time-only="true"
                  :show-time="true"
                  hour-format="24"
                  placeholder="점심 시작"
                  :manual-input="true"
                />
                <div class="time-separator">~</div>
                <PrimeDatePicker
                  v-model="lunchTimes[day].end"
                  :time-only="true"
                  :show-time="true"
                  hour-format="24"
                  placeholder="점심 마감"
                  :manual-input="true"
                />
              </template>
            </template>

            <template v-else>
              <div
                class="disabled-text"
                :style="{ gridColumn: lunchEnabled ? 'span 8' : 'span 5' }"
              >
                미사용
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- 예약 간격 -->
      <div class="section">
        <p class="section-title">예약 시간 간격</p>
        <div class="radio-horizontal inline-radio">
          <label><input v-model="interval" type="radio" value="10" /> 10분</label>
          <label><input v-model="interval" type="radio" value="30" /> 30분</label>
        </div>
      </div>

      <!-- 저장 버튼 -->
      <div class="button-group">
        <BaseButton type="primary" @click="saveSettings">설정 저장</BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';

  const days = ['월', '화', '수', '목', '금', '토', '일'];
  const activeDays = ref(['월', '화', '수', '목', '금']);
  const interval = ref('10');
  const lunchEnabled = ref(true);

  const availableTimes = ref({});
  const lunchTimes = ref({});

  for (const day of days) {
    availableTimes.value[day] = { start: '09:00', end: '18:00' };
    lunchTimes.value[day] = { start: '12:00', end: '13:00' };
  }

  const toggleDay = day => {
    if (activeDays.value.includes(day)) {
      activeDays.value = activeDays.value.filter(d => d !== day);
    } else {
      activeDays.value.push(day);
    }
  };

  const saveSettings = () => {
    for (const day of activeDays.value) {
      const { start, end } = availableTimes.value[day];
      if (!start || !end || start >= end) {
        alert(`${day}요일의 예약 시간이 올바르지 않습니다.`);
        return;
      }

      if (lunchEnabled.value) {
        const { start: lStart, end: lEnd } = lunchTimes.value[day];
        if (!lStart || !lEnd || lStart >= lEnd) {
          alert(`${day}요일의 점심시간이 올바르지 않습니다.`);
          return;
        }
      }
    }

    alert('✅ 설정이 정상적으로 저장되었습니다!');
  };
</script>

<style scoped>
  .reservation-settings {
    padding: 24px;
    max-width: 880px;
    margin: 0 auto;
    background: var(--color-neutral-white);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .section {
    margin-bottom: 32px;
  }

  .section-title {
    font-weight: bold;
    margin-bottom: 12px;
    font-size: 18px;
  }

  .time-grid {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  /* 점심 ON */
  .header-row.lunch-on,
  .time-row.lunch-on {
    display: grid;
    grid-template-columns: 60px 1fr 24px 1fr 48px 1fr 24px 1fr;
    gap: 8px;
    align-items: center;
  }

  /* 점심 OFF */
  .header-row.lunch-off,
  .time-row.lunch-off {
    display: grid;
    grid-template-columns: 60px 1fr 24px 1fr;
    gap: 8px;
    align-items: center;
  }

  .day-button {
    padding: 6px 12px;
    border: 1px solid var(--color-gray-300);
    border-radius: 6px;
    background: var(--color-neutral-white);
    cursor: pointer;
  }

  .day-button.active {
    background-color: var(--color-primary-main);
    color: white;
    border-color: var(--color-primary-main);
  }

  .time-separator {
    text-align: center;
    font-weight: 500;
    color: #444;
  }

  .disabled-text {
    color: var(--color-gray-500);
    font-size: 14px;
    padding-left: 8px;
  }

  .radio-horizontal {
    display: flex;
    gap: 24px;
    align-items: center;
    flex-wrap: wrap;
  }

  .inline-radio label {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }

  .button-group {
    display: flex;
    justify-content: flex-end;
  }

  .header-row > div {
    text-align: center;
    line-height: 1.6;
  }

  .reservation-wrapper {
    background-color: #f9fafb;
    min-height: 100vh;
    padding: 40px 0;
  }
  :deep(.p-datepicker-input) {
    padding-left: 12px !important;
    padding-right: 60px !important;
  }
</style>
