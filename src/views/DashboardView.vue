<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="dashboard-title-bar"></div>
      <h1 class="dashboard-title">学习看板</h1>
    </div>
    <div class="dashboard-card">
      <RelationGraph :resourceTree="store.resourceTree" />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import RelationGraph from '@/components/RelationGraph.vue'

const store = useAuthStore()

onMounted(async () => {
  if (!store.resourceTree || store.resourceTree.length === 0) {
    await store.fetchResourceTree()
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 32px 0 0 0;
  background: #f6f8fa;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-left: 24px;
}

.dashboard-title-bar {
  width: 6px;
  height: 32px;
  background: #4a6cf7;
  border-radius: 3px;
  margin-right: 14px;
}

.dashboard-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: #22223b;
  letter-spacing: 1px;
  margin: 0;
}

.dashboard-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(74,108,247,0.07), 0 1.5px 6px rgba(0,0,0,0.03);
  padding: 32px 28px 24px 28px;
  max-width: 1000px; /* 更宽 */
  margin: 0 auto;
}

.relation-graph-container {
  padding: 10px 0 0 0;
  background: transparent;
  border: none;
  border-radius: 0;
  margin-bottom: 0;
}

.graph-canvas {
  width: 100%;
  height: 400px;
  min-height: 240px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  margin-top: 16px;
}
</style>
