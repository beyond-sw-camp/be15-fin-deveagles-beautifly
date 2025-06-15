import { ref, computed } from 'vue';
import { useToast } from './useToast';
import { MESSAGES } from '@/constants/messages';

export function useListManagement(options = {}) {
  const { itemName = '항목', initialItems = [], itemsPerPage: initialItemsPerPage = 10 } = options;

  // State
  const items = ref([...initialItems]);
  const currentPage = ref(1);
  const itemsPerPage = ref(initialItemsPerPage);
  const loading = ref(false);
  const showDeleteConfirm = ref(false);
  const selectedItem = ref(null);
  const triggerElement = ref(null);

  // Toast
  const { showSuccess, showError, showInfo } = useToast();

  // Computed
  const totalItems = computed(() => items.value.length);
  const totalPages = computed(() => Math.ceil(totalItems.value / itemsPerPage.value));
  const paginatedItems = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    const end = start + itemsPerPage.value;
    return items.value.slice(start, end);
  });

  // Methods
  const toggleItemStatus = (item, statusField = 'isActive') => {
    const message = item[statusField]
      ? MESSAGES.COMMON.ACTIVATED(itemName)
      : MESSAGES.COMMON.DEACTIVATED(itemName);
    showSuccess(message);
  };

  const deleteItem = (item, event) => {
    selectedItem.value = item;
    triggerElement.value = event.target.closest('button');
    showDeleteConfirm.value = true;
  };

  const confirmDelete = () => {
    if (selectedItem.value) {
      const index = items.value.findIndex(item => item.id === selectedItem.value.id);
      if (index !== -1) {
        items.value.splice(index, 1);
        showSuccess(MESSAGES.COMMON.DELETED(itemName));
      }
    }
    cancelDelete();
  };

  const cancelDelete = () => {
    selectedItem.value = null;
    triggerElement.value = null;
    showDeleteConfirm.value = false;
  };

  const handlePageChange = page => {
    currentPage.value = page;
  };

  const handleItemsPerPageChange = newItemsPerPage => {
    itemsPerPage.value = newItemsPerPage;
    currentPage.value = 1; // 페이지당 항목 수 변경 시 첫 페이지로 이동
  };

  const addItem = newItem => {
    items.value.unshift({
      ...newItem,
      id: Date.now(), // 실제로는 서버에서 받아올 ID
    });
    showSuccess(MESSAGES.COMMON.CREATED(itemName));
  };

  const showNotImplemented = feature => {
    showInfo(`${feature} 기능은 준비 중입니다.`);
  };

  return {
    // State
    items,
    currentPage,
    itemsPerPage,
    loading,
    showDeleteConfirm,
    selectedItem,
    triggerElement,

    // Computed
    totalItems,
    totalPages,
    paginatedItems,

    // Methods
    toggleItemStatus,
    deleteItem,
    confirmDelete,
    cancelDelete,
    handlePageChange,
    handleItemsPerPageChange,
    addItem,
    showNotImplemented,

    // Toast methods
    showSuccess,
    showError,
    showInfo,
  };
}
