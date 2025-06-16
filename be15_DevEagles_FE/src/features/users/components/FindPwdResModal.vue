<template>
  <BaseModal v-model="showModal" title="">
    <div class="modal-body center-content">
      <template v-if="foundUserPwd">
        <p>
          비밀번호 변경을 위한 인증 메일이 전송되었습니다.<br />
          인증 완료 후 비밀번호를 변경해주세요.
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
    foundUserPwd: Boolean,
  });

  const showModal = computed({
    get: () => props.show,
    set: val => emit('update:show', val),
  });

  const closeModal = () => {
    emit('update:show', false);
  };
</script>
