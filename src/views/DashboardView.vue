<template>
  <div class="dashboard-container">
    <h1>学习看板</h1>
    <div class="relation-graph-container">
      <h2>文件关系图谱</h2>
      <div ref="graph" class="graph-canvas"></div>
    </div>
    <div class="content">
      <p>这里是学习看板页面内容</p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { Network } from 'vis-network/standalone/esm/vis-network'

const store = useAuthStore()
const resourceTree = computed(() => store.resourceTree || []);
const allFileContents = computed(() => store.allFileContents || {});

const graph = ref(null)
let network = null

const graphNodes = computed(() =>
  (resourceTree.value || []).map(file => ({
    id: file.fid,
    label: file.fName
  }))
)

const graphLinks = computed(() => {
  const links = [];
  if (!Array.isArray(resourceTree.value)) return links;
  resourceTree.value.forEach(file => {
    const content = allFileContents.value?.[file.fid];
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

function renderGraph() {
  if (!graph.value) return;
  const data = {
    nodes: graphNodes.value,
    edges: graphLinks.value
  };
  const options = {
    nodes: {
      shape: 'dot',
      size: 18,
      font: { size: 16 }
    },
    edges: {
      arrows: 'to',
      color: '#aaa'
    },
    physics: {
      stabilization: true
    }
  };
  if (network) network.destroy();
  network = new Network(graph.value, data, options);
}

onMounted(() => {
  renderGraph();
});
watch([graphNodes, graphLinks], () => {
  renderGraph();
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
