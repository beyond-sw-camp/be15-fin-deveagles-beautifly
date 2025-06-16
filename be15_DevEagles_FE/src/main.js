import './assets/base.css';
import '@/assets/css/styleguide.css';
import '@/assets/css/components.css';
import '@/assets/css/tooltip.css';

import { createApp } from 'vue';
import App from './App.vue';
import router from './routes';
import VCalendar from 'v-calendar';
import 'v-calendar/style.css';
import darkModePlugin from './plugins/darkMode.js';

const app = createApp(App);

app.use(router);
app.use(VCalendar, {});
app.use(darkModePlugin);

app.mount('#app');
