<template>
  <div class="staff-container">
    <!-- 상단 헤더 -->
    <div class="staff-header">
      <BaseButton @click="goToCreate">+ 직원 추가</BaseButton>
      <div class="search-area">
        <input
          v-model="searchText"
          placeholder="이름 검색"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <BaseButton @click="handleSearch">검색</BaseButton>
        <label class="checkbox-label">
          <input v-model="onlyActive" type="checkbox" />
          재직 여부
        </label>
      </div>
    </div>

    <!-- 테이블 -->
    <BaseTable
      :columns="columns"
      :data="filteredStaff"
      :hover="true"
      :striped="true"
      :row-key="'id'"
      @click-row="goToDetail"
    >
      <!-- 이름 셀 커스터마이징 -->
      <template #cell-name="{ item, value }">
        <div class="name-cell" @click="goToDetail(item)">
          <div class="color-box" :style="{ backgroundColor: item.colorCode }"></div>
          {{ value }}
        </div>
      </template>
    </BaseTable>

    <!-- 페이지네이션 -->
    <Pagination
      :current-page="page"
      :total-pages="totalPages"
      :total-items="totalCount"
      :items-per-page="limit"
      @page-change="handlePageChange"
    />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import Pagination from '@/components/common/Pagination.vue';

  const router = useRouter();
  const staffList = ref([]);
  // ✅ 더미 데이터
  const dummyStaffList = [
    {
      id: 1,
      name: '김보라',
      phone: '010-1111-1111',
      position: '수석 디자이너',
      status: '재직',
      colorCode: '#FFB6B6',
    },
    {
      id: 2,
      name: '이준혁',
      phone: '010-2222-2222',
      position: '디자이너',
      status: '재직',
      colorCode: '#FFD29D',
    },
    {
      id: 3,
      name: '정하늘',
      phone: '010-3333-3333',
      position: '수습',
      status: '재직',
      colorCode: '#FFF5BA',
    },
    {
      id: 4,
      name: '박지현',
      phone: '010-4444-4444',
      position: '디자이너',
      status: '퇴사',
      colorCode: '#C9F4AA',
    },
    {
      id: 5,
      name: '한민서',
      phone: '010-5555-5555',
      position: '사장',
      status: '재직',
      colorCode: '#A0E7E5',
    },
    {
      id: 6,
      name: '최유진',
      phone: '010-6666-6666',
      position: '수석 디자이너',
      status: '재직',
      colorCode: '#B4F8C8',
    },
    {
      id: 7,
      name: '장도연',
      phone: '010-7777-7777',
      position: '수습',
      status: '재직',
      colorCode: '#FFDAC1',
    },
    {
      id: 8,
      name: '홍지수',
      phone: '010-8888-8888',
      position: '디자이너',
      status: '퇴사',
      colorCode: '#E0BBE4',
    },
    {
      id: 9,
      name: '유하린',
      phone: '010-9999-9999',
      position: '디자이너',
      status: '재직',
      colorCode: '#D5AAFF',
    },
    {
      id: 10,
      name: '배진우',
      phone: '010-1010-1010',
      position: '수습',
      status: '재직',
      colorCode: '#A1C6EA',
    },
    {
      id: 11,
      name: '송가희',
      phone: '010-1112-1112',
      position: '디자이너',
      status: '재직',
      colorCode: '#FF9AA2',
    },
    {
      id: 12,
      name: '문재하',
      phone: '010-1212-1212',
      position: '수석 디자이너',
      status: '재직',
      colorCode: '#FFB347',
    },
    {
      id: 13,
      name: '김태림',
      phone: '010-1313-1313',
      position: '수습',
      status: '퇴사',
      colorCode: '#B5EAD7',
    },
    {
      id: 14,
      name: '이채연',
      phone: '010-1414-1414',
      position: '디자이너',
      status: '재직',
      colorCode: '#C7CEEA',
    },
    {
      id: 15,
      name: '오서준',
      phone: '010-1515-1515',
      position: '수석 디자이너',
      status: '재직',
      colorCode: '#F3B0C3',
    },
    {
      id: 16,
      name: '권소연',
      phone: '010-1616-1616',
      position: '디자이너',
      status: '재직',
      colorCode: '#C8E7FF',
    },
    {
      id: 17,
      name: '양은서',
      phone: '010-1717-1717',
      position: '수습',
      status: '퇴사',
      colorCode: '#F8ECD1',
    },
    {
      id: 18,
      name: '조승우',
      phone: '010-1818-1818',
      position: '디자이너',
      status: '재직',
      colorCode: '#F6DFEB',
    },
    {
      id: 19,
      name: '임소민',
      phone: '010-1919-1919',
      position: '수석 디자이너',
      status: '재직',
      colorCode: '#B5EAD7',
    },
    {
      id: 20,
      name: '정동윤',
      phone: '010-2020-2020',
      position: '사장',
      status: '재직',
      colorCode: '#FFCCBC',
    },
  ];

  onMounted(() => {
    staffList.value = dummyStaffList;
    totalCount.value = dummyStaffList.length;
  });

  const page = ref(1);
  const limit = ref(10);
  const totalCount = ref(0);
  const searchText = ref('');
  const onlyActive = ref(true);

  const columns = [
    { key: 'name', title: '이름', width: '200px' },
    { key: 'phone', title: '연락처', width: '180px' },
    { key: 'position', title: '직급', width: '150px' },
    { key: 'status', title: '재직 상태', width: '120px' },
  ];

  const totalPages = computed(() => Math.ceil(totalCount.value / limit.value));

  // 현재 페이지의 staffList 계산
  const pagedStaff = computed(() => {
    const start = (page.value - 1) * limit.value;
    const end = start + limit.value;
    return dummyStaffList.slice(start, end);
  });

  // 필터링된 직원 리스트
  const filteredStaff = computed(() => {
    return pagedStaff.value.filter(staff => {
      const matchesName = staff.name.includes(searchText.value);
      const matchesStatus = onlyActive.value ? staff.status === '재직' : true;
      return matchesName && matchesStatus;
    });
  });

  const handleSearch = () => {
    // 검색 버튼 눌렀을 때 현재 페이지를 1페이지로 초기화하고 다시 불러옴
    page.value = 1;
    fetchStaff();
  };

  const fetchStaff = async () => {
    // todo api 연동
  };

  const handlePageChange = newPage => {
    page.value = newPage;
    fetchStaff();
  };

  const goToCreate = () => {
    router.push({ name: 'StaffRegist' });
  };

  const goToDetail = staff => {
    router.push({ name: 'StaffDetail', params: { id: staff.id } });
  };
  // onMounted(fetchStaff);
</script>

<style scoped>
  .staff-container {
    padding: 20px;
  }

  .staff-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .search-area {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .search-input {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 6px;
  }

  .checkbox-label {
    font-size: 14px;
    color: #555;
  }

  .color-box {
    width: 12px;
    height: 12px;
    display: inline-block;
    margin-right: 8px;
    border-radius: 2px;
  }

  .name-cell {
    display: flex;
    align-items: center;
    cursor: pointer;
  }
</style>
