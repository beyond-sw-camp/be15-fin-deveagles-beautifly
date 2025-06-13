<template>
  <div class="home">
    <div class="dashboard-header">
      <h1 class="font-screen-title text-neutral-dark">대시보드</h1>
      <p class="font-body text-gray-600">DevEagles CRM 시스템에 오신 것을 환영합니다.</p>
    </div>

    <div class="dashboard-grid">
      <!-- 통계 카드들 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="font-section-inner text-gray-700">오늘 예약</h3>
            <CalendarIcon :size="24" class="stat-icon" />
          </div>
          <div class="stat-value">
            <span class="font-screen-title text-primary-main">12</span>
            <span class="stat-change positive">+3</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-header">
            <h3 class="font-section-inner text-gray-700">이번 달 매출</h3>
            <DollarIcon :size="24" class="stat-icon" />
          </div>
          <div class="stat-value">
            <span class="font-screen-title text-primary-main">2,450,000원</span>
            <span class="stat-change positive">+15%</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-header">
            <h3 class="font-section-inner text-gray-700">신규 고객</h3>
            <UsersIcon :size="24" class="stat-icon" />
          </div>
          <div class="stat-value">
            <span class="font-screen-title text-primary-main">8</span>
            <span class="stat-change neutral">-</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-header">
            <h3 class="font-section-inner text-gray-700">취소/변경</h3>
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              class="stat-icon"
            >
              <path
                d="M1 4V10H7"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M23 20V14H17"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M20.49 9C20.0412 7.05369 18.9988 5.30968 17.5095 4.06331C16.0201 2.81693 14.1736 2.13743 12.2453 2.13743C10.3171 2.13743 8.47056 2.81693 6.98124 4.06331C5.49191 5.30968 4.44952 7.05369 4.00076 9L1 4V10H7"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M3.51 15C3.95886 16.9463 5.00122 18.6903 6.49055 19.9367C7.97987 21.1831 9.82641 21.8626 11.7547 21.8626C13.6829 21.8626 15.5294 21.1831 17.0188 19.9367C18.5081 18.6903 19.5505 16.9463 19.9992 15L23 20V14H17"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <div class="stat-value">
            <span class="font-screen-title text-primary-main">3</span>
            <span class="stat-change negative">+1</span>
          </div>
        </div>
      </div>

      <!-- 최근 활동 -->
      <div class="activity-section">
        <div class="card">
          <div class="card-header">
            <h2 class="card-title">최근 예약</h2>
            <router-link to="/reservation/list" class="btn btn-sm btn-outline btn-primary"
              >전체 보기</router-link
            >
          </div>
          <div class="recent-bookings">
            <div class="booking-item">
              <div class="booking-info">
                <div class="customer-name font-one-liner-semibold">김민수</div>
                <div class="service-name font-small text-gray-600">헤어컷 + 파마</div>
              </div>
              <div class="booking-time">
                <div class="time font-small-semibold">14:00</div>
                <div class="status badge badge-success">확정</div>
              </div>
            </div>

            <div class="booking-item">
              <div class="booking-info">
                <div class="customer-name font-one-liner-semibold">이지영</div>
                <div class="service-name font-small text-gray-600">염색 + 트리트먼트</div>
              </div>
              <div class="booking-time">
                <div class="time font-small-semibold">15:30</div>
                <div class="status badge badge-warning">대기</div>
              </div>
            </div>

            <div class="booking-item">
              <div class="booking-info">
                <div class="customer-name font-one-liner-semibold">박상호</div>
                <div class="service-name font-small text-gray-600">헤어컷</div>
              </div>
              <div class="booking-time">
                <div class="time font-small-semibold">16:00</div>
                <div class="status badge badge-success">확정</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { CalendarIcon, DollarIcon, UsersIcon } from '../components/icons';
</script>

<style scoped>
  .home {
    padding: 0;
  }

  .dashboard-header {
    margin-bottom: 2rem;
  }

  .dashboard-header h1 {
    margin: 0 0 0.5rem 0;
  }

  .dashboard-header p {
    margin: 0;
  }

  .dashboard-grid {
    display: grid;
    gap: 1.5rem;
  }

  .stats-row {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1rem;
  }

  .stat-card {
    background: var(--color-neutral-white);
    padding: 1.5rem;
    border-radius: 0.75rem;
    border: 1px solid var(--color-gray-200);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }

  .stat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
  }

  .stat-header h3 {
    margin: 0;
  }

  .stat-icon {
    color: var(--color-gray-500);
    opacity: 0.7;
  }

  .stat-value {
    display: flex;
    align-items: baseline;
    gap: 0.5rem;
  }

  .stat-value span:first-child {
    margin: 0;
  }

  .stat-change {
    font-size: 14px;
    font-weight: 600;
    padding: 0.125rem 0.5rem;
    border-radius: 0.25rem;
  }

  .stat-change.positive {
    color: var(--color-success-600);
    background-color: var(--color-success-50);
  }

  .stat-change.negative {
    color: var(--color-error-600);
    background-color: var(--color-error-50);
  }

  .stat-change.neutral {
    color: var(--color-gray-600);
    background-color: var(--color-gray-100);
  }

  .activity-section {
    grid-column: 1 / -1;
  }

  .recent-bookings {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .booking-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background-color: var(--color-gray-50);
    border-radius: 0.5rem;
  }

  .booking-info {
    flex: 1;
  }

  .customer-name {
    margin-bottom: 0.25rem;
  }

  .booking-time {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  @media (max-width: 768px) {
    .stats-row {
      grid-template-columns: 1fr;
    }
  }
</style>
