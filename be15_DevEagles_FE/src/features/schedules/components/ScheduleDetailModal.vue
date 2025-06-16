<script setup>
  import { ref, computed, defineProps, defineEmits, onBeforeUnmount, watch } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';

  const props = defineProps({
    modelValue: { type: Boolean, required: true },
    reservation: { type: Object, default: () => ({}) },
  });
  const emit = defineEmits(['update:modelValue']);

  const showMenu = ref(false);
  const isEditMode = ref(false);
  const edited = ref({});
  watch(
    () => props.modelValue,
    newVal => {
      if (newVal) {
        isEditMode.value = false;
        showMenu.value = false;
        edited.value = {};
      }
    }
  );
  const close = () => {
    emit('update:modelValue', false);
    isEditMode.value = false;
    showMenu.value = false;
    edited.value = {};
  };

  const statusOptions = [
    { text: '노쇼', value: '노쇼' },
    { text: '가게에 의한 취소', value: '가게에 의한 취소' },
    { text: '고객에 의한 취소', value: '고객에 의한 취소' },
    { text: '예약 대기', value: '예약 대기' },
    { text: '예약 확정', value: '예약 확정' },
  ];

  const labelMap = {
    reservation: '예약',
    holiday: '휴무',
    event: '일정',
    regular_event: '정기일정',
    regular_holiday: '정기휴무',
  };

  const typeOptions = computed(() => {
    if (edited.value.type === 'event' || edited.value.type === 'regular_event') {
      return [
        { text: '일정', value: 'event' },
        { text: '정기일정', value: 'regular_event' },
      ];
    } else if (edited.value.type === 'holiday' || edited.value.type === 'regular_holiday') {
      return [
        { text: '휴무', value: 'holiday' },
        { text: '정기휴무', value: 'regular_holiday' },
      ];
    } else {
      return [];
    }
  });

  const toggleMenu = () => {
    showMenu.value = !showMenu.value;
  };

  let stopWatch = null;

  const handleEdit = () => {
    isEditMode.value = true;
    showMenu.value = false;

    edited.value = { ...props.reservation };

    if (!edited.value.type && ['event', 'holiday'].includes(props.reservation.type)) {
      edited.value.type = props.reservation.type;
    }

    const isAllDay = edited.value.timeRange === '00:00 - 23:59';
    edited.value.allDay = isAllDay;

    edited.value.repeat = edited.value.repeat || 'none';

    prevTimeRange.value = props.reservation.timeRange || '';
    prevDuration.value = props.reservation.duration || '';
  };

  onBeforeUnmount(() => {
    if (stopWatch) stopWatch();
  });

  const handleDelete = () => {
    showMenu.value = false;
    if (confirm('정말 삭제하시겠습니까?')) {
      alert('삭제 요청 전송');
    }
  };

  const prevTimeRange = ref('');
  const prevDuration = ref('');

  const handleAllDayToggle = () => {
    if (edited.value.allDay) {
      if (!prevTimeRange.value) prevTimeRange.value = edited.value.timeRange;
      if (!prevDuration.value || prevDuration.value === '하루 종일') {
        prevDuration.value = edited.value.duration !== '하루 종일' ? edited.value.duration : '';
      }

      edited.value.timeRange = '00:00 - 23:59';
      edited.value.duration = '하루 종일';
    } else {
      edited.value.timeRange = prevTimeRange.value || '';
      edited.value.duration = prevDuration.value || '';
    }
  };

  watch(
    () => edited.value.allDay,
    isAllDay => {
      if (isAllDay) {
        edited.value.duration = '하루 종일';
      } else {
        edited.value.duration = prevDuration.value || '';
      }
    }
  );

  const saveEdit = () => {
    alert('수정 내용 저장 요청:\n' + JSON.stringify(edited.value, null, 2));
    isEditMode.value = false;
  };
</script>

<template>
  <div v-if="modelValue" class="overlay">
    <div class="modal-panel">
      <div class="modal-header">
        <div>
          <h1>등록된 스케줄</h1>
          <p class="type-label">{{ labelMap[reservation.type] }}</p>
        </div>
        <button class="close-btn" @click="close">×</button>
      </div>

      <div class="modal-body">
        <div class="left-detail">
          <template v-if="reservation.type === 'reservation'">
            <div class="row">
              <label>고객명</label><span v-if="!isEditMode">{{ reservation.customer }}</span
              ><input v-else v-model="edited.customer" />
            </div>
            <div class="row">
              <label>연락처</label><span v-if="!isEditMode">{{ reservation.phone }}</span
              ><input v-else v-model="edited.phone" />
            </div>
            <div class="row">
              <label>예약일</label
              ><span v-if="!isEditMode">{{ reservation.start }} ~ {{ reservation.end }}</span>
              <div v-else style="display: flex; gap: 8px">
                <input v-model="edited.start" /><input v-model="edited.end" />
              </div>
            </div>
            <div class="row">
              <label>시술</label><span v-if="!isEditMode">{{ reservation.service }}</span
              ><input v-else v-model="edited.service" />
            </div>
            <div class="row row-select">
              <label>담당자</label>
              <div class="form-control-wrapper">
                <BaseForm
                  v-if="isEditMode"
                  v-model="edited.employee"
                  type="select"
                  :options="[
                    { text: '디자이너 A', value: '디자이너 A' },
                    { text: '디자이너 B', value: '디자이너 B' },
                  ]"
                  placeholder="담당자 선택"
                />
                <span v-else>{{ reservation.employee }}</span>
              </div>
            </div>
            <div class="row row-select">
              <label>예약 상태</label>
              <div class="form-control-wrapper">
                <BaseForm
                  v-if="isEditMode"
                  v-model="edited.status"
                  type="select"
                  :options="statusOptions"
                  placeholder="예약 상태 선택"
                />
                <span v-else>{{ reservation.status }}</span>
              </div>
            </div>
            <div class="row">
              <label>특이사항</label><span v-if="!isEditMode">{{ reservation.note }}</span
              ><textarea v-else v-model="edited.note" />
            </div>
            <div class="row">
              <label>고객 메모</label><span v-if="!isEditMode">{{ reservation.memo }}</span
              ><textarea v-else v-model="edited.memo" />
            </div>
          </template>

          <template v-else>
            <div class="row row-select">
              <label>구분</label>
              <div class="form-control-wrapper">
                <BaseForm
                  v-if="isEditMode"
                  v-model="edited.type"
                  type="select"
                  :options="typeOptions"
                  placeholder="구분 선택"
                />
                <span v-else>{{ labelMap[reservation.type] }}</span>
              </div>
            </div>
            <div class="row">
              <label>제목</label><span v-if="!isEditMode">{{ reservation.title }}</span
              ><input v-else v-model="edited.title" />
            </div>
            <div class="row row-select">
              <label>담당자</label>
              <div class="form-control-wrapper">
                <BaseForm
                  v-if="isEditMode"
                  v-model="edited.employee"
                  type="select"
                  :options="[
                    { text: '디자이너 A', value: '디자이너 A' },
                    { text: '디자이너 B', value: '디자이너 B' },
                  ]"
                  placeholder="담당자 선택"
                />
                <span v-else>{{ reservation.employee || '미지정' }}</span>
              </div>
            </div>

            <div class="row">
              <label>날짜</label>
              <div class="date-inline">
                <!-- ✏️ 수정 모드 -->
                <template v-if="isEditMode">
                  <input v-model="edited.date" type="date" />

                  <input
                    v-if="!edited.allDay"
                    v-model="edited.timeRange"
                    type="text"
                    placeholder="오후 01:00 - 오후 04:00"
                  />

                  <input type="text" :value="edited.duration" readonly class="duration-input" />

                  <label class="all-day-checkbox">
                    <input v-model="edited.allDay" type="checkbox" @change="handleAllDayToggle" />
                    <span>종일</span>
                  </label>
                </template>

                <!-- 👁️ 보기 모드 -->
                <template v-else>
                  <span>{{ reservation.date }}</span>
                  <span v-if="!reservation.allDay">{{ reservation.timeRange }}</span>
                  <span v-if="reservation.duration">({{ reservation.duration }} 소요)</span>
                  <span v-if="reservation.allDay">종일</span>
                </template>
              </div>
            </div>

            <!-- 반복 라인 -->
            <div class="row">
              <label>반복</label>
              <div class="repeat-inline">
                <!-- ✏️ 수정 모드 -->
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
                        : '매주 ' +
                          ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'][
                            new Date(edited.date).getDay()
                          ] +
                          ' 반복'
                    }}
                  </span>
                </template>

                <!-- 👁️ 보기 모드 -->
                <template v-else>
                  <span>
                    {{
                      reservation.repeat === 'none'
                        ? '반복 안함'
                        : reservation.repeat === 'monthly'
                          ? '매달 ' + new Date(reservation.date).getDate() + '일 반복'
                          : '매주 ' +
                            ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'][
                              new Date(reservation.date).getDay()
                            ] +
                            ' 반복'
                    }}
                  </span>
                </template>
              </div>
            </div>

            <div class="row">
              <label>메모</label><span v-if="!isEditMode">{{ reservation.memo }}</span
              ><textarea v-else v-model="edited.memo" />
            </div>
          </template>
        </div>

        <div v-if="reservation.type === 'reservation'" class="right-box">
          <p>고객정보 확인</p>
          <p>예약문자 발송</p>
          <p>매출 등록</p>
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
</template>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    z-index: 1000;
  }

  .modal-panel {
    position: fixed;
    top: 0;
    left: 240px;
    width: calc(100% - 240px);
    height: 100vh;
    background: var(--color-neutral-white);
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
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
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
    background: var(--color-neutral-white);
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
    background: var(--color-gray-100);
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
    gap: 4px;
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

  .date-inline span {
    white-space: nowrap;
    width: auto !important;
    max-width: none;
    color: var(--color-gray-800);
  }

  .repeat-inline span {
    white-space: nowrap;
    width: auto !important;
    max-width: none;
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
