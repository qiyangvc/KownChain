<template>
  <BaseDialog
    :show="show"
    :title="title"
    :description="description"
    :confirm-text="confirmText"
    :cancel-text="cancelText"
    :error-message="errorMessage"
    dialog-class="large"
    @confirm="handleConfirm"
    @cancel="$emit('cancel')"
    @close="$emit('close')"
  >
    <div class="tree-container">
      <div class="tree-option" @click="selectTarget(null)" :class="{ 'selected': selectedTarget === null }">
        <span class="folder-icon">📁</span> {{ rootText }}
      </div>
      <FileTreeNode 
        v-for="item in treeData" 
        :key="item.fid" 
        :node="item" 
        :level="0"
        :show-files="showFiles"
        :selected-node="selectedTarget"
        @node-click="handleNodeClick"
        @node-context-menu="() => {}"
        class="tree-node"
      />
    </div>
  </BaseDialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import BaseDialog from './BaseDialog.vue';
import FileTreeNode from './FileTreeNode.vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  confirmText: {
    type: String,
    default: '确认'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  errorMessage: {
    type: String,
    default: ''
  },
  treeData: {
    type: Array,
    default: () => []
  },
  showFiles: {
    type: Boolean,
    default: false
  },
  rootText: {
    type: String,
    default: '根目录'
  },
  excludeNode: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['confirm', 'cancel', 'close']);

const selectedTarget = ref(null);

// 重置选择
watch(() => props.show, (newShow) => {
  if (newShow) {
    selectedTarget.value = null;
  }
});

const selectTarget = (target) => {
  selectedTarget.value = target;
};

const handleNodeClick = (node) => {
  // 只有文件夹可以被选中作为目标
  if (node.isDir || node.dir) {
    // 如果有排除节点，检查是否是要排除的节点
    if (props.excludeNode) {
      const nodeId = node.fid || node.id || node.URL;
      const excludeId = props.excludeNode.fid || props.excludeNode.id || props.excludeNode.URL;
      if (nodeId === excludeId) {
        return; // 不允许选择被排除的节点
      }
    }
    selectedTarget.value = node;
  }
};

const handleConfirm = () => {
  emit('confirm', selectedTarget.value);
};
</script>

<style scoped>
.tree-container {
  border: 1px solid #ddd;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
  padding: 10px;
}

.tree-option {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s;
  margin-bottom: 5px;
}

.tree-option:hover {
  background-color: #f0f0f0;
}

.tree-option.selected {
  background-color: #e3f2fd;
  color: #1976d2;
  font-weight: 500;
}

.folder-icon {
  font-size: 16px;
}

.tree-node {
  margin-bottom: 2px;
}

:deep(.node-content) {
  cursor: pointer;
  transition: background-color 0.2s;
}

:deep(.node-content:hover) {
  background-color: #f0f0f0;
}
</style>
