<template>
  <div>
    <div v-if="modelValue && edited && edited.id" class="overlay" @click.self="close">
      <div class="modal-panel">
        <div class="modal-header">
          <div>
            <h1>등록된 스케줄</h1>
            <p class="type-label">{{ planTypeLabel }}</p>
          </div>
          <button class="close-btn" @click="close">×</button>
        </div>

        <div class="modal-body">
          <div class="left-detail">
            <div class="row row-select">
              <label>구분</label>
              <div class="form-control-wrapper">
                <BaseForm
                  v-if="isEditMode"
                  v-model="edited.type"
                  type="select"
                  :options="[
                    { text: '일정', value: 'plan' },
                    { text: '정기일정', value: 'regular_plan' },
                  ]"
                  placeholder="구분 선택"
                />
                <span v-else>{{ planTypeLabel }}</span>
              </div>
            </div>

            <div class="row">
              <label>제목</label>
              <span v-if="!isEditMode">{{ edited.title }}</span>
              <BaseForm v-else v-model="edited.title" type="text" />
            </div>

            <div class="row row-select">
              <label>담당자</label>
              <div class="form-control-wrapper">
                <BaseForm
                  v-if="isEditMode"
                  v-model="edited.staffName"
                  type="select"
                  :options="[
                    { text: '디자이너 A', value: '디자이너 A' },
                    { text: '디자이너 B', value: '디자이너 B' },
                  ]"
                  placeholder="담당자 선택"
                />
                <span v-else>{{ edited.staffName || '미지정' }}</span>
              </div>
            </div>

            <div class="row">
              <label>날짜</label>
              <div class="date-inline">
                <template v-if="isEditMode">
                  <!-- 그대로 유지 -->
                  <PrimeDatePicker
                    v-model="edited.date"
                    :clearable="false"
                    :show-time="false"
                    :show-button-bar="true"
                    :style="{ width: '200px' }"
                  />
                  <PrimeDatePicker
                    v-model="edited.startTime"
                    :clearable="false"
                    :show-time="true"
                    :time-only="true"
                    hour-format="24"
                    placeholder="시작 시간"
                    :style="{ width: '140px' }"
                  />
                  <PrimeDatePicker
                    v-model="edited.endTime"
                    :clearable="false"
                    :show-time="true"
                    :time-only="true"
                    hour-format="24"
                    placeholder="종료 시간"
                    :style="{ width: '140px' }"
                  />
                  <span>소요 시간 : </span>
                  <input
                    type="text"
                    :value="edited.duration"
                    readonly
                    class="duration-input"
                    style="width: 100px"
                  />
                  <label class="all-day-checkbox">
                    <input v-model="edited.allDay" type="checkbox" @change="handleAllDayToggle" />
                    <span>종일</span>
                  </label>
                </template>

                <template v-else>
                  <span v-if="edited.timeRange">
                    {{ edited.timeRange }}
                    <span v-if="edited.duration"> ({{ edited.duration }} 소요) </span>
                  </span>
                  <span v-else>시간 정보 없음</span>
                </template>
              </div>
            </div>

            <div class="row">
              <label>반복</label>
              <div class="repeat-inline">
                <template v-if="isEditMode">
                  <BaseForm
                    v-model="edited.repeat"
                    type="select"
                    :options="[
                      { text: '반복 안함', value: 'none' },
                      { text: '매달 반복', value: 'monthly' },
                      { text: '요일 반복', value: 'weekly' },
                    ]"
                    placeholder="반복 주기"
                  />
                  <span v-if="edited.repeat !== 'none' && edited.date" class="repeat-description">
                    {{
                      edited.repeat === 'monthly'
                        ? '매달 ' + new Date(edited.date).getDate() + '일 반복'
                        : '매주 ' + koreanWeekday[new Date(edited.date).getDay()] + ' 반복'
                    }}
                  </span>
                </template>

                <template v-else>
                  <template v-if="type === 'regular_plan' && edited.repeat === 'weekly'">
                    <span>매주 {{ koreanWeekday[edited.date] || '요일 미지정' }} 반복</span>
                  </template>
                  <template v-else-if="type === 'regular_plan' && edited.repeat === 'monthly'">
                    <span>매달 {{ edited.date || '일 미지정' }}일 반복</span>
                  </template>
                  <template v-else>
                    <span>반복 없음</span>
                  </template>
                </template>
              </div>
            </div>

            <div class="row">
              <label>메모</label>
              <span v-if="!isEditMode">{{ edited.memo }}</span>
              <BaseForm v-else v-model="edited.memo" type="textarea" rows="3" />
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <BaseButton type="error" @click="close">닫기</BaseButton>
          <template v-if="isEditMode">
            <BaseButton type="primary" @click="saveEdit">저장</BaseButton>
          </template>
          <template v-else>
            <div class="action-dropdown">
              <BaseButton type="primary" @click="toggleMenu">수정 / 삭제</BaseButton>
              <ul v-if="showMenu" class="dropdown-menu">
                <li @click="handleEdit">수정하기</li>
                <li @click="handleDelete">삭제하기</li>
              </ul>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { computed, defineProps, defineEmits, onMounted, onBeforeUnmount, ref, watch } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import { fetchScheduleDetail } from '@/features/schedules/api/schedules.js';

  const emit = defineEmits(['update:modelValue']);
  const isEditMode = ref(false);
  const showMenu = ref(false);
  const edited = ref({});
  const backup = ref({});

  defineOptions({
    name: 'PlanDetailModal',
  });

  const props = defineProps({
    modelValue: Boolean,
    id: [String, Number],
    type: String,
  });

  const koreanWeekday = {
    SUN: '일요일',
    MON: '월요일',
    TUE: '화요일',
    WED: '수요일',
    THU: '목요일',
    FRI: '금요일',
    SAT: '토요일',
  };

  const close = () => {
    emit('update:modelValue', false);
    isEditMode.value = false;
    showMenu.value = false;
    edited.value = {};
  };

  const toggleMenu = () => (showMenu.value = !showMenu.value);

  const handleEdit = () => {
    isEditMode.value = true;
    showMenu.value = false;

    const { date, timeRange, duration, repeat = 'none', ...rest } = edited.value;

    const [startStr, endStr] = (timeRange || '').split(' - ').map(str => str.trim());

    const toTime = str => {
      if (!str) return null;
      const [h, m] = str.split(':').map(Number);
      let baseDate;
      if (repeat === 'monthly') {
        baseDate = new Date();
        baseDate.setDate(Number(date));
      } else if (repeat === 'weekly') {
        baseDate = new Date();
      } else {
        baseDate = new Date(date);
      }
      if (isNaN(baseDate)) {
        console.warn('⚠ Invalid base date for time parsing:', date);
        return null;
      }
      baseDate.setHours(h, m, 0, 0);
      return baseDate;
    };

    edited.value = {
      ...rest,
      date: repeat === 'none' ? new Date(date) : date,
      startTime: toTime(startStr),
      endTime: toTime(endStr),
      duration,
      timeRange,
      allDay: timeRange === '00:00 - 23:59',
      repeat,
    };

    backup.value = JSON.parse(JSON.stringify(edited.value));
  };

  const handleDelete = () => {
    showMenu.value = false;
    if (confirm('정말 삭제하시겠습니까?')) {
      alert('삭제 요청 전송');
    }
  };

  const saveEdit = () => {
    alert('수정 내용 저장:\n' + JSON.stringify(edited.value, null, 2));
    isEditMode.value = false;
  };
  const handleAllDayToggle = () => {
    if (edited.value.allDay) {
      edited.value.startTime = new Date(edited.value.date);
      edited.value.startTime.setHours(0, 0, 0);
      edited.value.endTime = new Date(edited.value.date);
      edited.value.endTime.setHours(23, 59, 0);
    } else {
      edited.value.startTime = new Date(backup.value.startTime);
      edited.value.endTime = new Date(backup.value.endTime);
    }
  };

  watch(
    () => [props.id, props.type, props.modelValue],
    async ([id, type, visible]) => {
      if (!id || !type || !visible) return;

      const { data } = await fetchScheduleDetail(type, id);
      const d = data.data;

      const convert = (startStr, endStr, showDate = false) => {
        const parseTime = str => {
          if (/^\d{2}:\d{2}:\d{2}$/.test(str)) {
            const today = new Date();
            const yyyy = today.getFullYear();
            const mm = String(today.getMonth() + 1).padStart(2, '0');
            const dd = String(today.getDate()).padStart(2, '0');
            return new Date(`${yyyy}-${mm}-${dd}T${str}`);
          }
          return new Date(str);
        };

        const startDate = parseTime(startStr);
        const endDate = parseTime(endStr);

        if (isNaN(startDate) || isNaN(endDate)) {
          return {
            timeRange: '시간 정보 없음',
            duration: '',
          };
        }

        const startTime = startDate.toTimeString().slice(0, 5);
        const endTime = endDate.toTimeString().slice(0, 5);

        const toYMD = date =>
          `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;

        const timeRange = showDate
          ? toYMD(startDate) === toYMD(endDate)
            ? `${toYMD(startDate)} ${startTime} - ${endTime}`
            : `${toYMD(startDate)} ${startTime} ~ ${toYMD(endDate)} ${endTime}`
          : `${startTime} - ${endTime}`;

        const diffMs = endDate - startDate;
        const minutes = Math.floor(diffMs / 60000);
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;

        const duration = `${hours ? `${hours}시간` : ''} ${mins ? `${mins}분` : ''}`.trim();

        return {
          timeRange,
          duration,
        };
      };

      let result = {
        id,
        type,
        title: d.title ?? d.leaveTitle,
        staffName: d.staffName,
        staffId: d.staffId,
        memo: d.memo,
        allDay: false,
        repeat: 'none',
      };

      if (type === 'regular_plan') {
        result.type = 'regular_plan';
        if (d.weeklyPlan) {
          result.repeat = 'weekly';
          result.date = d.weeklyPlan;
        } else if (d.monthlyPlan) {
          result.repeat = 'monthly';
          result.date = String(d.monthlyPlan);
        }
        const { timeRange, duration } = convert(d.startAt, d.endAt, false);
        result.timeRange = timeRange;
        result.duration = duration;
      } else {
        result.type = 'plan';
        const { timeRange, duration } = convert(d.startAt, d.endAt, true);
        result.timeRange = timeRange;
        result.duration = duration;
      }

      edited.value = result;
    },
    { immediate: true }
  );

  const handleEsc = e => e.key === 'Escape' && close();
  onMounted(() => window.addEventListener('keydown', handleEsc));
  onBeforeUnmount(() => window.removeEventListener('keydown', handleEsc));

  const planTypeLabel = computed(() => {
    return edited.value?.type === 'regular_plan' ? '정기 일정' : '일정';
  });
</script>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.3);
    z-index: 1000;
  }

  .modal-panel {
    position: fixed;
    top: 0;
    left: 240px;
    width: calc(100% - 240px);
    height: 100vh;
    background-color: var(--color-neutral-white);
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow-y: auto;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .modal-header h1 {
    font-size: 20px;
    font-weight: bold;
    color: var(--color-text-primary);
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: var(--color-text-primary);
  }

  .modal-body {
    display: flex;
    gap: 32px;
    flex: 1;
  }

  .left-detail {
    flex: 1;
  }

  .row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 14px;
  }

  .row label {
    width: 100px;
    font-weight: bold;
    color: var(--color-gray-800);
    padding-top: 6px;
    line-height: 1.5;
  }

  .row span,
  .row input,
  .row textarea {
    font-size: 14px;
    line-height: 1.5;
    padding: 6px 8px;
    vertical-align: middle;
    width: 100%;
    max-width: 400px;
    box-sizing: border-box;
    color: var(--color-text-primary);
    background-color: var(--color-neutral-white);
  }

  .row input,
  .row textarea {
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
  }

  .row textarea {
    resize: vertical;
  }

  .form-control-wrapper {
    flex: 1;
    display: flex;
    align-items: flex-start;
  }

  .form-control-wrapper :deep(.input) {
    width: 100%;
    max-width: 300px;
  }

  .right-box {
    width: 200px;
    padding: 12px;
    border-left: 1px solid var(--color-gray-200);
  }

  .right-box p {
    margin-bottom: 16px;
    font-weight: 500;
    color: var(--color-gray-700);
  }

  .modal-footer {
    margin-top: 32px;
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }

  .action-dropdown {
    position: relative;
  }

  .dropdown-menu {
    position: absolute;
    bottom: 40px;
    right: 0;
    background-color: var(--color-neutral-white);
    border: 1px solid var(--color-gray-300);
    border-radius: 6px;
    list-style: none;
    padding: 8px 0;
    width: 120px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
    z-index: 10;
  }

  .dropdown-menu li {
    padding: 8px 12px;
    cursor: pointer;
    color: var(--color-gray-800);
  }

  .dropdown-menu li:hover {
    background-color: var(--color-gray-100);
  }

  .type-label {
    margin-top: 4px;
    font-size: 18px;
    font-weight: 500;
    color: var(--color-gray-500);
  }

  .date-inline {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
  }

  .date-inline input[type='date'],
  .date-inline input[type='text'],
  .date-inline select {
    font-size: 14px;
    padding: 6px 8px;
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
    background-color: var(--color-neutral-white);
    color: var(--color-gray-900);
    min-width: 120px;
    height: 32px;
  }

  .all-day-checkbox {
    display: flex;
    align-items: center;
    white-space: nowrap;
    color: var(--color-gray-700);
  }

  .repeat-inline {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: nowrap;
  }

  .repeat-inline :deep(.input) {
    display: inline-block;
    width: auto;
    min-width: 160px;
  }

  .repeat-description {
    font-size: 14px;
    color: var(--color-gray-500);
    white-space: nowrap;
  }

  .date-inline span,
  .repeat-inline span {
    white-space: nowrap;
    width: auto !important;
    max-width: none;
    color: var(--color-gray-800);
  }

  .duration-input {
    font-size: 14px;
    padding: 6px 8px;
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
    background-color: var(--color-gray-100);
    color: var(--color-gray-800);
    min-width: 100px;
    height: 32px;
    white-space: nowrap;
  }
</style>
