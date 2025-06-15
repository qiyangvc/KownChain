<!-- filepath: /Users/cjc/KnowChain/src/components/RelationGraph.vue -->
<template>
  <div class="relation-graph-container">
    <h2>文件关系图谱</h2>
    <div ref="graph" class="graph-canvas"></div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  nodes: Array,
  links: Array
})

const graph = ref(null)

function renderGraph() {
  if (!graph.value || !props.nodes || !props.links) return
  import('vis-network').then(({ Network }) => {
    const data = {
      nodes: new window.vis.DataSet(props.nodes),
      edges: new window.vis.DataSet(props.links)
    }
    const options = {
      nodes: { shape: 'box', font: { size: 14 } },
      edges: { arrows: 'to' },
      physics: { stabilization: true }
    }
    new Network(graph.value, data, options)
  })
}

onMounted(renderGraph)
watch([() => props.nodes, () => props.links], renderGraph)
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
  height: 320px;
  min-height: 200px;
}
</style>