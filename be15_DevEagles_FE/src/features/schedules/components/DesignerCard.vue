<template>
  <div class="designer-card">
    <div class="tags">
      <span class="tag">디자이너</span>
      <span class="tag">{{ name }}</span>
    </div>
    <div class="content">
      <img class="profile" :src="image" alt="디자이너 이미지" />
      <div class="description">
        <p>
          정성껏 시술하는 {{ name }} 디자이너입니다. 고객의 스타일을 가장 잘 살리는 디테일을
          추구합니다.
        </p>
      </div>
    </div>
    <button class="reserve-button" @click="onReserveClick">예약</button>
  </div>
</template>

<script setup>
  const props = defineProps({
    name: String,
    image: String,
  });

  const emit = defineEmits(['reserve']);

  const onReserveClick = () => {
    emit('reserve', props.name);
  };
</script>

<style scoped>
  .designer-card {
    width: 400px;
    height: 250px;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
    background-color: #fff;
  }

  .tags {
    display: flex;
    gap: 6px;
  }

  .tag {
    background-color: #f5f5f5;
    padding: 4px 8px;
    border-radius: 6px;
    color: #333;
    font-weight: 600;
    font-size: 18px;
  }

  .content {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    flex: 1;
  }

  .profile {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    border: 1px solid #ccc;
  }

  .description {
    flex: 1;
    background-color: #fafafa;
    padding: 12px;
    border-radius: 6px;
    font-size: 15px;
    color: #333;
    border: 1px solid #eee;
    box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.03);
  }

  .reserve-button {
    background-color: #000;
    color: #fff;
    border: none;
    border-radius: 6px;
    margin-top: auto;
    padding: 12px 0;
    font-size: 15px;
    font-weight: bold;
    cursor: pointer;
  }
</style>
