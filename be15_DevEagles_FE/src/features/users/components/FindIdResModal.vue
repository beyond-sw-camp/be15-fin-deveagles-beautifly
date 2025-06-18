<template>
  <BaseModal v-model="showModal" title="아이디 찾기">
    <div class="modal-body center-content">
      <template v-if="foundUserId">
        <p>
          <strong>회원님의 아이디는</strong><br />
          {{ foundUserId }} <strong>입니다.</strong>
        </p>
      </template>
      <template v-else>
        <p>
          존재하지 않는 회원정보입니다.<br />
          확인 후 다시 입력해주세요.
        </p>
      </template>
    </div>
    <template #footer>
      <BaseButton type="primary" @click="closeModal">확인</BaseButton>
    </template>
  </BaseModal>
</template>
<script setup>
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { computed } from 'vue';

  const emit = defineEmits(['update:show']);
  const props = defineProps({
    show: Boolean,
    foundUserId: {
      type: String,
      default: null,
    },
  });

  const showModal = computed({
    get: () => props.show,
    set: val => emit('update:show', val),
  });

  const closeModal = () => {
    emit('update:show', false);
  };
</script>
