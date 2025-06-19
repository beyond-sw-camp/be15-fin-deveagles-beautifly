import CampaignList from './views/CampaignList.vue';
import CampaignDetail from './views/CampaignDetail.vue';

export default [
  {
    path: '/campaigns',
    name: 'CampaignList',
    component: CampaignList,
    meta: {
      title: '캠페인 목록',
    },
  },
  {
    path: '/campaigns/:id',
    name: 'CampaignDetail',
    component: CampaignDetail,
    meta: {
      title: '캠페인 상세',
    },
  },
];
