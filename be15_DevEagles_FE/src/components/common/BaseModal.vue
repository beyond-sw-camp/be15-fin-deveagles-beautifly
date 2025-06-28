<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div
        v-if="modelValue"
        class="modal-backdrop"
        :style="{ zIndex: zIndex }"
        @click.self="closeModal"
      >
        <div :class="['modal', animationClass]">
          <div class="modal-header">
            <h2 class="modal-title">{{ title }}</h2>
            <button class="modal-close" @click="closeModal">&times;</button>
          </div>
          <div class="modal-body">
            <slot></slot>
          </div>
          <div v-if="$slots.footer" class="modal-footer">
            <slot name="footer">
              <BaseButton @click="closeModal">닫기</BaseButton>
            </slot>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script>
  import BaseButton from './BaseButton.vue';

  export default {
    name: 'BaseModal',
    components: { BaseButton },
    props: {
      modelValue: {
        type: Boolean,
        required: true,
      },
      title: {
        type: String,
        default: '',
      },
      animationClass: {
        type: String,
        default: '',
      },
      // [수정] z-index를 prop으로 받아 제어
      zIndex: {
        type: Number,
        default: 1000,
      },
    },
    emits: ['update:modelValue'],
    mounted() {
      window.addEventListener('keydown', this.handleEsc);
    },
    beforeUnmount() {
      window.removeEventListener('keydown', this.handleEsc);
    },
    methods: {
      closeModal() {
        this.$emit('update:modelValue', false);
      },
      handleEsc(event) {
        if (event.key === 'Escape') {
          this.closeModal();
        }
      },
    },
  };
</script>

<style>
  .modal-fade-enter-active,
  .modal-fade-leave-active {
    transition: opacity 0.3s ease;
  }

  .modal-fade-enter-from,
  .modal-fade-leave-to {
    opacity: 0;
  }

  .back-in-left {
    animation: backInLeft 0.6s both;
  }

  @keyframes backInLeft {
    0% {
      opacity: 0;
      transform: translateX(-800px) scale(0.7);
    }
    80% {
      opacity: 0.7;
      transform: translateX(0px) scale(1.05);
    }
    100% {
      opacity: 1;
      transform: translateX(0) scale(1);
    }
  }

  /* BaseModal.vue에 있는 스타일은 그대로 유지합니다 */
  .modal-backdrop {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .modal {
    background: white;
    border-radius: 8px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
    width: 90%;
    max-width: 500px;
    display: flex;
    flex-direction: column;
  }
  .modal-header {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .modal-title {
    font-size: 1.25rem;
    font-weight: 600;
    margin: 0;
  }
  .modal-close {
    background: transparent;
    border: 0;
    font-size: 1.5rem;
    cursor: pointer;
  }
  .modal-body {
    padding: 1.5rem;
    overflow-y: auto;
  }
  .modal-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid #e5e7eb;
    display: flex;
    justify-content: flex-end;
  }
</style>
