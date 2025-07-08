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

  let loadedShopId = null;
  let loadingPromise = null;
  let metadataLoaded = false;

  async function loadMetadata() {
    const authStore = useAuthStore();
    const shopId = authStore.shopId?.value || authStore.shopId || 1;

    if (loadedShopId === shopId && metadataLoaded) {
      return;
    }

    if (loadingPromise) {
      return loadingPromise;
    }

    loadingPromise = (async () => {
      // Tags
      try {
        tags.value = await tagsAPI.getTagsByShop(shopId);
      } catch (_) {
        tags.value = [];
      }

      // Grades
      try {
        grades.value = await gradesAPI.getGradesByShop(shopId);
      } catch (_) {
        grades.value = [];
      }

      // Staff
      try {
        const res = await getStaff({ page: 1, size: 1000, isActive: true, keyword: '' });
        const list = res.data?.data?.staffList || [];
        staff.value = list.map(s => ({ id: s.staffId || s.id, name: s.staffName || s.name }));
      } catch (_) {
        staff.value = [];
      }

      // Acquisition channels
      try {
        channels.value = await channelsAPI.getChannelsByShop(shopId);
      } catch (_) {
        channels.value = [];
      }

      loadedShopId = shopId;
      metadataLoaded = true;
      loadingPromise = null;
    })();

    return loadingPromise;
  }

  // ---------------------- Grade CRUD --------------------------------------
  async function createGrade({ name, discountRate }) {
    const authStore = useAuthStore();
    const shopId = authStore.shopId?.value || authStore.shopId || 1;
    const id = await gradesAPI.createGrade({ shopId, name, discountRate });
    await loadMetadata();
    return id;
  }

  async function updateGrade(gradeId, { name, discountRate }) {
    const authStore = useAuthStore();
    const shopId = authStore.shopId?.value || authStore.shopId || 1;
    await gradesAPI.updateGrade(gradeId, { shopId, name, discountRate });
    await loadMetadata();
  }

  async function deleteGrade(gradeId) {
    await gradesAPI.deleteGrade(gradeId);
    await loadMetadata();
  }

  // ---------------------- Tag CRUD ----------------------------------------
  async function createTag({ tagName, colorCode }) {
    const authStore = useAuthStore();
    const shopId = authStore.shopId?.value || authStore.shopId || 1;
    const id = await tagsAPI.createTag({ shopId, tagName, colorCode });
    await loadMetadata();
    return id;
  }

  async function updateTag(tagId, { tagName, colorCode }) {
    const authStore = useAuthStore();
    const shopId = authStore.shopId?.value || authStore.shopId || 1;
    await tagsAPI.updateTag(tagId, { shopId, tagName, colorCode });
    await loadMetadata();
  }

  async function deleteTag(tagId) {
    await tagsAPI.deleteTag(tagId);
    await loadMetadata();
  }

  return {
    // state
    tags,
    grades,
    staff,
    channels,
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
