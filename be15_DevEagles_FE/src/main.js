import './assets/base.css';
import '@/assets/css/styleguide.css';
import '@/assets/css/components.css';
import '@/assets/css/tooltip.css';

import { createApp } from 'vue';
import App from './App.vue';
import router from './routes';

createApp(App).use(router).mount('#app');
