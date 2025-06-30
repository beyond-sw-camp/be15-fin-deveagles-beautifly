import './assets/base.css';
import '@/assets/css/styleguide.css';
import '@/assets/css/components.css';
import '@/assets/css/tooltip.css';
import '@/assets/css/primevue-theme.css';

import { createApp } from 'vue';
import App from './App.vue';
import router from './routes';

// PrimeVue imports
import PrimeVue from 'primevue/config';
import Aura from '@primevue/themes/aura';
import { createPinia } from 'pinia';

const app = createApp(App);
const pinia = createPinia();

app.use(router);
app.use(PrimeVue, {
  theme: {
    preset: Aura,
  },
  locale: {
    accept: '확인',
    reject: '취소',
    choose: '선택',
    upload: '업로드',
    cancel: '취소',
    dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
    dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
    dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
    monthNames: [
      '1월',
      '2월',
      '3월',
      '4월',
      '5월',
      '6월',
      '7월',
      '8월',
      '9월',
      '10월',
      '11월',
      '12월',
    ],
    monthNamesShort: [
      '1월',
      '2월',
      '3월',
      '4월',
      '5월',
      '6월',
      '7월',
      '8월',
      '9월',
      '10월',
      '11월',
      '12월',
    ],
    today: '오늘',
    clear: '닫기',
    dateFormat: 'yy-mm-dd',
    firstDayOfWeek: 0,
    prevText: '이전',
    nextText: '다음',
    hourText: '시',
    minuteText: '분',
    secondText: '초',
    am: '오전',
    pm: '오후',
    year: '년',
    month: '월',
    day: '일',
    hour: '시',
    minute: '분',
    second: '초',
    week: '주',
    dateIs: '날짜는',
    dateIsNot: '날짜가 아닙니다',
    dateBefore: '이전 날짜',
    dateAfter: '이후 날짜',
    chooseYear: '년도 선택',
    chooseMonth: '월 선택',
    chooseDate: '날짜 선택',
    prevDecade: '이전 10년',
    nextDecade: '다음 10년',
    prevYear: '이전 년도',
    nextYear: '다음 년도',
    prevMonth: '이전 월',
    nextMonth: '다음 월',
    prevHour: '이전 시간',
    nextHour: '다음 시간',
    prevMinute: '이전 분',
    nextMinute: '다음 분',
    prevSecond: '이전 초',
    nextSecond: '다음 초',
  },
});

app.use(pinia);
app.mount('#app');
