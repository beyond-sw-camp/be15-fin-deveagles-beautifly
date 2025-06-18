<template>
  <div class="permission-section">
    <h3>접근 권한 목록</h3>

    <div v-for="(perm, index) in permissions" :key="perm.key" class="permission-item">
      <!-- 권한 활성화 토글 -->
      <div class="permission-header">
        <label>{{ perm.label }}</label>
        <BaseToggleSwitch v-model="perm.enabled" />
      </div>

      <!-- 하위 권한: 읽기/쓰기/삭제 -->
      <div v-if="perm.enabled" class="permission-detail">
        <BaseForm
          v-model="perm.permissions"
          type="checkbox"
          :label="''"
          :options="[
            { value: 'read', text: '읽기' },
            { value: 'write', text: '쓰기' },
            { value: 'delete', text: '삭제' },
          ]"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import BaseToggleSwitch from '@/components/common/BaseToggleSwitch.vue';
  import BaseForm from '@/components/common/BaseForm.vue';

  const props = defineProps({
    modelValue: Array,
  });

  const emit = defineEmits(['update:modelValue']);

  const permissions = ref([]);

  watch(
    () => props.modelValue,
    val => {
      permissions.value = val ? JSON.parse(JSON.stringify(val)) : [];
    },
    { immediate: true, deep: true }
  );
  watch(
    permissions,
    val => {
      emit('update:modelValue', val);
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
