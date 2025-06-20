<template>
  <div class="designer-list">
    <DesignerCard
      v-for="(designer, index) in designers"
      :key="index"
      :name="designer.name"
      :image="designer.image"
    />
  </div>
</template>

<script setup>
  import img1 from '@/images/suri/designer-profile1.png';
  import img2 from '@/images/suri/designer-profile5.png';
  import img3 from '@/images/suri/designer-profile3.png';
  import img4 from '@/images/suri/designer-profile4.png';
  import img5 from '@/images/suri/designer-profile2.png';
  import img6 from '@/images/suri/designer-profile6.png';
  import DesignerCard from '@/features/schedules/components/DesignerCard.vue';

  const designers = [
    { name: '김이슬 1', image: img1 },
    { name: '김이슬 2', image: img2 },
    { name: '김이슬 3', image: img3 },
    { name: '김이슬 4', image: img4 },
    { name: '김이슬 5', image: img5 },
    { name: '김이슬 6', image: img6 },
  ];
</script>

<style scoped>
  .designer-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 24px;
  }
</style>
