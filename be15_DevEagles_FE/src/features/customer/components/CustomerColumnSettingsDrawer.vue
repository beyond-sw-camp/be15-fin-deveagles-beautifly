<template>
  <BaseDrawer
    v-model="visible"
    title="노출 및 순서 설정"
    position="right"
    size="sm"
    :closable="true"
    :mask-closable="true"
  >
    <!-- Drawer Body (BaseDrawer의 .drawer-body에 들어감) -->
    <div>
      <div class="column-settings-desc">
        <div class="desc-title">노출 및 순서 설정</div>
        <div class="desc-detail">
          표에 노출을 원하는 데이터를 설정하고, 순서를 정할 수 있어요.<br />
          왼쪽 아이콘을 클릭해 순서를 옮겨보세요.
        </div>
      </div>
      <div class="column-settings-table">
        <div class="table-header">
          <div class="col-name">데이터 이름</div>
          <div class="col-toggle">노출</div>
        </div>
        <div class="table-body" @dragover.prevent>
          <div
            v-for="(col, idx) in localColumns"
            :key="col.key"
            class="table-row"
            :draggable="true"
            @dragstart="onDragStart(idx)"
            @dragover.prevent="onDragOver(idx)"
            @drop="onDrop(idx)"
          >
            <span class="drag-handle">☰</span>
            <span class="col-label">{{ col.title }}</span>
            <BaseToggleSwitch v-model="col.visible" />
          </div>
        </div>
      </div>
    </div>
    <!-- Drawer Footer -->
    <template #footer>
      <div class="drawer-footer-actions">
        <BaseButton type="secondary" outline size="sm" @click="handleCancel">취소</BaseButton>
        <BaseButton type="primary" size="sm" @click="handleSave">저장</BaseButton>
      </div>
    </template>
  </BaseDrawer>
</template>

<script setup>
  import { ref, watch, defineProps, defineEmits } from 'vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToggleSwitch from '@/components/common/BaseToggleSwitch.vue';

  const props = defineProps({
    modelValue: { type: Boolean, required: true },
    columns: {
      type: Array,
      required: true,
      // [{ key, title, width }]
    },
    value: {
      type: Array,
      default: () => [],
      // [{ key, title, visible }]
    },
  });
  const emit = defineEmits(['update:modelValue', 'save', 'cancel']);

  const visible = ref(props.modelValue);
  watch(
    () => props.modelValue,
    v => (visible.value = v)
  );
  watch(visible, v => emit('update:modelValue', v));

  // 체크박스 컬럼은 완전히 제외
  function getCustomizableColumns() {
    return props.columns.filter(col => col.key !== 'checkbox');
  }

  const localColumns = ref(
    (props.value && props.value.length
      ? props.value
      : getCustomizableColumns().map(col => ({ ...col, visible: true }))
    ).map(col => ({ ...col }))
  );

  watch(
    () => props.value,
    val => {
      localColumns.value = (
        val && val.length ? val : getCustomizableColumns().map(col => ({ ...col, visible: true }))
      ).map(col => ({ ...col }));
    }
  );
  watch(
    () => props.columns,
    () => {
      localColumns.value = getCustomizableColumns().map(col => ({
        ...col,
        visible: true,
      }));
    }
  );

  let dragIndex = null;
  function onDragStart(idx) {
    dragIndex = idx;
  }
  function onDragOver(idx) {}
  function onDrop(idx) {
    if (dragIndex === null || dragIndex === idx) return;
    const moved = localColumns.value.splice(dragIndex, 1)[0];
    localColumns.value.splice(idx, 0, moved);
    dragIndex = null;
  }
  function handleCancel() {
    localColumns.value = (
      props.value && props.value.length
        ? props.value
        : getCustomizableColumns().map(col => ({ ...col, visible: true }))
    ).map(col => ({ ...col }));
    visible.value = false;
    emit('cancel');
  }
  function handleSave() {
    emit(
      'save',
      localColumns.value.map(col => ({ ...col }))
    );
    visible.value = false;
  }
</script>

<style scoped>
  .column-settings-desc {
    margin-bottom: 18px;
  }
  .desc-title {
    font-size: 17px;
    font-weight: 700;
    margin-bottom: 5px;
  }
  .desc-detail {
    font-size: 13px;
    color: #888;
    line-height: 1.6;
  }
  .column-settings-table {
    background: #fafbfc;
    border-radius: 8px;
    border: 1px solid #eee;
    overflow: hidden;
    margin-bottom: 24px;
  }
  .table-header {
    display: flex;
    align-items: center;
    background: #f7f7fa;
    border-bottom: 1px solid #eee;
    padding: 10px 16px;
    font-size: 13px;
    font-weight: 600;
    color: #555;
  }
  .col-name {
    flex: 2;
  }
  .col-toggle {
    flex: 1;
    text-align: right;
  }
  .table-body {
    /* 이 부분에서 max-height와 overflow-y를 제거하여 부모 스크롤을 사용하게 함 */
  }
  .table-row {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    background: #fff;
    border-bottom: 1px solid #f0f0f0;
    cursor: grab;
    user-select: none;
    transition: background 0.15s;
  }
  .table-row:last-child {
    border-bottom: none;
  }
  .drag-handle {
    font-size: 18px;
    color: #bbb;
    margin-right: 12px;
    cursor: grab;
    width: 18px;
    text-align: center;
    flex-shrink: 0;
  }
  .col-label {
    flex: 2;
    font-size: 15px;
    color: #222;
  }
  .drawer-footer-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
</style>
