<template>
  <div class="dashboard-container">
    <h1>学习看板</h1>
    <RelationGraph :nodes="graphNodes" :links="graphLinks" />
    <div class="content">
      <p>这里是学习看板页面内容</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import RelationGraph from '@/components/RelationGraph.vue'

const store = useAuthStore()
const resourceTree = computed(() => store.resourceTree)
const allFileContents = computed(() => store.allFileContents) // 你需要在store里维护

const graphNodes = computed(() =>
  resourceTree.value.map(file => ({
    id: file.fid,
    label: file.fName
  }))
)

const graphLinks = computed(() => {
  const links = [];
  resourceTree.value.forEach(file => {
    const content = allFileContents.value[file.fid];
    if (!content) return;
    const regex = /\[.*?\]\((.*?)\)/g;
    let match;
    while ((match = regex.exec(content)) !== null) {
      const url = match[1];
      const target = resourceTree.value.find(f => url.includes(f.fName));
      if (target) {
        links.push({ from: file.fid, to: target.fid });
      }
    }
  });
  return links;
});
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.content {
  margin-top: 20px;
}

.relation-graph-container {
  padding: 10px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
}

.graph-canvas {
  width: 100%;
  height: 320px;
  min-height: 200px;
}
</style>
