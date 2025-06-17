<!-- filepath: /Users/cjc/KnowChain/src/components/RelationGraph.vue -->
<template>
  <div class="relation-graph-container">
    <h2>文件关系图谱</h2>
    <div v-if="loading" class="loading-tip">
      正在加载中...（已加载节点数：{{ nodes.length }}）
    </div>
    <div ref="graph" class="graph-canvas"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import api from '@/api/auth'
import { Network } from 'vis-network/standalone/esm/vis-network'

const props = defineProps({
  resourceTree: { type: Array, required: true }
})

const nodes = ref([])
const links = ref([])
const graph = ref(null)
const loading = ref(false)
let network = null

// 递归收集所有文件节点
function collectFiles(tree, files = []) {
  if (!tree) return files;
  tree.forEach(item => {
    if (item.isDir && item.children && item.children.length) {
      collectFiles(item.children, files);
    } else if (!item.isDir) {
      files.push(item);
    }
  });
  return files;
}

// 递归构建节点和父子边
function buildGraph(tree, parent = null, level = 0) {
  if (!tree) return;
  tree.forEach(item => {
    nodes.value.push({
      id: item.fid,
      label: item.fName,
      isDir: !!item.isDir,
      level
    });
    if (parent) {
      links.value.push({ from: parent.fid, to: item.fid, type: 'tree' });
    }
    if (item.children && item.children.length) {
      buildGraph(item.children, item, level + 1);
    }
  });
}

// 新增：分析内容跳转关系
async function buildContentLinks(files) {
  // 建立文件名/URL到fid的映射
  const fileMap = {};
  files.forEach(f => {
    fileMap[f.fName] = f.fid;
    if (f.URL) fileMap[f.URL] = f.fid;
  });

  for (const [idx, file] of files.entries()) {
    // 获取内容
    let content = '';
    try {
      const res = await api.getFileContent(file.URL || file.fid);
      content = res.data;
    } catch (e) {
      continue;
    }
    // 匹配 Markdown 链接
    const regex = /\[.*?\]\((.*?)\)/g;
    let match;
    while ((match = regex.exec(content)) !== null) {
      const target = match[1];
      // 支持目标为文件名或URL
      const targetFid = fileMap[target];
      if (targetFid) {
        links.value.push({ from: file.fid, to: targetFid, type: 'content' });
      }
    }
    // 每处理完一个文件，实时刷新图谱
    renderGraph();
  }
}

async function initGraph() {
  loading.value = true;
  nodes.value = [];
  links.value = [];
  buildGraph(props.resourceTree);
  const files = collectFiles(props.resourceTree);
  await buildContentLinks(files);
  await nextTick();
  renderGraph();
  loading.value = false;
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
    edges: links.value.map(l => ({
      from: l.from,
      to: l.to,
      color: l.type === 'content' ? '#ff9800' : '#aaa',
      dashes: l.type === 'content'
    }))
  };
  const options = {
    nodes: { font: { size: 16 } },
    edges: { arrows: 'to' },
    physics: { stabilization: true }
  };
  if (network) network.destroy();
  network = new Network(graph.value, data, options);
}

onMounted(() => {
  initGraph();
});

watch(() => props.resourceTree, () => {
  initGraph();
}, { deep: true });
</script>

<style scoped>
.relation-graph-container {
  padding: 24px 24px 16px 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
  min-height: 450px; /* 增大整体高度 */
  box-shadow: 0 4px 24px rgba(74,108,247,0.07), 0 1.5px 6px rgba(0,0,0,0.03);
}
.graph-canvas {
  width: 100%;
  height: 450px;   /* 增大画布高度 */
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  margin-top: 16px;
}
.loading-tip {
  color: #1976d2;
  font-size: 16px;
  margin-bottom: 12px;
  font-weight: bold;
}
</style>