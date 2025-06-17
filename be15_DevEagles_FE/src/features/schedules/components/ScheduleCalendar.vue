<script setup>
  import { defineProps, watch, ref } from 'vue';
  import FullCalendar from '@fullcalendar/vue3';
  import dayGridPlugin from '@fullcalendar/daygrid';
  import timeGridPlugin from '@fullcalendar/timegrid';
  import interactionPlugin from '@fullcalendar/interaction';
  import koLocale from '@fullcalendar/core/locales/ko';

  const props = defineProps({
    schedules: {
      type: Array,
      default: () => [],
    },
  });

  const emit = defineEmits(['clickSchedule']);

  const calendarOptions = ref({
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    editable: true,
    locale: koLocale,
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay',
    },
    events: props.schedules,
    eventContent({ event }) {
      const dotColor = event.backgroundColor || '#999';
      const title = event.title;
      return {
        html: `
        <div style="display: flex; align-items: center; gap: 6px;">
          <span style="display:inline-block;width:10px;height:10px;background:${dotColor};border-radius:2px;"></span>
          <span style="color:#111;font-size:13px;font-weight:500;">${title}</span>
        </div>
      `,
      };
    },
    eventClick(info) {
      emit('clickSchedule', info.event.id);
    },
    eventDrop(info) {
      console.log('이벤트 이동됨:', {
        id: info.event.id,
        newStart: info.event.start,
        newEnd: info.event.end,
      });
    },
  });

  watch(
    () => props.schedules,
    newVal => {
      calendarOptions.value.events = newVal;
    },
    { immediate: true, deep: true }
  );
</script>

<template>
  <FullCalendar :options="calendarOptions" />
</template>

<style scoped>
  :deep(.fc-event) {
    font-weight: 500;
    border: none;
    border-radius: 6px;
  }
</style>
