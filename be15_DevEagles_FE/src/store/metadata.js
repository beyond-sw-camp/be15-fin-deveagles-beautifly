import { defineStore } from 'pinia';
import { ref } from 'vue';

// API modules
import tagsAPI from '@/features/customer/api/tags.js';
import gradesAPI from '@/features/customer/api/grades.js';
import channelsAPI from '@/features/customer/api/channels.js';
import { getStaff } from '@/features/staffs/api/staffs.js';
import { useAuthStore } from './auth.js';

export const useMetadataStore = defineStore('metadata', () => {
  const tags = ref([]);
  const grades = ref([]);
  const staff = ref([]);
  const channels = ref([]);
  const isLoading = ref(false);
  const metadataLoaded = ref(false);
  const authStore = useAuthStore();

  async function loadMetadata(force = false) {
    if (!force && metadataLoaded.value) {
      return;
    }

    if (isLoading.value) {
      return;
    }

    isLoading.value = true;
    try {
      // Tags
      try {
        tags.value = await tagsAPI.getTagsByShop();
      } catch (error) {
        console.error('Failed to load tags:', error);
        tags.value = [];
      }

      // Grades
      try {
        grades.value = await gradesAPI.getGradesByShop();
      } catch (error) {
        console.error('Failed to load grades:', error);
        grades.value = [];
      }

      // Staff
      try {
        const res = await getStaff({ page: 1, size: 1000, isActive: true, keyword: '' });
        const list = res.data?.data?.staffList || [];
        staff.value = list.map(s => ({ id: s.staffId || s.id, name: s.staffName || s.name }));
      } catch (error) {
        console.error('Failed to load staff:', error);
        staff.value = [];
      }

      // Acquisition channels
      try {
        channels.value = await channelsAPI.getChannelsByShop();
      } catch (error) {
        console.error('Failed to load channels:', error);
        channels.value = [];
      }

      metadataLoaded.value = true;
    } finally {
      isLoading.value = false;
    }
  }

  // ---------------------- Grade CRUD --------------------------------------
  async function createGrade({ name, discountRate }) {
    const id = await gradesAPI.createGrade({
      name,
      discountRate,
      shopId: authStore.shopId,
    });
    await loadMetadata(true); // Force reload
    return id;
  }

  async function updateGrade(gradeId, { name, discountRate }) {
    await gradesAPI.updateGrade(gradeId, {
      name,
      discountRate,
      shopId: authStore.shopId,
    });
    await loadMetadata(true); // Force reload
  }

  async function deleteGrade(gradeId) {
    await gradesAPI.deleteGrade(gradeId);
    await loadMetadata(true); // Force reload
  }

  // ---------------------- Tag CRUD ----------------------------------------
  async function createTag({ tagName, colorCode }) {
    const id = await tagsAPI.createTag({
      tagName,
      colorCode,
      shopId: authStore.shopId,
    });
    await loadMetadata(true); // Force reload
    return id;
  }

  async function updateTag(tagId, { tagName, colorCode }) {
    await tagsAPI.updateTag(tagId, {
      tagName,
      colorCode,
      shopId: authStore.shopId,
    });
    await loadMetadata(true); // Force reload
  }

  async function deleteTag(tagId) {
    await tagsAPI.deleteTag(tagId);
    await loadMetadata(true); // Force reload
  }

  return {
    // state
    tags,
    grades,
    staff,
    channels,
    isLoading,
    metadataLoaded,
    // actions
    loadMetadata,
    createGrade,
    updateGrade,
    deleteGrade,
    createTag,
    updateTag,
    deleteTag,
  };
});
