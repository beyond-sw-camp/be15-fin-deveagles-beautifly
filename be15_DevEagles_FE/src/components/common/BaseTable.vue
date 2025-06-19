<template>
  <div class="table-container">
    <table :class="['table', { 'table-striped': striped }, { 'table-hover': hover }]">
      <!-- Table Header -->
      <thead v-if="columns.length > 0">
        <tr @click="$emit('row-click', item, $event)">
          <th
            v-for="column in columns"
            :key="column.key"
            :style="{ width: column.width }"
            :class="column.headerClass"
          >
            <slot :name="`header-${column.key}`" :column="column">
              {{ column.title }}
            </slot>
          </th>
        </tr>
      </thead>

      <!-- Table Body -->
      <tbody>
        <slot name="body">
          <tr
            v-for="(item, index) in data"
            :key="getRowKey(item, index)"
            @click="$emit('row-click', item, $event)"
          >
            <td v-for="column in columns" :key="column.key" :class="column.cellClass">
              <slot
                :name="`cell-${column.key}`"
                :item="item"
                :value="getNestedValue(item, column.key)"
                :index="index"
              >
                {{ getNestedValue(item, column.key) }}
              </slot>
            </td>
          </tr>
        </slot>
      </tbody>
    </table>

    <!-- Empty State -->
    <div v-if="data.length === 0 && !loading" class="table-empty">
      <slot name="empty">
        <p class="text-gray-500">데이터가 없습니다.</p>
      </slot>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="table-loading">
      <slot name="loading">
        <p class="text-gray-500">로딩 중...</p>
      </slot>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'BaseTable',
    props: {
      columns: {
        type: Array,
        default: () => [],
        // columns: [{ key: 'name', title: '이름', width: '200px', headerClass: '', cellClass: '' }]
      },
      data: {
        type: Array,
        default: () => [],
      },
      striped: {
        type: Boolean,
        default: false,
      },
      hover: {
        type: Boolean,
        default: true,
      },
      loading: {
        type: Boolean,
        default: false,
      },
      rowKey: {
        type: [String, Function],
        default: 'id',
      },
    },
    emits: ['row-click'],
    methods: {
      getRowKey(item, index) {
        if (typeof this.rowKey === 'function') {
          return this.rowKey(item, index);
        }
        return item[this.rowKey] || index;
      },
      getNestedValue(obj, path) {
        return path.split('.').reduce((current, key) => current?.[key], obj);
      },
    },
  };
</script>

<style scoped>
  .table-container {
    position: relative;
  }

  .table-empty,
  .table-loading {
    text-align: center;
    padding: 3rem;
  }

  .table-loading {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>
