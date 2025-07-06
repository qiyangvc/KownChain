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
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
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

// 递归收集所有文件节点（添加防御性编程）
function collectFiles(tree, files = []) {
  if (!tree || !Array.isArray(tree)) return files;
  
  tree.forEach(item => {
    // 防御性检查：确保item是有效对象
    if (!item || typeof item !== 'object') return;
    
    // 统一字段名处理（支持多种可能的字段名）
    const isDirectory = item.isDir || item.dir || false;
    const children = item.children || item.child || [];
    const fileName = item.fName || item.fname || item.name || 'unnamed';
    const fileId = item.fid || item.id || item.URL;
    
    if (isDirectory && Array.isArray(children) && children.length > 0) {
      collectFiles(children, files);
    } else if (!isDirectory && fileId) {
      // 只收集有有效ID的非目录文件
      files.push({
        ...item,
        fName: fileName,
        fid: fileId,
        isDir: isDirectory
      });
    }
  });
  return files;
}

// 递归构建节点和父子边（添加防御性编程）
function buildGraph(tree, parent = null, level = 0) {
  if (!tree || !Array.isArray(tree)) return;
  
  tree.forEach(item => {
    // 防御性检查：确保item是有效对象
    if (!item || typeof item !== 'object') return;
    
    // 统一字段名处理（支持多种可能的字段名）
    const fileName = item.fName || item.fname || item.name || 'unnamed';
    const fileId = item.fid || item.id || item.URL;
    const isDirectory = item.isDir || item.dir || false;
    const children = item.children || item.child || [];
    
    // 确保节点有有效的ID
    if (!fileId) return;
    
    nodes.value.push({
      id: fileId,
      label: fileName,
      isDir: !!isDirectory,
      level
    });
    
    // 建立父子关系
    if (parent) {
      const parentId = parent.fid || parent.id || parent.URL;
      if (parentId) {
        links.value.push({ from: parentId, to: fileId, type: 'tree' });
      }
    }
    
    // 递归处理子节点
    if (Array.isArray(children) && children.length > 0) {
      buildGraph(children, { ...item, fid: fileId }, level + 1);
    }
  });
}

// 新增：分析内容跳转关系（增强防御性编程）
async function buildContentLinks(files) {
  if (!files || !Array.isArray(files)) return;
  
  // 建立文件名/URL到fid的映射
  const fileMap = {};
  files.forEach(f => {
    if (!f || typeof f !== 'object') return;
    
    const fileName = f.fName || f.fname || f.name;
    const fileId = f.fid || f.id || f.URL;
    const fileUrl = f.fid || f.id || f.URL || f.url;
    
    if (fileName && fileId) {
      fileMap[fileName] = fileId;
    }
    if (fileUrl && fileId) {
      fileMap[fileUrl] = fileId;
    }
  });

  for (const [idx, file] of files.entries()) {
    if (!file || typeof file !== 'object') continue;
    
    const fileId = file.fid || file.id || file.URL;
    const fileUrl = file.URL || file.url;
    
    if (!fileId) continue;
    
    // 获取内容
    let content = '';
    try {
      const res = await api.getFileContent(fileId || fileUrl);
      if (res && res.data && typeof res.data === 'string') {
        content = res.data;
      }
    } catch (e) {
      console.warn('获取文件内容失败:', fileUrl || fileId, e);
      continue;
    }
    
    if (!content) continue;
    
    // 匹配 Markdown 链接
    const regex = /\[.*?\]\((.*?)\)/g;
    let match;
    while ((match = regex.exec(content)) !== null) {
      const target = match[1];
      if (!target || typeof target !== 'string') continue;
      
      // 支持目标为文件名或URL
      const targetFid = fileMap[target];
      if (targetFid && targetFid !== fileId) { // 避免自引用
        links.value.push({ from: fileId, to: targetFid, type: 'content' });
      }
    }
    
    // 每处理完一个文件，实时刷新图谱
    try {
      renderGraph();
    } catch (e) {
      console.warn('渲染图谱失败:', e);
    }
  }
}

async function initGraph() {
  try {
    loading.value = true;
    nodes.value = [];
    links.value = [];
    
    // 防御性检查props.resourceTree
    if (!props.resourceTree || !Array.isArray(props.resourceTree)) {
      console.warn('资源树数据无效或为空');
      return;
    }
    
    buildGraph(props.resourceTree);
    const files = collectFiles(props.resourceTree);
    
    if (files && files.length > 0) {
      await buildContentLinks(files);
    }
    
    await nextTick();
    renderGraph();
  } catch (error) {
    console.error('初始化图谱失败:', error);
  } finally {
    loading.value = false;
  }
}

function renderGraph() {
  try {
    if (!graph.value) {
      console.warn('图谱容器DOM元素未找到');
      return;
    }
    
    // 防御性检查节点和边数据
    const validNodes = (nodes.value || []).filter(n => 
      n && typeof n === 'object' && n.id !== undefined && n.id !== null
    );
    
    const validLinks = (links.value || []).filter(l => 
      l && typeof l === 'object' && l.from !== undefined && l.to !== undefined
    );
    
    const data = {
      nodes: validNodes.map(n => ({
        id: n.id,
        label: String(n.label || 'unnamed'),
        color: n.isDir ? '#1976d2' : '#f44336',
        shape: 'dot',
        size: n.isDir ? Math.max(12, 30 - (n.level || 0) * 6) : 14,
        font: { size: 16 }
      })),
      edges: validLinks.map(l => ({
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
    
    // 销毁之前的网络实例
    if (network) {
      try {
        network.destroy();
      } catch (e) {
        console.warn('销毁网络实例失败:', e);
      }
    }
    
    // 创建新的网络实例
    network = new Network(graph.value, data, options);
  } catch (error) {
    console.error('渲染图谱失败:', error);
  }
}

// 清理函数
function cleanup() {
  if (network) {
    try {
      network.destroy();
    } catch (e) {
      console.warn('清理网络实例失败:', e);
    }
    network = null;
  }
}

onMounted(() => {
  initGraph();
});

// 组件卸载时清理
onUnmounted(() => {
  cleanup();
});

// 监听资源树变化，增加防御性检查
watch(() => props.resourceTree, (newTree, oldTree) => {
  // 只有在数据真正改变时才重新初始化
  if (JSON.stringify(newTree) !== JSON.stringify(oldTree)) {
    initGraph();
  }
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