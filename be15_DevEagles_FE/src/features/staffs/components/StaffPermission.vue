<template>
  <div class="permission-section">
    <h3>접근 권한 목록</h3>

    <div
      v-for="(perm, index) in permissions"
      :key="perm.authId ?? `${perm.accessName}-${index}`"
      class="permission-item"
    >
      <!-- 권한 활성화 토글 -->
      <div class="permission-header">
        <label>{{ perm.accessName }}</label>
        <BaseToggleSwitch v-model="perm.active" />
      </div>

      <!-- 하위 권한: 읽기/쓰기/삭제 -->
      <div v-if="perm.active" class="permission-detail">
        <BaseForm
          v-model="perm.permissions"
          type="checkbox"
          :label="''"
          :options="[
            { value: 'canRead', text: '읽기' },
            { value: 'canWrite', text: '쓰기' },
            { value: 'canDelete', text: '삭제' },
          ]"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import isEqual from 'lodash.isequal';
  import BaseToggleSwitch from '@/components/common/BaseToggleSwitch.vue';
  import BaseForm from '@/components/common/BaseForm.vue';

  const props = defineProps({
    modelValue: Array,
  });

  const emit = defineEmits(['update:modelValue']);

  const permissions = ref([]);
  let prevEmitted = null;

  // ✅ modelValue → 내부 permissions 초기화
  watch(
    () => props.modelValue,
    val => {
      if (!val || isEqual(val, permissions.value)) return;

      permissions.value = val.map(p => ({
        ...p,
        permissions: [
          ...(p.canRead ? ['canRead'] : []),
          ...(p.canWrite ? ['canWrite'] : []),
          ...(p.canDelete ? ['canDelete'] : []),
        ],
      }));
    },
    { immediate: true }
  );

  // ✅ permissions 변경 시 → modelValue emit
  watch(
    permissions,
    val => {
      const emitData = val.map(p => ({
        ...p,
        canRead: p.permissions.includes('canRead'),
        canWrite: p.permissions.includes('canWrite'),
        canDelete: p.permissions.includes('canDelete'),
      }));

      if (!isEqual(emitData, prevEmitted)) {
        emit('update:modelValue', emitData);
        prevEmitted = emitData;
      }
    },
    { deep: true }
  );
</script>

<style scoped>
  .permission-section {
    border: 1px solid #ccc;
    border-radius: 8px;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .permission-item {
    padding: 12px;
    border-bottom: 1px solid #eee;
  }

  .permission-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .permission-detail {
    margin-top: 8px;
    display: flex;
    gap: 12px;
  }
</style>
