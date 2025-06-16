<!-- filepath: /Users/cjc/KnowChain/src/components/RelationGraph.vue -->
<template>
  <div class="relation-graph-container">
    <h2>文件关系图谱</h2>
    <div ref="graph" class="graph-canvas"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import api from '@/api/auth'
import { Network } from 'vis-network/standalone/esm/vis-network'

const nodes = ref([])
const links = ref([])
const graph = ref(null)
let network = null

async function fetchGraph() {
  const res = await api.getKnowledgeGraph();
  nodes.value = res.data.nodes;
  links.value = res.data.links;
  // 调试输出
  console.log('nodes for render', nodes.value);
  console.log('links for render', links.value);
}

function renderGraph() {
  if (!graph.value) return;
  const data = {
    nodes: nodes.value.map(n => ({
      id: n.id,
      label: n.label,
      color: n.isDir ? '#1976d2' : '#f44336',
      shape: 'dot',
      size: n.isDir ? 30 - (n.level || 0) * 6 : 14,
      font: { size: 16 }
    })),
    edges: links.value.map(l => ({ from: l.from, to: l.to }))
  };
  const options = {
    nodes: { font: { size: 16 } },
    edges: { arrows: 'to', color: '#aaa' },
    physics: { stabilization: true }
  };
  if (network) network.destroy();
  network = new Network(graph.value, data, options);
}

onMounted(async () => {
  await fetchGraph();
  await nextTick();
  renderGraph();
});

watch([nodes, links], () => {
  if (graph.value) renderGraph();
});
</script>

<style scoped>
.relation-graph-container {
  padding: 10px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
}
.graph-canvas {
  width: 100%;
  height: 400px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  margin-top: 16px;
}
</style>