<template>
  <div class="resource-container">
    <h1>资源管理</h1>
    
    <!-- 分为左右两部分的布局 -->
    <div class="resource-layout">
      <!-- 左侧文件树 -->
      <div class="file-tree-panel" :style="{ width: fileTreeWidth + 'px' }">
        <h3 class="panel-title">文件资源</h3>
        <div v-if="isLoadingTree" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>
        <div v-else-if="treeError" class="error-message">
          {{ treeError }}
        </div>        <div v-else class="tree-container">
          <file-tree-node 
            v-for="item in resourceTree" 
            :key="item.fid" 
            :node="item" 
            :level="0"
            @node-click="handleNodeClick"
          />
        </div>
        
        <!-- 添加调整大小的分隔线 -->
        <Resizer :onResize="handleTreeResize" />
      </div>
      
      <!-- 右侧文件内容 -->
      <div class="file-content-panel">
        <div v-if="!currentFile" class="empty-state">
          <p>选择一个文件以查看内容</p>
        </div>
        <div v-else>          <div class="file-header">
            <h2>{{ currentFileName }}</h2>
            <button class="close-button" @click="closeFile" title="关闭文件">
              <span>×</span>
            </button>
          </div>
          
          <div v-if="isLoadingContent" class="loading-state">
            <div class="spinner"></div>
            <span>加载内容中...</span>
          </div>
          <div v-else-if="contentError" class="error-message">
            {{ contentError }}
          </div>
          <div v-else class="markdown-content" v-html="renderedContent"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { marked } from 'marked'; 
import FileTreeNode from '@/components/FileTreeNode.vue';
import Resizer from '@/components/Resizer.vue';

const store = useAuthStore();

// 文件树宽度状态
const fileTreeWidth = ref(220); // 初始宽度
const minTreeWidth = 160; // 最小宽度
const maxTreeWidth = 400; // 最大宽度

// 处理文件树大小调整
const handleTreeResize = (deltaX) => {
  fileTreeWidth.value += deltaX;
  // 限制最小和最大宽度
  if (fileTreeWidth.value < minTreeWidth) fileTreeWidth.value = minTreeWidth;
  if (fileTreeWidth.value > maxTreeWidth) fileTreeWidth.value = maxTreeWidth;
};

// 配置marked选项
marked.setOptions({
  breaks: true,         // 允许换行符转换为 <br>
  gfm: true,            // 使用GitHub风格的Markdown
  headerIds: true,      // 为标题添加id
  mangle: false,        // 不转义内联HTML
  pedantic: false,      // 不使用严格模式
  sanitize: false,      // 不清理HTML标签
  silent: false,        // 不忽略错误
  smartLists: true,     // 使用智能列表
  smartypants: false,   // 不使用智能标点
  xhtml: false          // 不使用自闭合标签
});

// 计算属性
const resourceTree = computed(() => store.resourceTree);
const currentFile = computed(() => store.currentFile);
const isLoadingTree = computed(() => store.isLoadingTree);
const isLoadingContent = computed(() => store.isLoadingContent);
const treeError = computed(() => store.treeError);
const contentError = computed(() => store.contentError);

// 安全获取当前文件名
const currentFileName = computed(() => {
  if (!currentFile.value) return '';
  return currentFile.value.fName || currentFile.value.fname || '未知文件';
});

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!store.currentFileContent) return '';
  return marked(store.currentFileContent);
});

// 处理文件节点点击
const handleNodeClick = async (node) => {
  if (node.isDir || node.dir) return; // 如果是目录，不进行操作
  
  // 设置当前文件
  store.setCurrentFile(node);
  
  // 使用文件ID获取文件内容
  const fileId = node.fid || node.id || node.URL;
  if (fileId) {
    try {
      await store.fetchFileContent(fileId);
    } catch (error) {
      console.error('获取文件内容失败', error);
    }
  }
};

// 关闭当前文件
const closeFile = () => {
  store.closeCurrentFile();
};

// 组件挂载时获取资源树
onMounted(async () => {
  console.log('ResourceView: 组件挂载，开始获取资源树')
  try {
    await store.fetchResourceTree();
    console.log('ResourceView: 资源树获取成功')
  } catch (error) {
    console.error('ResourceView: 获取资源树失败', error);
    // 不阻止页面渲染，只记录错误
  }
});

// 组件卸载时清理状态
onUnmounted(() => {
  console.log('ResourceView: 组件卸载，清理状态')
  // 不清理资源树，因为其他页面可能需要
  // 只清理当前文件状态
  store.closeCurrentFile();
});
</script>

<style scoped>
.resource-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.resource-layout {
  display: flex;
  height: calc(100% - 60px); /* 减去标题的高度 */
}

.file-tree-panel {
  position: relative; /* 为 Resizer 提供定位上下文 */
  height: 100%;
  border-right: 1px solid #e0e0e0;
  padding: 15px;
  overflow-y: auto;
  background-color: #f9f9f9;
  flex-shrink: 0;
}

.panel-title {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
  font-size: 16px;
  font-weight: 500;
}

.file-content-panel {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #666;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #e74c3c;
  padding: 15px;
  background-color: #fdecea;
  border-radius: 4px;
  margin: 10px 0;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50%;
  color: #666;
  font-style: italic;
}

.file-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.close-button {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid #e0e0e0;
  background-color: #fff;
  color: #666;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.close-button:hover {
  background-color: #f44336;
  color: white;
  border-color: #f44336;
}

.markdown-content {
  line-height: 1.6;
  color: #000000; /* 使用纯黑色提高对比度 */
  font-weight: normal;
}

/* 增强 Markdown 样式 */
.markdown-content :deep(h1) { font-size: 2em; margin-top: 0.67em; margin-bottom: 0.67em; font-weight: 700; color: #000000; }
.markdown-content :deep(h2) { font-size: 1.5em; margin-top: 0.83em; margin-bottom: 0.83em; font-weight: 700; color: #000000; }
.markdown-content :deep(h3) { font-size: 1.17em; margin-top: 1em; margin-bottom: 1em; font-weight: 600; color: #000000; }
.markdown-content :deep(p) { margin-top: 1em; margin-bottom: 1em; color: #000000; }
.markdown-content :deep(ul), .markdown-content :deep(ol) { padding-left: 2em; color: #000000; }
.markdown-content :deep(li) { margin-bottom: 0.5em; color: #000000; }
.markdown-content :deep(code) { background-color: #f4f4f4; padding: 0.2em 0.4em; border-radius: 3px; color: #d63384; font-weight: 500; }
.markdown-content :deep(pre) { background-color: #f4f4f4; padding: 1em; overflow-x: auto; color: #333; font-weight: normal; }
.markdown-content :deep(blockquote) { border-left: 4px solid #ddd; padding-left: 1em; margin-left: 0; color: #333; }
.markdown-content :deep(img) { max-width: 100%; }
.markdown-content :deep(a) { 
  color: #0d6efd; 
  text-decoration: underline; /* 将无下划线改为有下划线 */
  font-weight: 500; 
}
.markdown-content :deep(a):hover { 
  text-decoration: underline; 
  color: #0a58ca; /* 添加悬停时的颜色变化，使交互效果更明显 */
}
.markdown-content :deep(strong), .markdown-content :deep(b) { font-weight: 700; color: #000000; }
.markdown-content :deep(em), .markdown-content :deep(i) { font-style: italic; }

/* 表格样式 */
.markdown-content :deep(table) { 
  border-collapse: collapse; 
  width: 100%; 
  margin: 1em 0; 
  color: #000000;
}
.markdown-content :deep(th), .markdown-content :deep(td) { 
  border: 1px solid #ddd; 
  padding: 8px; 
  text-align: left; 
}
.markdown-content :deep(th) { 
  background-color: #f2f2f2; 
  font-weight: 600;
}
.markdown-content :deep(tr:nth-child(even)) { 
  background-color: #f8f8f8; 
}

/* 代码块样式增强 */
.markdown-content :deep(pre code) {
  display: block;
  padding: 1em;
  color: #333;
  font-weight: 400;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  line-height: 1.5;
}
</style>
