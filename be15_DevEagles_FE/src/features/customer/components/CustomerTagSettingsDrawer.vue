<template>
  <Teleport to="body">
    <div v-if="modelValue" class="overlay" @click.self="close">
      <div class="modal-panel">
        <!-- X 버튼 -->
        <button class="drawer-close-btn" aria-label="닫기" @click="close">
          <span aria-hidden="true">&times;</span>
        </button>
        <div class="tag-header-row">
          <div class="header-titles">
            <span class="main-title">고객 태그 설정</span>
            <span class="desc"> 태그를 설정해 고객의 특이사항을 분류하고 관리해보세요! </span>
          </div>
        </div>
        <div class="basic-tag-container">
          <BaseButton type="primary" size="sm" class="add-tag-btn" @click="showAddDrawer = true">
            새 태그 등록
          </BaseButton>
        </div>
        <div class="modal-body">
          <div class="tag-list-wrapper">
            <div v-for="tag in tags" :key="tag.tagId" class="tag-item">
              <div class="tag-main-col">
                <span
                  class="tag-name"
                  :style="{
                    backgroundColor: getLightColor(tag.colorCode),
                    color: tag.colorCode,
                  }"
                >
                  {{ tag.tagName }}
                </span>
              </div>
              <div class="tag-actions">
                <button class="action-btn" @click="openEditDrawer(tag)">
                  <EditIcon :size="16" />
                  <span>수정</span>
                </button>
                <button class="action-btn delete" @click="deleteTag(tag)">
                  <TrashIcon :size="16" />
                  <span>삭제</span>
                </button>
              </div>
            </div>
          </div>
        </div>
        <AddTagDrawer v-model="showAddDrawer" @create="onAddTag" />
        <EditTagDrawer v-model="showEditDrawer" :tag="selectedTag" @update="onEditTag" />
        <BaseConfirm
          v-model="showDeleteConfirm"
          title="태그 삭제"
          message="정말 삭제하시겠어요?"
          confirm-text="확인"
          cancel-text="취소"
          confirm-type="error"
          @confirm="handleDeleteTagConfirmed"
          @cancel="showDeleteConfirm = false"
        />
        <BaseToast ref="toastRef" />
      </div>
    </div>
  </Teleport>
</template>

<script setup>
  import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
  import EditIcon from '@/components/icons/EditIcon.vue';
  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import AddTagDrawer from './AddTagDrawer.vue';
  import EditTagDrawer from './EditTagDrawer.vue';
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
  const { tags } = storeToRefs(metadataStore);

  const showAddDrawer = ref(false);
  const showEditDrawer = ref(false);
  const selectedTag = ref(null);
  const toastRef = ref(null);

  const showDeleteConfirm = ref(false);
  const tagToDelete = ref(null);

  const close = () => {
    emit('update:modelValue', false);
  };

  async function onAddTag(newTag) {
    try {
      await metadataStore.createTag({
        tagName: newTag.tagName,
        colorCode: newTag.colorCode,
        shopId: authStore.shopId,
      });
      toastRef.value?.success('태그가 생성되었습니다.');
    } catch (e) {
      toastRef.value?.error(e.message || '태그 생성 실패');
    } finally {
      showAddDrawer.value = false;
    }
  }

  function openEditDrawer(tag) {
    selectedTag.value = { ...tag };
    showEditDrawer.value = true;
    nextTick(() => {});
  }

  async function onEditTag(editedTag) {
    try {
      await metadataStore.updateTag(editedTag.tagId, {
        tagName: editedTag.tagName,
        colorCode: editedTag.colorCode,
        shopId: authStore.shopId,
      });
      toastRef.value?.success('태그가 수정되었습니다.');
    } catch (e) {
      toastRef.value?.error(e.message || '태그 수정 실패');
    } finally {
      showEditDrawer.value = false;
    }
  }

  function deleteTag(tag) {
    tagToDelete.value = tag;
    showDeleteConfirm.value = true;
  }

  async function handleDeleteTagConfirmed() {
    try {
      await metadataStore.deleteTag(tagToDelete.value.tagId);
      toastRef.value?.success('태그가 삭제되었습니다.');
    } catch (e) {
      toastRef.value?.error(e.message || '태그 삭제 실패');
    } finally {
      showDeleteConfirm.value = false;
    }
  }

  // HEX 색상을 더 연하게 (흰색과 80% 섞기)
  function getLightColor(hex, ratio = 0.8) {
    if (!hex) return '#f5f5f5';
    let color = hex.replace('#', '');
    if (color.length === 3)
      color = color
        .split('')
        .map(x => x + x)
        .join('');
    const r = parseInt(color.substring(0, 2), 16);
    const g = parseInt(color.substring(2, 4), 16);
    const b = parseInt(color.substring(4, 6), 16);
    const newR = Math.round(r + (255 - r) * ratio);
    const newG = Math.round(g + (255 - g) * ratio);
    const newB = Math.round(b + (255 - b) * ratio);
    return `#${((1 << 24) + (newR << 16) + (newG << 8) + newB).toString(16).slice(1)}`;
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
  .tag-header-row {
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
  .basic-tag-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    width: 100%;
    margin-bottom: 12px;
  }
  .add-tag-btn {
    margin-top: 0;
    margin-right: 24px;
  }
  .modal-body {
    display: flex;
    flex-direction: column;
    gap: 32px;
    flex: 1;
  }
  .tag-list-wrapper {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  .tag-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 18px 14px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    background: #fff;
  }
  .tag-main-col {
    flex: 1 1 0;
  }
  .tag-name {
    font-size: 15px;
    font-weight: 600;
    border-radius: 6px;
    padding: 5px 18px;
    display: inline-block; /* 텍스트 영역에만 배경색 적용 */
    margin-bottom: 2px;
    transition: background 0.15s;
  }
  .tag-actions {
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
