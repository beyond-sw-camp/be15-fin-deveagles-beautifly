<template>
  <div class="base-calendar">
    <div class="calendar-header">
      <BaseButton type="secondary" size="sm" @click="previousMonth">
        <span class="arrow">‹</span>
      </BaseButton>
      <h3 class="calendar-title">{{ formattedCurrentDate }}</h3>
      <BaseButton type="secondary" size="sm" @click="nextMonth">
        <span class="arrow">›</span>
      </BaseButton>
    </div>

    <div class="calendar-weekdays">
      <div v-for="day in weekdays" :key="day" class="weekday">
        {{ day }}
      </div>
    </div>

    <div class="calendar-days">
      <div
        v-for="day in calendarDays"
        :key="`${day.year}-${day.month}-${day.date}`"
        class="calendar-day"
        :class="{
          'other-month': day.isOtherMonth,
          today: day.isToday,
          selected: day.isSelected,
          disabled: day.isDisabled,
        }"
        @click="selectDate(day)"
      >
        <span class="day-number">{{ day.date }}</span>
        <div v-if="day.events && day.events.length" class="day-events">
          <div
            v-for="event in day.events.slice(0, 2)"
            :key="event.id"
            class="event-dot"
            :class="`event-${event.type}`"
          ></div>
          <div v-if="day.events.length > 2" class="more-events">+{{ day.events.length - 2 }}</div>
        </div>
      </div>
    </div>

    <div v-if="showEventList && selectedDayEvents.length" class="event-list">
      <h4>{{ selectedDateText }} 일정</h4>
      <div
        v-for="event in selectedDayEvents"
        :key="event.id"
        class="event-item"
        :class="`event-${event.type}`"
      >
        <span class="event-time">{{ event.time }}</span>
        <span class="event-title">{{ event.title }}</span>
      </div>
    </div>
  </div>
</template>

<script>
  import BaseButton from './BaseButton.vue';

  export default {
    name: 'BaseCalendar',
    components: {
      BaseButton,
    },
    props: {
      modelValue: {
        type: Date,
        default: null,
      },
      events: {
        type: Array,
        default: () => [],
      },
      minDate: {
        type: Date,
        default: null,
      },
      maxDate: {
        type: Date,
        default: null,
      },
      showEventList: {
        type: Boolean,
        default: false,
      },
      locale: {
        type: String,
        default: 'ko-KR',
      },
    },
    emits: ['update:modelValue', 'date-select', 'event-click'],
    data() {
      return {
        currentDate: new Date(),
        selectedDate: null,
        weekdays: ['일', '월', '화', '수', '목', '금', '토'],
      };
    },
    computed: {
      formattedCurrentDate() {
        return this.currentDate.toLocaleDateString(this.locale, {
          year: 'numeric',
          month: 'long',
        });
      },
      selectedDateText() {
        if (!this.selectedDate) return '';
        return this.selectedDate.toLocaleDateString(this.locale, {
          year: 'numeric',
          month: 'long',
          day: 'numeric',
        });
      },
      calendarDays() {
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const startDate = new Date(firstDay);
        const endDate = new Date(lastDay);

        // 달력 시작일 (이전 달의 마지막 주)
        startDate.setDate(startDate.getDate() - startDate.getDay());
        // 달력 종료일 (다음 달의 첫 주)
        endDate.setDate(endDate.getDate() + (6 - endDate.getDay()));

        const days = [];
        const currentDateObj = new Date(startDate);

        while (currentDateObj <= endDate) {
          const dayObj = {
            date: currentDateObj.getDate(),
            month: currentDateObj.getMonth(),
            year: currentDateObj.getFullYear(),
            fullDate: new Date(currentDateObj),
            isOtherMonth: currentDateObj.getMonth() !== month,
            isToday: this.isToday(currentDateObj),
            isSelected: this.isSelected(currentDateObj),
            isDisabled: this.isDisabled(currentDateObj),
            events: this.getEventsForDate(currentDateObj),
          };
          days.push(dayObj);
          currentDateObj.setDate(currentDateObj.getDate() + 1);
        }

        return days;
      },
      selectedDayEvents() {
        if (!this.selectedDate) return [];
        return this.getEventsForDate(this.selectedDate);
      },
    },
    watch: {
      modelValue: {
        handler(newValue) {
          if (newValue) {
            this.selectedDate = new Date(newValue);
            this.currentDate = new Date(newValue);
          }
        },
        immediate: true,
      },
    },
    methods: {
      previousMonth() {
        this.currentDate = new Date(
          this.currentDate.getFullYear(),
          this.currentDate.getMonth() - 1,
          1
        );
      },
      nextMonth() {
        this.currentDate = new Date(
          this.currentDate.getFullYear(),
          this.currentDate.getMonth() + 1,
          1
        );
      },
      selectDate(day) {
        if (day.isDisabled) return;

        this.selectedDate = new Date(day.fullDate);
        this.$emit('update:modelValue', this.selectedDate);
        this.$emit('date-select', this.selectedDate);
      },
      isToday(date) {
        const today = new Date();
        return (
          date.getDate() === today.getDate() &&
          date.getMonth() === today.getMonth() &&
          date.getFullYear() === today.getFullYear()
        );
      },
      isSelected(date) {
        if (!this.selectedDate) return false;
        return (
          date.getDate() === this.selectedDate.getDate() &&
          date.getMonth() === this.selectedDate.getMonth() &&
          date.getFullYear() === this.selectedDate.getFullYear()
        );
      },
      isDisabled(date) {
        if (this.minDate && date < this.minDate) return true;
        if (this.maxDate && date > this.maxDate) return true;
        return false;
      },
      getEventsForDate(date) {
        return this.events.filter(event => {
          const eventDate = new Date(event.date);
          return (
            eventDate.getDate() === date.getDate() &&
            eventDate.getMonth() === date.getMonth() &&
            eventDate.getFullYear() === date.getFullYear()
          );
        });
      },
    },
  };
</script>

<style scoped>
  .base-calendar {
    background: white;
    border: 1px solid var(--color-gray-200);
    border-radius: 8px;
    padding: 1rem;
    font-family: inherit;
  }

  .calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
  }

  .calendar-title {
    font-size: 1.125rem;
    font-weight: 600;
    color: var(--color-neutral-dark);
    margin: 0;
  }

  .arrow {
    font-size: 1.25rem;
    font-weight: bold;
  }

  .calendar-weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 1px;
    margin-bottom: 1px;
  }

  .weekday {
    padding: 0.5rem;
    text-align: center;
    font-size: 0.875rem;
    font-weight: 600;
    color: var(--color-gray-600);
    background-color: var(--color-gray-50);
  }

  .calendar-days {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 1px;
    background-color: var(--color-gray-200);
  }

  .calendar-day {
    min-height: 80px;
    padding: 0.5rem;
    background-color: white;
    cursor: pointer;
    position: relative;
    transition: all 0.2s ease;
    display: flex;
    flex-direction: column;
  }

  .calendar-day:hover {
    background-color: var(--color-gray-50);
  }

  .calendar-day.other-month {
    background-color: var(--color-gray-25);
    color: var(--color-gray-400);
  }

  .calendar-day.today {
    background-color: var(--color-primary-50);
    color: var(--color-primary-700);
  }

  .calendar-day.selected {
    background-color: var(--color-primary-500);
    color: white;
  }

  .calendar-day.disabled {
    background-color: var(--color-gray-100);
    color: var(--color-gray-300);
    cursor: not-allowed;
  }

  .calendar-day.disabled:hover {
    background-color: var(--color-gray-100);
  }

  .day-number {
    font-size: 0.875rem;
    font-weight: 500;
    margin-bottom: 0.25rem;
  }

  .day-events {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .event-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background-color: var(--color-primary-500);
  }

  .event-dot.event-primary {
    background-color: var(--color-primary-500);
  }

  .event-dot.event-success {
    background-color: var(--color-success-500);
  }

  .event-dot.event-warning {
    background-color: var(--color-warning-500);
  }

  .event-dot.event-error {
    background-color: var(--color-error-500);
  }

  .more-events {
    font-size: 0.75rem;
    color: var(--color-gray-500);
    margin-top: 2px;
  }

  .event-list {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid var(--color-gray-200);
  }

  .event-list h4 {
    font-size: 1rem;
    font-weight: 600;
    margin: 0 0 0.75rem 0;
    color: var(--color-neutral-dark);
  }

  .event-item {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.5rem;
    margin-bottom: 0.5rem;
    border-radius: 4px;
    border-left: 3px solid var(--color-primary-500);
    background-color: var(--color-gray-25);
  }

  .event-item.event-success {
    border-left-color: var(--color-success-500);
  }

  .event-item.event-warning {
    border-left-color: var(--color-warning-500);
  }

  .event-item.event-error {
    border-left-color: var(--color-error-500);
  }

  .event-time {
    font-size: 0.875rem;
    color: var(--color-gray-600);
    min-width: 60px;
  }

  .event-title {
    font-size: 0.875rem;
    color: var(--color-neutral-dark);
  }

  @media (max-width: 768px) {
    .calendar-day {
      min-height: 60px;
      padding: 0.25rem;
    }

    .day-number {
      font-size: 0.75rem;
    }

    .event-dot {
      width: 4px;
      height: 4px;
    }

    .more-events {
      font-size: 0.625rem;
    }
  }
</style>
