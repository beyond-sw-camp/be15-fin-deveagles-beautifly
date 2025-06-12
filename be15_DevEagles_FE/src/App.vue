<script setup>
// 기본 애플리케이션 설정
</script>

<template>
  <div class="app">
    <header class="app-header">
      <h1 class="font-screen-title text-primary-main">DevEagles</h1>
      <p class="font-body text-gray-600">프로젝트 설정이 완료되었습니다.</p>
    </header>

    <main class="app-main">
      <div class="container">
        <section class="welcome-section">
          <h2 class="font-section-title text-primary-500">환영합니다!</h2>
          <p class="font-body text-gray-700">
            DevEagles 프로젝트의 기본 설정이 완료되었습니다.<br>
            스타일가이드와 폰트, CSS 설정이 모두 적용되어 있습니다.
          </p>
          
          <div class="demo-colors">
            <div class="color-box bg-primary-main">Primary</div>
            <div class="color-box bg-secondary-main">Secondary</div>
            <div class="color-box bg-success-300">Success</div>
            <div class="color-box bg-error-300">Error</div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  padding: 2rem;
  text-align: center;
  background-color: var(--color-gray-50);
}

.app-main {
  flex: 1;
  padding: 2rem;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  padding: 2rem;
}

.welcome-section h2 {
  margin-bottom: 1rem;
}

.welcome-section p {
  margin-bottom: 2rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.demo-colors {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 2rem;
}

.color-box {
  padding: 1rem 2rem;
  border-radius: 8px;
  color: white;
  font-weight: 700;
  min-width: 120px;
  box-shadow: var(--shadow-drop, 0 8px 40px -10px rgba(0, 0, 0, 0.08));
}
</style>
