<template>
  <div class="file-node" v-if="showFiles || isDirectory">    <div 
      class="node-content" 
      @click="handleNodeClick"
      @contextmenu="handleContextMenu"      :class="{ 
        'is-folder': isDirectory, 
        'is-file': !isDirectory,
        'selected': isSelected
      }"
    ><!-- 文件/文件夹图标 -->
      <span class="icon">
        <i v-if="isDirectory" :class="expanded ? 'folder-open' : 'folder'"></i>
        <i v-else :class="getFileIcon(fileName)"></i>
      </span>
      
      <!-- 文件/文件夹名称 -->
      <span class="name">{{ fileName }}</span>
    </div>    <!-- 子节点，仅在文件夹被展开时显示 -->
    <div v-if="isDirectory && expanded && node.children?.length" class="children">      <file-tree-node
        v-for="child in node.children"
        :key="child.fid || child.id || child.fname || child.fName"
        :node="child"
        :level="level + 1"
        :show-files="showFiles"
        :selected-node="selectedNode"
        @node-click="onChildClick"
        @node-context-menu="onChildContextMenu"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  node: {
    type: Object,
    required: true
  },
  level: {
    type: Number,
    default: 0
  },
  showFiles: {
    type: Boolean,
    default: true
  },
  selectedNode: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['node-click', 'node-context-menu']);

// 控制文件夹展开/收起状态 - 第一层文件夹默认展开
const expanded = ref(props.level === 0 && (props.node.isDir || props.node.dir));

// 处理不同字段名格式的计算属性
const fileName = computed(() => {
  return props.node.fName || props.node.fname || props.node.name || '';
});

const isDirectory = computed(() => {
  return props.node.isDir || props.node.dir || false;
});

const isSelected = computed(() => {
  if (!props.selectedNode) return false;
  const nodeId = props.node.fid || props.node.id || props.node.URL;
  const selectedId = props.selectedNode.fid || props.selectedNode.id || props.selectedNode.URL;
  return nodeId === selectedId;
});

// 处理节点点击事件
const handleNodeClick = () => {
  // 如果是文件夹，切换展开状态
  if (isDirectory.value) {
    expanded.value = !expanded.value;
  }
  
  // 触发点击事件，传递节点信息
  emit('node-click', props.node);
};

// 处理右键点击事件
const handleContextMenu = (event) => {
  // 阻止默认右键菜单
  event.preventDefault();
  // 阻止事件冒泡，防止被父级容器的右键事件覆盖
  event.stopPropagation();
  
  // 触发右键菜单事件，无论是文件还是文件夹都应该触发
  emit('node-context-menu', {
    node: props.node,
    event: event
  });
};

// 从子节点传递点击事件
const onChildClick = (node) => {
  emit('node-click', node);
};

// 从子节点传递右键菜单事件
const onChildContextMenu = (data) => {
  emit('node-context-menu', data);
};

// 根据文件名获取对应的图标类名
const getFileIcon = (fileName) => {
  if (!fileName || typeof fileName !== 'string') return 'file-default';
  if (fileName.endsWith('.md')) return 'file-md';
  if (fileName.endsWith('.pdf')) return 'file-pdf';
  if (fileName.endsWith('.doc') || fileName.endsWith('.docx')) return 'file-doc';
  if (fileName.endsWith('.xls') || fileName.endsWith('.xlsx')) return 'file-xls';
  if (fileName.endsWith('.ppt') || fileName.endsWith('.pptx')) return 'file-ppt';
  if (fileName.endsWith('.jpg') || fileName.endsWith('.png') || fileName.endsWith('.gif')) return 'file-img';
  return 'file-default';
};
</script>

<style scoped>
.file-node {
  margin: 2px 0;
}

.node-content {
  display: flex;
  align-items: center;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
}

.node-content:hover {
  background-color: #eee;
}

.is-folder {
  font-weight: 500;
}

.is-file.node-content:hover {
  background-color: #e3f2fd;
}

.icon {
  margin-right: 8px;
  width: 16px;
  text-align: center;
  font-size: 18px;
}

.name {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.children {
  padding-left: 24px;
  margin-top: 2px;
}

/* 图标样式 */
.folder::before {
  content: "📁";
}

.folder-open::before {
  content: "📂";
}

.file-md::before {
  content: "📄";
}

.file-pdf::before {
  content: "📑";
}

.file-doc::before {
  content: "📝";
}

.file-xls::before {
  content: "📊";
}

.file-ppt::before {
  content: "📽️";
}

.file-img::before {
  content: "🖼️";
}

.file-default::before {
  content: "📎";
}

/* 选中状态样式 */
.node-content.selected {
  background-color: #e3f2fd !important;
  color: #1976d2 !important;
  font-weight: 500;
}
</style>
