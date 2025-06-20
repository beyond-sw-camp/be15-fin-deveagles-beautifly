<template>
  <div>
    <!-- 휴무 날짜 (한 줄) -->
    <div class="row row-inline">
      <label class="label-wide">휴무 날짜</label>
      <div class="flat-flex">
        <PrimeDatePicker
          v-model="form.date"
          :show-time="false"
          :show-button-bar="true"
          :clearable="false"
          hour-format="24"
          placeholder="날짜를 선택하세요"
        />
      </div>
    </div>

    <!-- 반복 여부 -->
    <div class="row row-inline">
      <label class="label-wide">반복</label>
      <select v-model="form.repeat">
        <option value="none">반복 안함</option>
        <option value="weekly">매주</option>
        <option value="monthly">매달</option>
      </select>
    </div>

    <!-- 휴무 제목 -->
    <div class="row row-inline">
      <label class="label-wide">휴무 제목</label>
      <input v-model="form.title" type="text" />
    </div>

    <!-- 담당자 -->
    <div class="row row-inline">
      <label class="label-wide">담당자</label>
      <select v-model="form.staff">
        <option value="">담당자</option>
        <option value="김이글">김이글</option>
        <option value="박이글">박이글</option>
      </select>
    </div>

    <!-- 메모 -->
    <div class="row align-top">
      <label class="label-wide">메모</label>
      <textarea v-model="form.memo" rows="3" />
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';

  const form = ref({
    date: '',
    repeat: 'none',
    title: '',
    staff: '',
    memo: '',
  });
</script>

<style scoped>
  .row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 14px;
    width: 100%;
  }
  .row-inline {
    flex-wrap: nowrap;
    align-items: center;
  }
  .row.align-top {
    align-items: flex-start;
  }
  .row label {
    flex-shrink: 0;
    font-weight: bold;
    color: var(--color-gray-800);
    line-height: 1.5;
    white-space: nowrap;
  }
  .label-wide {
    width: 120px;
  }
  .row input,
  .row textarea,
  .row select {
    font-size: 14px;
    line-height: 1.5;
    padding: 6px 8px;
    width: 100%;
    max-width: 400px;
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
    box-sizing: border-box;
  }
  .row textarea {
    resize: vertical;
  }
  .flat-flex {
    display: flex;
    flex: 1;
    gap: 8px;
    flex-wrap: nowrap;
    align-items: center;
  }
</style>
