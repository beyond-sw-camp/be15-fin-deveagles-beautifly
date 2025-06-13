<script setup>
  import FullCalendar from '@fullcalendar/vue3';
  import dayGridPlugin from '@fullcalendar/daygrid';
  import timeGridPlugin from '@fullcalendar/timegrid';
  import interactionPlugin from '@fullcalendar/interaction';
  import { ref } from 'vue';
  import koLocale from '@fullcalendar/core/locales/ko';

  const calendarOptions = ref({
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    editable: true,
    locale: koLocale, // ⬅️ 한국어 설정 추가
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay',
    },
    events: [
      {
        id: '1',
        title: '13:00 이채은 고객',
        start: '2025-06-12T13:00:00',
        end: '2025-06-12T13:30:00',
        backgroundColor: '#f87171',
        textColor: '#111',
      },
    ],
    eventDrop(info) {
      console.log('이벤트 이동됨:', {
        id: info.event.id,
        newStart: info.event.start,
        newEnd: info.event.end,
      });
    },
  });
</script>

<template>
  <FullCalendar :options="calendarOptions" />
</template>

<style scoped>
  :deep(.fc-event) {
    color: #fff;
    font-weight: 500;
    border: none;
    border-radius: 6px;
  }
</style>
