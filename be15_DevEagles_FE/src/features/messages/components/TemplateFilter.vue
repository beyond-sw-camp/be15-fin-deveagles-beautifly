<script setup>
  import { reactive } from 'vue';
  import { SearchIcon } from '@/components/icons';

  const emit = defineEmits(['submitFilter']);
  const local = reactive({ keyword: '' });

  function submit() {
    emit('submitFilter', { ...local });
  }
</script>

<template>
  <form class="filter-form" @submit.prevent="submit">
    <div class="search-wrapper">
      <input
        v-model="local.keyword"
        type="text"
        placeholder="템플릿명 또는 내용 검색"
        class="search-input"
      />
      <SearchIcon :size="16" class="search-icon" />
    </div>
  </form>
</template>

<style scoped>
  .filter-form {
    width: 100%;
  }

  .search-wrapper {
    position: relative;
    width: 100%;
    max-width: 300px;
  }

  .search-input {
    width: 100%;
    padding: 0.5rem 2.25rem 0.5rem 1rem; /* 오른쪽 패딩 확보 */
    font-size: 0.875rem;
    border: 1px solid var(--color-gray-300);
    border-radius: 0.5rem;
    background-color: white;
  }

  .search-icon {
    position: absolute;
    top: 50%;
    right: 0.75rem;
    transform: translateY(-50%);
    color: var(--color-gray-500);
    pointer-events: none;
  }
</style>
