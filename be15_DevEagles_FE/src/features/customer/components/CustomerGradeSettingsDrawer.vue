<template>
  <Teleport to="body">
    <div v-if="modelValue" class="overlay" @click.self="close">
      <div class="modal-panel">
        <!-- X 버튼 -->
        <button class="drawer-close-btn" aria-label="닫기" @click="close">
          <span aria-hidden="true">&times;</span>
        </button>
        <div class="grade-header-row">
          <div class="header-titles">
            <span class="main-title">고객 등급 설정</span>
            <span class="desc">
              고객에 따라 등급을 다르게 설정해보세요. 등급별로 적립률을 다르게 설정할 수 있어요!
            </span>
          </div>
        </div>
        <!-- 새 등급 등록 버튼: 등급 목록 오른쪽 끝, 아래 폼과 간격 통일 -->
        <div class="basic-grade-container">
          <BaseButton type="primary" size="sm" class="add-grade-btn" @click="showAddDrawer = true">
            새 등급 등록
          </BaseButton>
        </div>
        <div class="modal-body">
          <div class="grade-list-wrapper" @dragover.prevent>
            <!-- 기본등급: 드래그핸들 위치에 빈공간, 등급명/할인율 두 줄 -->
            <div v-if="defaultGrade" :key="defaultGrade.id" class="grade-item">
              <span class="drag-handle drag-handle-disabled"></span>
              <div class="grade-main-col">
                <span class="grade-name">{{ defaultGrade.name }}</span>
                <span class="grade-rates"> 할인율 {{ defaultGrade.discountRate ?? 0 }}% </span>
              </div>
              <div class="grade-actions">
                <button class="action-btn" @click="openEditDrawer(defaultGrade)">
                  <EditIcon :size="16" />
                  <span>수정</span>
                </button>
              </div>
            </div>
            <!-- 나머지 등급: 드래그 가능, 핸들 표시, 등급명/할인율 한 줄 -->
            <div
              v-for="(grade, idx) in otherGrades"
              :key="grade.id"
              class="grade-item"
              :draggable="true"
              @dragstart="onDragStart(idx)"
              @dragover.prevent="onDragOver(idx)"
              @drop="onDrop(idx)"
            >
              <span class="drag-handle">☰</span>
              <div class="grade-main-col">
                <span class="grade-name">{{ grade.name }}</span>
                <span class="grade-rates"> 할인율 {{ grade.discountRate ?? 0 }}% </span>
              </div>
              <div class="grade-actions">
                <button class="action-btn" @click="openEditDrawer(grade)">
                  <EditIcon :size="16" />
                  <span>수정</span>
                </button>
                <button class="action-btn delete" @click="deleteGrade(grade)">
                  <TrashIcon :size="16" />
                  <span>삭제</span>
                </button>
              </div>
            </div>
          </div>
        </div>
        <AddGradeDrawer v-model="showAddDrawer" @create="onAddGrade" />
        <EditGradeDrawer v-model="showEditDrawer" :grade="selectedGrade" @update="onEditGrade" />
        <BaseConfirm
          v-model="showDeleteConfirm"
          title="등급 삭제"
          :message="`해당 등급에 해당된 고객은 기본등급으로 전환됩니다.\n삭제하시겠어요?`"
          confirm-text="확인"
          cancel-text="취소"
          confirm-type="error"
          @confirm="handleDeleteGradeConfirmed"
          @cancel="showDeleteConfirm = false"
        />
        <BaseToast ref="toastRef" />
      </div>
    </div>
  </Teleport>
</template>

<script setup>
  import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue';
  import EditIcon from '@/components/icons/EditIcon.vue';
  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import AddGradeDrawer from './AddGradeDrawer.vue';
  import EditGradeDrawer from './EditGradeDrawer.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseConfirm from '@/components/common/BaseConfirm.vue';
  import { useMetadataStore } from '@/store/metadata.js';
  import { useAuthStore } from '@/store/auth.js';
  import { storeToRefs } from 'pinia';

  const props = defineProps({
    modelValue: { type: Boolean, required: true },
  });
  const emit = defineEmits(['update:modelValue']);

  const metadataStore = useMetadataStore();
  const authStore = useAuthStore();
  const { grades } = storeToRefs(metadataStore);

  const showAddDrawer = ref(false);
  const showEditDrawer = ref(false);
  const selectedGrade = ref(null);
  const toastRef = ref(null);

  const showDeleteConfirm = ref(false);
  const gradeToDelete = ref(null);

  const defaultGrade = computed(() => {
    const list = Array.isArray(grades.value) ? grades.value : [];
    return list.find(g => !g.is_deletable);
  });
  const otherGrades = computed(() => {
    const list = Array.isArray(grades.value) ? grades.value : [];
    return list.filter(g => g.is_deletable);
  });

  let dragIndex = null;
  function onDragStart(idx) {
    dragIndex = idx;
  }
  function onDragOver(idx) {}
  function onDrop(idx) {
    if (dragIndex === null || dragIndex === idx) return;
    const arr = [...otherGrades.value];
    const moved = arr.splice(dragIndex, 1)[0];
    arr.splice(idx, 0, moved);
    grades.value = [...grades.value.filter(g => !g.is_deletable), ...arr];
    dragIndex = null;
  }

  const close = () => {
    emit('update:modelValue', false);
  };

  async function onAddGrade(newGrade) {
    try {
      await metadataStore.createGrade({
        name: newGrade.name,
        discountRate: newGrade.discountRate ?? 0,
        shopId: authStore.shopId,
      });
      toastRef.value?.success('고객 등급이 생성되었습니다.');
    } catch (e) {
      toastRef.value?.error(e.message || '등급 생성 실패');
    } finally {
      showAddDrawer.value = false;
    }
  }

  function openEditDrawer(grade) {
    selectedGrade.value = { ...grade };
    showEditDrawer.value = true;
    nextTick(() => {});
  }
  async function onEditGrade(editedGrade) {
    try {
      await metadataStore.updateGrade(editedGrade.id, {
        name: editedGrade.name,
        discountRate: editedGrade.discountRate ?? 0,
        shopId: authStore.shopId,
      });
      toastRef.value?.success('고객 등급이 수정되었습니다.');
    } catch (e) {
      toastRef.value?.error(e.message || '등급 수정 실패');
    } finally {
      showEditDrawer.value = false;
    }
  }
  function deleteGrade(grade) {
    gradeToDelete.value = grade;
    showDeleteConfirm.value = true;
  }
  async function handleDeleteGradeConfirmed() {
    try {
      await metadataStore.deleteGrade(gradeToDelete.value.id);
      toastRef.value?.success('고객 등급이 삭제되었습니다.');
    } catch (e) {
      toastRef.value?.error(e.message || '등급 삭제 실패');
    } finally {
      showDeleteConfirm.value = false;
    }
  }

  const handleEsc = e => {
    if (e.key === 'Escape') close();
  };
  onMounted(() => {
    window.addEventListener('keydown', handleEsc);
  });
  onBeforeUnmount(() => window.removeEventListener('keydown', handleEsc));
</script>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    z-index: 1000;
    display: flex;
    justify-content: flex-end;
  }
  .modal-panel {
    position: fixed;
    top: 0;
    left: unset;
    right: 0;
    width: 480px;
    height: 100vh;
    background: #fff;
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow-y: auto;
    position: relative;
  }
  .drawer-close-btn {
    position: absolute;
    right: 20px;
    top: 20px;
    background: none;
    border: none;
    font-size: 28px;
    color: #888;
    cursor: pointer;
    padding: 0 6px;
    border-radius: 50%;
    transition: background 0.15s;
    height: 40px;
    width: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10;
  }
  .drawer-close-btn:hover {
    background: #f5f5f5;
    color: #e53935;
  }
  .grade-header-row {
    display: flex;
    justify-content: flex-start;
    align-items: flex-start;
    margin-bottom: 12px;
    gap: 32px;
  }
  .header-titles {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 7px;
  }
  .main-title {
    font-size: 18px;
    font-weight: bold;
    color: #222;
    line-height: 1.2;
  }
  .desc {
    font-size: 14px;
    color: #888;
    line-height: 1.4;
    margin-top: 2px;
  }
  .basic-grade-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    width: 100%;
    margin-bottom: 12px; /* 등급 폼과 동일한 간격 */
  }
  .add-grade-btn {
    margin-top: 0;
    margin-right: 24px; /* 등급 목록 오른쪽 끝과 맞춤 */
  }
  .modal-body {
    display: flex;
    flex-direction: column;
    gap: 32px;
    flex: 1;
  }
  .grade-list-wrapper {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 12px; /* 등급 폼 간격 */
  }
  .grade-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 18px 14px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    background: #fff;
  }
  .drag-handle {
    font-size: 18px;
    color: #bbb;
    margin-right: 8px;
    cursor: grab;
    width: 18px;
    text-align: center;
    flex-shrink: 0;
    user-select: none;
  }
  .drag-handle-disabled {
    opacity: 0;
    pointer-events: none;
    margin-right: 8px;
    width: 18px;
    height: 18px;
    display: inline-block;
  }
  .grade-main-col {
    display: flex;
    flex-direction: column;
    gap: 3px;
    flex: 1 1 0;
  }
  .grade-name {
    font-size: 15px;
    font-weight: 600;
    color: #333;
  }
  .grade-rates {
    font-size: 13px;
    color: #555;
    white-space: pre-line;
  }
  .grade-actions {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-left: auto;
  }
  .action-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    background: none;
    border: none;
    color: #666;
    cursor: pointer;
    font-size: 0.95rem;
    font-weight: 500;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    transition: all 0.2s;
  }
  .action-btn:hover {
    background-color: #f0f0f0;
    color: #333;
  }
  .action-btn.delete:hover {
    color: #e53935;
  }

  :deep(.message) {
    white-space: pre-line;
    word-break: break-word;
    font-size: 14px;
    color: #333;
    line-height: 1.5;
  }
</style>
