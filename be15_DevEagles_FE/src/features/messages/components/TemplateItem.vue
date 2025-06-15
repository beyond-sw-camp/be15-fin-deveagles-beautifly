<script setup>
  const props = defineProps({ item: Object });

  function edit() {
    alert(`수정: ${props.item.name}`);
  }

  function remove() {
    if (confirm(`'${props.item.name}' 템플릿을 삭제할까요?`)) {
      alert('삭제 처리');
    }
  }
</script>

<template>
  <tr>
    <td class="cell">{{ item.name }}</td>
    <td class="cell">{{ item.content }}</td>
    <td class="cell">{{ item.createdAt }}</td>
    <td class="cell actions">
      <button class="btn btn--gray btn--xs" @click="edit">수정</button>
      <button class="btn btn--danger btn--xs" @click="remove">삭제</button>
    </td>
  </tr>
</template>

<style scoped>
  .cell {
    text-align: center;
    vertical-align: middle;
    padding: 0.75rem;
    font-size: 0.875rem;
    color: var(--color-neutral-dark);
  }

  .actions {
    display: flex;
    justify-content: center;
    gap: 0.5rem;
  }
</style>
