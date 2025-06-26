<script setup>
  import { ref, computed, watch } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: Boolean,
    template: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['update:modelValue', 'submit']);

  const visible = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  // 입력값 상태
  const name = ref('');
  const content = ref('');
  const grade = ref('');
  const tags = ref([]);
  const type = ref(''); // ✅ 유형 추가

  watch(
    () => props.modelValue,
    val => {
      if (val && props.template?.id) {
        name.value = props.template.name ?? '';
        content.value = props.template.content ?? '';
        grade.value = props.template.grade ?? '';
        tags.value = props.template.tags ?? [];
        type.value = props.template.type ?? '안내'; // ✅ 기본값
      }
    },
    { immediate: true }
  );

  function close() {
    visible.value = false;
  }

  function submit() {
    if (!name.value.trim() || !content.value.trim()) return;

    emit('submit', {
      id: props.template.id,
      name: name.value,
      content: content.value,
      grade: grade.value,
      tags: tags.value,
      type: type.value, // ✅ 포함
      createdAt: props.template.createdAt,
    });

    close();
  }
</script>

<template>
  <BaseModal v-model="visible" title="템플릿 수정">
    <div class="space-y-4">
      <!-- 템플릿명 -->
      <BaseForm v-model="name" type="input" label="템플릿명" placeholder="예: 예약 안내" />

      <!-- 내용 -->
      <BaseForm
        v-model="content"
        type="textarea"
        label="내용"
        placeholder="메시지 내용을 입력하세요"
        :rows="10"
      />

      <!-- 유형 -->
      <BaseForm
        v-model="type"
        type="select"
        label="유형"
        :options="['안내', '광고', '기타']"
        placeholder="유형 선택"
      />

      <!-- 대상 등급 -->
      <BaseForm
        v-model="grade"
        type="select"
        label="대상 등급"
        :options="['전체', 'VIP', 'VVIP']"
        placeholder="등급 선택"
      />

      <!-- 고객 태그 -->
      <BaseForm
        v-model="tags"
        type="multiselect"
        label="고객 태그"
        :options="['재방문', '신규', '이탈위험']"
        placeholder="고객 태그 선택"
      />

      <!-- 버튼 -->
      <div class="action-buttons mt-4 d-flex justify-content-end gap-2">
        <BaseButton type="ghost" @click="close">취소</BaseButton>
        <BaseButton :disabled="!name.trim() || !content.trim()" @click="submit">수정</BaseButton>
      </div>
    </div>
  </BaseModal>
</template>
