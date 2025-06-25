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

  // 입력값
  const name = ref('');
  const content = ref('');
  const grade = ref('');
  const tags = ref([]);
  const includeLink = ref(false);
  const couponId = ref(null);

  // 템플릿 열릴 때 기존 값 세팅
  watch(
    () => props.modelValue,
    val => {
      if (val && props.template?.id) {
        name.value = props.template.name ?? '';
        content.value = props.template.content ?? '';
        grade.value = props.template.grade ?? '';
        tags.value = props.template.tags ?? [];
        includeLink.value = !!props.template.includeLink;
        couponId.value = props.template.couponId ?? null;
      }
    },
    { immediate: true }
  );

  // 메시지에 '#{인스타url}' 포함되면 자동 체크
  watch(content, val => {
    includeLink.value = val.includes('#{인스타url}');
  });

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
      includeLink: includeLink.value,
      couponId: couponId.value,
      createdAt: props.template.createdAt,
    });

    close();
  }
</script>

<template>
  <BaseModal v-model="visible" title="템플릿 수정">
    <div class="space-y-4">
      <!-- 템플릿명 입력 -->
      <BaseForm v-model="name" type="input" label="템플릿명" placeholder="예: 예약 안내" />

      <!-- 내용 입력 -->
      <BaseForm
        v-model="content"
        type="textarea"
        label="내용"
        placeholder="메시지 내용을 입력하세요"
        :rows="10"
      />

      <!-- 등급 선택 -->
      <BaseForm
        v-model="grade"
        type="select"
        label="대상 등급"
        :options="['전체', 'VIP', 'VVIP']"
        placeholder="등급을 선택하세요"
      />

      <!-- 태그 선택 -->
      <BaseForm
        v-model="tags"
        type="multiselect"
        label="고객 태그"
        :options="['재방문', '신규', '이탈위험']"
        placeholder="고객 태그를 선택하세요"
      />

      <!-- 링크 첨부 여부 (자동으로 체크됨) -->
      <BaseForm v-model="includeLink" type="checkbox" label="인스타그램 링크 포함 여부" disabled />

      <!-- 쿠폰 선택 -->
      <BaseForm
        v-model="couponId"
        type="select"
        label="쿠폰 선택"
        :options="[
          { label: '10% 할인 쿠폰', value: 'coupon10' },
          { label: '무료 체험권', value: 'freeTrial' },
        ]"
        placeholder="쿠폰을 선택하세요"
      />

      <!-- 버튼 영역 -->
      <div class="action-buttons mt-4 d-flex justify-content-end gap-2">
        <BaseButton type="ghost" @click="close">취소</BaseButton>
        <BaseButton :disabled="!name.trim() || !content.trim()" @click="submit">수정</BaseButton>
      </div>
    </div>
  </BaseModal>
</template>
