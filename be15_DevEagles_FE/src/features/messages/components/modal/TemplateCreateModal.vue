<script setup>
  import { ref, computed, nextTick } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: Boolean,
  });
  const emit = defineEmits(['update:modelValue', 'submit']);

  const visible = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const name = ref('');
  const content = ref('');
  const grade = ref('전체');
  const tags = ref('');
  const type = ref('안내'); // ✅ 유형 추가
  const showDropdown = ref(false);
  const contentWrapper = ref(null);

  const variables = ['#{고객명}', '#{잔여선불충전액}', '#{잔여포인트}', '#{상점명}'];

  function insertVariable(variable) {
    const textarea = contentWrapper.value?.querySelector('textarea');
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const before = content.value.slice(0, start);
    const after = content.value.slice(end);

    content.value = before + variable + after;

    nextTick(() => {
      textarea.focus();
      textarea.selectionStart = textarea.selectionEnd = start + variable.length;
    });
  }

  function toggleDropdown() {
    showDropdown.value = !showDropdown.value;
  }

  function close() {
    visible.value = false;
  }

  function submit() {
    if (!name.value || !content.value) return;

    emit('submit', {
      id: Date.now(),
      name: name.value,
      content: content.value,
      grade: grade.value,
      tags: tags.value
        .split(',')
        .map(t => t.trim())
        .filter(Boolean),
      type: type.value, // ✅ 포함
      createdAt: new Date().toISOString().slice(0, 10),
    });

    close();
  }
</script>

<template>
  <BaseModal v-model="visible" title="템플릿 등록">
    <div class="space-y-4">
      <BaseForm v-model="name" label="템플릿명" placeholder="예: 예약 안내" />

      <div class="form-group">
        <label class="form-label flex justify-between items-center">
          <span>내용</span>
          <BaseButton size="xs" type="ghost" @click.stop="toggleDropdown"> 변수 삽입 ▼ </BaseButton>
        </label>
        <div ref="contentWrapper" class="relative" @click.self="showDropdown = false">
          <BaseForm
            v-model="content"
            type="textarea"
            :rows="8"
            placeholder="메시지 내용을 입력하세요"
          />
          <div
            v-if="showDropdown"
            class="absolute z-10 mt-1 right-0 bg-white border border-gray-200 rounded shadow p-2 w-48"
          >
            <div v-for="v in variables" :key="v" class="insert-item" @click="insertVariable(v)">
              {{ v }}
            </div>
          </div>
        </div>
      </div>

      <BaseForm
        v-model="type"
        label="유형"
        type="select"
        :options="[
          { value: '안내', text: '안내' },
          { value: '광고', text: '광고' },
          { value: '기타', text: '기타' },
        ]"
      />

      <BaseForm
        v-model="grade"
        label="대상 등급"
        type="select"
        :options="[
          { value: '전체', text: '전체' },
          { value: 'VIP', text: 'VIP' },
          { value: '단골', text: '단골' },
          { value: '신규', text: '신규' },
        ]"
      />

      <BaseForm v-model="tags" label="태그 (쉼표로 구분)" placeholder="예: 여름,이벤트,첫방문" />

      <div class="flex justify-end mt-4">
        <BaseButton type="error" @click="close">취소</BaseButton>
        <BaseButton type="primary" :disabled="!name || !content" class="ml-3" @click="submit">
          등록
        </BaseButton>
      </div>
    </div>
  </BaseModal>
</template>

<style scoped>
  .insert-item {
    @apply py-1 px-2 text-sm hover:bg-gray-100 rounded cursor-pointer;
  }
</style>
