<template>
  <div class="workbench-container">
    <h1>学习工作台</h1>
    
    <!-- 分为左右多部分的布局 -->
    <div class="workbench-layout">
      <!-- 左侧文件树 -->
      <div class="file-tree-panel" :style="{ width: fileTreeWidth + 'px' }">
        <h3 class="panel-title">文件资源</h3>
        <div v-if="isLoadingTree" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>
        <div v-else-if="treeError" class="error-message">
          {{ treeError }}        </div>        <div v-else class="tree-container" @click="closeContextMenu">
          <div class="tree-header">
            <h3>文件管理</h3>            <div class="tree-actions">
              <button class="action-btn" @click="openCreateDialog('folder', null)" title="新建文件夹">
                <span class="icon">📁</span>
              </button>
              <button class="action-btn" @click="openCreateDialog('file', null)" title="新建文件">
                <span class="icon">📄</span>
              </button>
            </div>
          </div>
          <file-tree-node 
            v-for="item in resourceTree" 
            :key="item.fid" 
            :node="item" 
            :level="0"
            @node-click="handleNodeClick"
            @node-context-menu="handleNodeContextMenu"
          />
        </div>
        
        <!-- 添加调整大小的分隔线 -->
        <Resizer :onResize="handleTreeResize" />
      </div>
      
      <!-- 中间链接预览区域 - 仅在有预览内容时显示 -->
      <template v-if="previewFile">
        <div class="preview-panel" :style="{ width: previewPanelWidth + 'px' }">
          <div class="preview-header">            <div class="preview-title">
              <h3>预览: {{ previewFileName }}</h3>
              <span class="preview-path">{{ previewFile.URL }}</span>
            </div>
            <button class="close-button preview-close" @click="closePreview" title="关闭预览">
              <span>×</span>
            </button>
          </div>
          <div v-if="isLoadingPreview" class="loading-state">
            <div class="spinner"></div>
            <span>加载预览内容中...</span>
          </div>
          <div v-else-if="previewError" class="error-message">
            {{ previewError }}
          </div>
          <div
            v-else
            class="markdown-content preview-content"
            v-html="renderedPreviewContent"
            @click="handlePreviewContentClick"
          ></div>
        </div>
        <!-- 预览面板和右侧内容之间的分隔线 -->
        <Resizer :onResize="handlePreviewResize" />
      </template>
      
      <!-- 右侧文件内容 -->
      <div class="file-content-panel">
        <div v-if="!currentFile" class="empty-state">
          <p>选择一个文件以查看或编辑内容</p>
        </div>
        <div v-else>          <div class="file-header">
            <h2>{{ currentFileName }}</h2>
            <div class="file-actions">
              <button 
                v-if="!isEditing" 
                class="edit-button" 
                @click="startEditing"
                :disabled="!isEditableFile"
              >
                编辑
              </button>
              <button 
                v-else 
                class="save-button" 
                @click="saveContent"
              >
                保存
              </button>
              <button 
                v-if="isEditing" 
                class="cancel-button" 
                @click="cancelEditing"
              >
                取消
              </button>
              <span v-if="saveStatus" class="save-status success">{{ saveStatus }}</span>
              <span v-if="saveError" class="save-status error">{{ saveError }}</span>
              <button class="close-button" @click="closeFile" title="关闭文件">
                <span>×</span>
              </button>
            </div>
          </div>
          
          <div v-if="isLoadingContent" class="loading-state">
            <div class="spinner"></div>
            <span>加载内容中...</span>
          </div>
          <div v-else-if="contentError" class="error-message">
            {{ contentError }}
          </div>
          <div v-else>
            <!-- 编辑模式 -->
            <div v-if="isEditing" class="editor-container">
              <textarea 
                v-model="editableContent" 
                class="content-editor"
              ></textarea>
            </div>
            <!-- 预览模式 -->
            <div v-else class="markdown-content" v-html="renderedContent" @click="handleContentClick"></div>
          </div>
        </div>
      </div>
    </div>    <!-- 文件右键菜单 -->
    <div v-if="showContextMenu" class="context-menu" :style="contextMenuStyle">
      <!-- 文件操作 - 只在文件上右键时显示 -->
      <template v-if="selectedNode && !selectedNode.isDir && !selectedNode.dir">
        <div class="menu-item" @click="copyFileUrl">
          <span class="menu-icon">📋</span> 复制Markdown链接
        </div>
        <div class="menu-item" @click="openFile">
          <span class="menu-icon">📄</span> 打开文件
        </div>
        <div class="menu-item" @click="previewFileFromMenu">
          <span class="menu-icon">👁️</span> 预览文件
        </div>
        <div class="menu-divider"></div>
        <div class="menu-item" @click="openRenameDialog">
          <span class="menu-icon">✏️</span> 重命名
        </div>
        <div class="menu-item" @click="openMoveDialog">
          <span class="menu-icon">📁</span> 移动到...
        </div>
        <div class="menu-divider"></div>
        <div class="menu-item danger" @click="deleteItem">
          <span class="menu-icon">🗑️</span> 删除文件
        </div>
      </template>
      
      <!-- 文件夹操作 - 在文件夹上右键时显示 -->
      <template v-else>
        <div class="menu-item" @click="openCreateDialog('folder', selectedNode)">
          <span class="menu-icon">📁</span> 新建文件夹
        </div>
        <div class="menu-item" @click="openCreateDialog('file', selectedNode)">
          <span class="menu-icon">📄</span> 新建文件
        </div>
        <div v-if="selectedNode" class="menu-divider"></div>
        <div v-if="selectedNode" class="menu-item" @click="openRenameDialog">
          <span class="menu-icon">✏️</span> 重命名
        </div>
        <div v-if="selectedNode" class="menu-item" @click="openMoveDialog">
          <span class="menu-icon">📁</span> 移动到...
        </div>
        <div v-if="selectedNode" class="menu-divider"></div>
        <div v-if="selectedNode" class="menu-item danger" @click="deleteItem">
          <span class="menu-icon">🗑️</span> 删除文件夹
        </div>
      </template>
    </div>    <!-- 新建文件/文件夹对话框 -->
    <InputDialog
      :show="showCreateDialog"
      :title="createDialogTitle"
      :placeholder="createDialogPlaceholder"
      :error-message="createError"
      confirm-text="创建"
      @confirm="confirmCreate"
      @cancel="cancelCreate"
      @close="cancelCreate"
    />
    
    <!-- 删除确认对话框 -->
    <BaseDialog
      :show="showDeleteDialog"
      title="确认删除"
      :description="deleteDescription"
      confirm-text="删除"
      confirm-button-class="btn-danger"
      dialog-class="danger"
      :error-message="deleteError"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
      @close="cancelDelete"
    />

    <!-- 重命名对话框 -->
    <InputDialog
      :show="showRenameDialog"
      title="重命名"
      :description="renameDescription"
      placeholder="请输入新名称"
      :initial-value="newName"
      :error-message="renameError"
      confirm-text="确认"
      @confirm="confirmRename"
      @cancel="cancelRename"
      @close="cancelRename"
    />

    <!-- 移动对话框 -->
    <TreeSelectDialog
      :show="showMoveDialog"
      title="移动文件"
      :description="moveDescription"
      :tree-data="resourceTree"
      :exclude-node="moveItemToMove"
      :error-message="moveError"
      confirm-text="移动"
      @confirm="confirmMove"
      @cancel="cancelMove"
      @close="cancelMove"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, onUnmounted, nextTick } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { marked } from 'marked'; 
import FileTreeNode from '@/components/FileTreeNode.vue';
import Resizer from '@/components/Resizer.vue';
import BaseDialog from '@/components/BaseDialog.vue';
import InputDialog from '@/components/InputDialog.vue';
import TreeSelectDialog from '@/components/TreeSelectDialog.vue';
import authApi from '@/api/auth';

// 配置marked选项
marked.setOptions({
  breaks: true,    // 支持换行符转换为<br>
  gfm: true,       // 支持GitHub风格的Markdown
  headerIds: false, // 禁用标题ID，避免重复ID问题
  mangle: false,   // 禁用标题锚点混淆
});

const store = useAuthStore();

// 编辑状态
const isEditing = ref(false);
const editableContent = ref('');
const saveStatus = ref('');
const saveError = ref('');

// 右键菜单状态
const showContextMenu = ref(false);
const contextMenuStyle = ref({
  top: '0px',
  left: '0px'
});
const selectedNode = ref(null);

// 新建文件/文件夹对话框状态
const showCreateDialog = ref(false);
const createDialogTitle = ref('');
const createDialogPlaceholder = ref('');
const createError = ref('');
const isCreatingDirectory = ref(false);

// 删除确认对话框状态
const showDeleteDialog = ref(false);
const deleteItemToDelete = ref(null);
const deleteError = ref('');

// 重命名对话框状态
const showRenameDialog = ref(false);
const renameItemToRename = ref(null);
const newName = ref('');
const renameError = ref('');

// 移动对话框状态
const showMoveDialog = ref(false);
const moveItemToMove = ref(null);
const moveError = ref('');

// 预览状态
const previewFile = ref(null);
const previewContent = ref('');
const isLoadingPreview = ref(false);
const previewError = ref('');

// 面板宽度状态
const fileTreeWidth = ref(220); // 初始宽度
const previewPanelWidth = ref(300); // 初始宽度
const minWidth = 160; // 最小宽度
const maxTreeWidth = 400; // 文件树最大宽度
const maxPreviewWidth = 500; // 预览面板最大宽度

// 配置marked选项
marked.setOptions({
  breaks: true,
  gfm: true,
  headerIds: true,
  mangle: false,
  pedantic: false,
  sanitize: false,
  silent: false,
  smartLists: true,
  smartypants: false,
  xhtml: false
});

// 计算属性
const resourceTree = computed(() => store.resourceTree);
const currentFile = computed(() => store.currentFile);
const isLoadingTree = computed(() => store.isLoadingTree);
const isLoadingContent = computed(() => store.isLoadingContent);
const treeError = computed(() => store.treeError);
const contentError = computed(() => store.contentError);

// 判断选中的节点是否为目录
const isSelectedNodeDirectory = computed(() => {
  if (!selectedNode.value) return false;
  return selectedNode.value.isDir || selectedNode.value.dir || false;
});

// 判断是否为可编辑文件
const isEditableFile = computed(() => {
  if (!currentFile.value) return false;
  const fileName = currentFile.value.fName || currentFile.value.fname || '';
  if (!fileName || typeof fileName !== 'string') return false;
  const extension = fileName.split('.').pop()?.toLowerCase();
  return ['md', 'txt', 'json', 'html', 'css', 'js', 'vue'].includes(extension || '');
});

// 安全获取当前文件名
const currentFileName = computed(() => {
  if (!currentFile.value) return '';
  return currentFile.value.fName || currentFile.value.fname || '未知文件';
});

// 安全获取预览文件名
const previewFileName = computed(() => {
  if (!previewFile.value) return '';
  return previewFile.value.fName || previewFile.value.fname || '未知文件';
});

// 对话框描述文本
const deleteDescription = computed(() => {
  if (!deleteItemToDelete.value) return '';
  const fileName = deleteItemToDelete.value.fName || deleteItemToDelete.value.fname || deleteItemToDelete.value.name || '未知文件';
  return `您确定要删除"${fileName}"吗？\n此操作不可撤销！`;
});

const renameDescription = computed(() => {
  if (!renameItemToRename.value) return '';
  const fileName = renameItemToRename.value.fName || renameItemToRename.value.fname || renameItemToRename.value.name || '未知文件';
  return `重命名: ${fileName}`;
});

const moveDescription = computed(() => {
  if (!moveItemToMove.value) return '';
  const fileName = moveItemToMove.value.fName || moveItemToMove.value.fname || moveItemToMove.value.name || '未知文件';
  return `移动: ${fileName}`;
});

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!store.currentFileContent) return '';
  if (currentFile.value) {
    const fileName = currentFile.value.fName || currentFile.value.fname || '';
    if (fileName && fileName.toLowerCase().endsWith('.md')) {
      return marked(store.currentFileContent);
    }
  }
  return `<pre>${store.currentFileContent}</pre>`;
});

// 渲染预览内容
const renderedPreviewContent = computed(() => {
  console.log('渲染预览内容:', previewContent.value ? previewContent.value.substring(0, 100) : 'null');
  console.log('预览文件信息:', previewFile.value);
  
  if (!previewContent.value) return '';
  
  // 默认尝试作为Markdown渲染，因为大多数文档都是Markdown格式
  let shouldRenderAsMarkdown = true;
  
  if (previewFile.value) {
    const fileName = previewFile.value.fName || previewFile.value.fname || '';
    console.log('文件名:', fileName);
    
    // 只有明确不是Markdown文件时才不渲染为Markdown
    if (fileName && !fileName.toLowerCase().endsWith('.md') && !fileName.toLowerCase().includes('markdown')) {
      shouldRenderAsMarkdown = false;
    }
  }
  
  if (shouldRenderAsMarkdown) {
    console.log('作为Markdown渲染');
    try {
      return marked(previewContent.value);
    } catch (error) {
      console.error('Markdown渲染错误:', error);
      return `<pre>${previewContent.value}</pre>`;
    }
  } else {
    console.log('作为纯文本渲染');
    return `<pre>${previewContent.value}</pre>`;
  }
});

// 处理文件节点点击
const handleNodeClick = async (node) => {
  if (node.isDir || node.dir) return; // 如果是目录，不进行操作
  
  // 如果正在编辑，提示用户保存或取消
  if (isEditing.value) {
    if (!confirm('你有未保存的更改，确定要切换文件吗？')) {
      return;
    }
    isEditing.value = false;
  }
  
  // 设置当前文件
  store.setCurrentFile(node);
  
  // 使用文件ID获取文件内容
  // 优先用 URL 作为 fileId
  const fileId = node.URL || node.fid || node.id;
  if (fileId) {
    try {
      await store.fetchFileContent(fileId);
    } catch (error) {
      console.error('获取文件内容失败', error);
    }
  }
};

// 处理文件节点右键点击
const handleNodeContextMenu = (data) => {
  console.log('右键点击节点:', data.node);
  console.log('节点名称:', data.node.fName || data.node.fname || data.node.name || '未知');
  console.log('节点是否为目录:', data.node.isDir || data.node.dir || false);
  console.log('节点ID:', data.node.fid || data.node.id);
  
  // 阻止事件继续传播到tree-container
  data.event.stopPropagation();
  
  // 显示右键菜单
  showContextMenu.value = true;
  selectedNode.value = data.node;
  
  // 设置菜单位置
  contextMenuStyle.value = {
    top: `${data.event.clientY}px`,
    left: `${data.event.clientX}px`
  };
};



// 复制文件URL到剪贴板
const copyFileUrl = () => {
  if (selectedNode.value && selectedNode.value.fid) {
    const fileId = selectedNode.value.fid;
    const fileName = selectedNode.value.fName || selectedNode.value.fname || 'unknown';
    // 生成Markdown格式的链接
    const markdownLink = `[${fileName}](${fileId})`;
    
    navigator.clipboard.writeText(markdownLink)
      .then(() => {
        saveStatus.value = 'Markdown链接已复制到剪贴板';
        setTimeout(() => {
          saveStatus.value = '';
        }, 2000);
      })
      .catch(err => {
        console.error('复制失败: ', err);
        saveError.value = '复制链接失败';
        setTimeout(() => {
          saveError.value = '';
        }, 2000);
      });
  } else {
    saveError.value = '无法获取文件信息';
    setTimeout(() => {
      saveError.value = '';
    }, 2000);
  }
  closeContextMenu();
};

// 从右键菜单打开文件
const openFile = async () => {
  if (selectedNode.value) {
    await handleNodeClick(selectedNode.value);
  }
  closeContextMenu();
};

// 从右键菜单预览文件
const previewFileFromMenu = async () => {
  if (selectedNode.value) {
    await loadPreview(selectedNode.value);
  }
  closeContextMenu();
};

// 关闭右键菜单
const closeContextMenu = () => {
  showContextMenu.value = false;
  selectedNode.value = null;
};

// 显示创建对话框
const openCreateDialog = (type, parentNode) => {
  closeContextMenu();
  
  console.log('openCreateDialog - 类型:', type, '父节点:', parentNode);
  
  if (type === 'folder') {
    isCreatingDirectory.value = true;
    createDialogTitle.value = '新建文件夹';
    createDialogPlaceholder.value = '请输入文件夹名称';
  } else {
    isCreatingDirectory.value = false;
    createDialogTitle.value = '新建文件';
    createDialogPlaceholder.value = '请输入文件名（如：note.md）';
  }
    // 设置父节点（如果parentNode为null，则在根目录创建）
  selectedNode.value = parentNode;
  console.log('设置selectedNode为:', selectedNode.value);
  
  createError.value = '';
  showCreateDialog.value = true;
};

// 确认创建
const confirmCreate = async (fileName) => {
  if (!fileName || !fileName.trim()) {
    createError.value = '名称不能为空';
    return;
  }
  
  try {
    createError.value = '';
    
    // 确定父级ID - 添加调试信息
    let parentId = null;
    console.log('选中的节点:', selectedNode.value);
    
    if (selectedNode.value) {
      const isDirectory = selectedNode.value.isDir || selectedNode.value.dir;
      console.log('是否为目录:', isDirectory);
      
      if (isDirectory) {
        // 在选中的文件夹下创建
        parentId = selectedNode.value.fid || selectedNode.value.id || selectedNode.value.URL;
        console.log('设置父级ID:', parentId);
      } else {
        console.log('选中的是文件，不应该创建子项');
        createError.value = '无法在文件下创建内容';
        return;
      }
    } else {
      console.log('在根目录创建');
    }
    
    console.log('创建参数:', {
      fileName: fileName.trim(),
      parentId: parentId,
      isDirectory: isCreatingDirectory.value
    });
      // 调用API创建文件或文件夹
    await authApi.createFile(fileName.trim(), parentId, isCreatingDirectory.value);
    
    // 创建成功后刷新文件树
    await store.fetchResourceTree();
    
    // 关闭对话框
    showCreateDialog.value = false;
      } catch (error) {
    console.error('创建失败:', error);
    
    // 改进错误信息处理
    let errorMessage = '创建失败';
    
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message;
    } else if (error.response?.data) {
      errorMessage = error.response.data;
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    // 特别处理重名错误
    if (errorMessage.includes('已存在') || errorMessage.includes('同名')) {
      createError.value = errorMessage;
    } else {
      createError.value = errorMessage;
    }
    
    console.log('显示错误信息:', createError.value);
  }
};

// 取消创建
const cancelCreate = () => {
  showCreateDialog.value = false;
  createError.value = '';
};

// 删除文件/文件夹
const deleteItem = () => {
  if (!selectedNode.value) {
    console.error('没有选中的节点');
    return;
  }
  
  console.log('准备删除:', selectedNode.value);
  deleteItemToDelete.value = selectedNode.value;
  deleteError.value = '';
  showDeleteDialog.value = true;
  
  // 在设置删除项后再关闭右键菜单
  closeContextMenu();
};

// 确认删除
const confirmDelete = async () => {
  if (!deleteItemToDelete.value) {
    console.error('没有要删除的项目');
    return;
  }
  
  try {
    deleteError.value = '';
      const itemId = deleteItemToDelete.value.fid || deleteItemToDelete.value.id || deleteItemToDelete.value.URL;
    const itemName = deleteItemToDelete.value.fName || deleteItemToDelete.value.fname || deleteItemToDelete.value.name;
    const isDirectory = deleteItemToDelete.value.isDir || deleteItemToDelete.value.dir;
    
    console.log('删除项目:', {
      id: itemId,
      name: itemName,
      isDirectory: isDirectory
    });
    
    // 调用统一的删除API
    await authApi.deleteFile(itemId);
    
    // 删除成功后刷新文件树
    await store.fetchResourceTree();
    
    // 如果删除的是当前打开的文件，清空内容区域
    if (store.currentFile && 
        (store.currentFile.fid === itemId || 
         store.currentFile.id === itemId || 
         store.currentFile.URL === itemId)) {
      store.setCurrentFile(null);
      store.currentFileContent = '';
    }
    
    // 关闭对话框
    showDeleteDialog.value = false;
    deleteItemToDelete.value = null;
    
  } catch (error) {
    console.error('删除失败:', error);
    deleteError.value = error.message || '删除失败';
  }
};

// 取消删除
const cancelDelete = () => {
  showDeleteDialog.value = false;
  deleteItemToDelete.value = null;
  deleteError.value = '';
};

// 重命名文件/文件夹
const openRenameDialog = () => {
  if (!selectedNode.value) {
    console.error('没有选中的节点');
    return;
  }
  
  console.log('准备重命名:', selectedNode.value);
  renameItemToRename.value = selectedNode.value;
  newName.value = selectedNode.value.fName || selectedNode.value.fname || selectedNode.value.name || '';
  renameError.value = '';
  showRenameDialog.value = true;
  
  // 关闭右键菜单
  closeContextMenu();
};

// 确认重命名
const confirmRename = async (newFileName) => {
  if (!renameItemToRename.value) {
    console.error('没有要重命名的项目');
    return;
  }
  
  if (!newFileName || !newFileName.trim()) {
    renameError.value = '名称不能为空';
    return;
  }
  
  const originalName = renameItemToRename.value.fName || renameItemToRename.value.fname || renameItemToRename.value.name;
  if (newFileName.trim() === originalName) {
    // 名称没有变化，直接关闭对话框
    cancelRename();
    return;
  }
  
  try {
    renameError.value = '';
    const itemId = renameItemToRename.value.fid || renameItemToRename.value.id || renameItemToRename.value.URL;
    
    console.log('重命名项目:', {
      id: itemId,
      oldName: originalName,
      newName: newFileName.trim()
    });
    
    // 调用重命名API
    await authApi.renameFile(itemId, newFileName.trim());
    
    // 重命名成功后刷新文件树
    await store.fetchResourceTree();
    
    // 关闭对话框
    showRenameDialog.value = false;
    renameItemToRename.value = null;
    
  } catch (error) {
    console.error('重命名失败:', error);
    renameError.value = error.response?.data?.message || error.message || '重命名失败';
  }
};

// 取消重命名
const cancelRename = () => {
  showRenameDialog.value = false;
  renameItemToRename.value = null;
  renameError.value = '';
};

// 移动文件/文件夹
const openMoveDialog = () => {
  if (!selectedNode.value) {
    console.error('没有选中的节点');
    return;
  }
    console.log('准备移动:', selectedNode.value);
  moveItemToMove.value = selectedNode.value;
  moveError.value = '';
  showMoveDialog.value = true;
  
  // 关闭右键菜单
  closeContextMenu();
};

// 确认移动
const confirmMove = async (targetNode) => {
  if (!moveItemToMove.value) {
    console.error('没有要移动的项目');
    return;
  }
  
  try {
    moveError.value = '';
    const itemId = moveItemToMove.value.fid || moveItemToMove.value.id || moveItemToMove.value.URL;
    const targetId = targetNode ? (targetNode.fid || targetNode.id || targetNode.URL) : null;
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const userId = user.userid;
    
    console.log('移动项目:', {
      id: itemId,
      targetId: targetId,
      userId: userId
    });
    
    // 检查是否移动到自己或自己的子目录
    if (targetId === itemId) {
      moveError.value = '不能移动到自己';
      return;
    }
    
    // 调用移动API
    await authApi.moveFile(itemId, targetId, userId);
    
    // 移动成功后刷新文件树
    await store.fetchResourceTree();
    
    // 关闭对话框
    showMoveDialog.value = false;
    moveItemToMove.value = null;
    
  } catch (error) {
    console.error('移动失败:', error);
    moveError.value = error.response?.data?.message || error.message || '移动失败';
  }
};

// 取消移动
const cancelMove = () => {
  showMoveDialog.value = false;
  moveItemToMove.value = null;
  moveError.value = '';
};

// 处理内容区域点击，用于链接预览
const handleContentClick = async (event) => {
  // 检查是否点击的是链接
  if (event.target.tagName === 'A') {
    event.preventDefault();
    const url = event.target.getAttribute('href');
    console.log('点击链接:', url);
    
    // 尝试在文件树中查找匹配的文件
    const file = findFileByUrl(url);
    if (file) {
      console.log('找到文件:', file);
      await loadPreview(file);
    } else {
      console.log('未找到文件，尝试直接加载:', url);
      // 如果找不到文件，检查是否是纯数字ID
      if (/^\d+$/.test(url)) {
        // 数字ID，尝试加载
        previewFile.value = { fName: `文件${url}`, fid: url };
        await loadPreviewContent(url);
      } else {
        // 文件名，设置一个默认的预览文件信息
        previewFile.value = { fName: url, URL: url };
        await loadPreviewContent(url);
      }
    }
  }
};

// 在文件树中查找匹配URL的文件
const findFileByUrl = (url) => {
  console.log('搜索文件:', url);
  
  // 递归搜索函数
  const searchInTree = (nodes) => {
    if (!nodes) return null;
    
    for (const node of nodes) {
      const nodeId = node.fid || node.id || node.URL;
      const fileName = node.fName || node.fname || '';
      
      if (!node.isDir && !node.dir) {
        console.log('检查文件:', fileName, 'ID:', nodeId);
        
        // 1. 精确匹配ID或URL
        if (nodeId && nodeId.toString() === url.toString()) {
          console.log('ID匹配成功:', nodeId);
          return node;
        }
        
        // 2. 匹配文件名（完全匹配）
        if (fileName === url) {
          console.log('文件名完全匹配:', fileName);
          return node;
        }
        
        // 3. 匹配文件名（忽略.md扩展名）
        if (fileName.endsWith('.md') && fileName.replace('.md', '') === url.replace('.md', '')) {
          console.log('文件名匹配（忽略扩展名）:', fileName);
          return node;
        }
        
        // 4. 如果url不包含.md，尝试添加.md匹配
        if (!url.includes('.md') && fileName === url + '.md') {
          console.log('添加.md扩展名匹配:', fileName);
          return node;
        }
      }
      
      if ((node.isDir || node.dir) && node.children) {
        const found = searchInTree(node.children);
        if (found) return found;
      }
    }
    
    return null;
  };
  
  const result = searchInTree(resourceTree.value);
  console.log('搜索结果:', result);
  return result;
};

// 加载预览文件
const loadPreview = async (file) => {
  previewFile.value = file;
  const fileId = file.fid || file.id || file.URL;
  await loadPreviewContent(fileId);
};

// 加载预览内容
const loadPreviewContent = async (fileId) => {
  isLoadingPreview.value = true;
  previewError.value = '';
  console.log('加载预览内容，文件ID:', fileId);
  
  try {
    const response = await authApi.getFileContent(fileId);
    previewContent.value = response.data;
    console.log('文件内容加载成功，长度:', response.data ? response.data.length : 0);
    
    // 如果previewFile还没有完整信息，尝试从文件树中查找
    if (previewFile.value && (!previewFile.value.fName && !previewFile.value.fname)) {
      console.log('尝试从文件树中查找文件信息');
      const foundFile = findFileById(fileId);
      if (foundFile) {
        console.log('找到完整文件信息:', foundFile);
        previewFile.value = foundFile;
      } else {
        // 如果还是找不到，设置一个默认的.md文件名
        console.log('未找到文件信息，设置默认.md文件名');
        previewFile.value = {
          ...previewFile.value,
          fName: `文件${fileId}.md`
        };
      }
    }
  } catch (error) {
    console.error('加载预览内容失败', error);
    previewError.value = `加载预览内容失败: ${error.message || '未知错误'}`;
    previewContent.value = '';
  } finally {
    isLoadingPreview.value = false;
  }
};

// 根据ID查找文件
const findFileById = (fileId) => {
  const searchInTree = (nodes) => {
    if (!nodes) return null;
    
    for (const node of nodes) {
      const nodeId = node.fid || node.id || node.URL;
      if (nodeId && nodeId.toString() === fileId.toString()) {
        return node;
      }
      
      if ((node.isDir || node.dir) && node.children) {
        const found = searchInTree(node.children);
        if (found) return found;
      }
    }
    return null;
  };
  
  return searchInTree(resourceTree.value);
};

// 关闭预览
const closePreview = () => {
  previewFile.value = null;
  previewContent.value = '';
  previewError.value = '';
};

// 开始编辑文件
const startEditing = () => {
  editableContent.value = store.currentFileContent || '';
  isEditing.value = true;
  saveStatus.value = '';
  saveError.value = '';
};

// 保存文件内容
const saveContent = async () => {  if (!currentFile.value) {
    saveError.value = '无法保存文件：没有选中的文件';
    return;
  }
  
  // 获取文件ID，支持多种字段名
  const fileId = currentFile.value.fid || currentFile.value.id || currentFile.value.URL;
  if (!fileId) {
    saveError.value = '无法保存文件：文件ID不存在';
    return;
  }
  
  try {
    saveStatus.value = '保存中...';
    // 使用API保存文件内容
    await authApi.saveFileContent(fileId, editableContent.value);
    
    // 更新store中的文件内容
    store.updateCurrentFileContent(editableContent.value);
    
    saveStatus.value = '保存成功';
    isEditing.value = false;
    
    // 3秒后清除状态信息
    setTimeout(() => {
      saveStatus.value = '';
    }, 3000);
  } catch (error) {
    console.error('保存文件失败', error);
    saveError.value = `保存失败：${error.message || '未知错误'}`;
    
    // 5秒后清除错误信息
    setTimeout(() => {
      saveError.value = '';
    }, 5000);
  }
};

// 取消编辑
const cancelEditing = () => {
  if (editableContent.value !== store.currentFileContent) {
    if (!confirm('确定要取消编辑？所有未保存的更改将丢失。')) {
      return;
    }
  }
  isEditing.value = false;
  editableContent.value = '';
  saveStatus.value = '';
  saveError.value = '';
};

// 关闭当前文件
const closeFile = () => {
  // 如果正在编辑，提示用户保存或取消
  if (isEditing.value) {
    if (!confirm('你有未保存的更改，确定要关闭文件吗？')) {
      return;
    }
    isEditing.value = false;
    editableContent.value = '';
  }
  
  store.closeCurrentFile();
};

// 全局点击事件，用于关闭右键菜单
const handleGlobalClick = () => {
  if (showContextMenu.value) {
    closeContextMenu();
  }
};

// 文件树缩放
const handleTreeResize = (deltaX) => {
  fileTreeWidth.value += deltaX;
  if (fileTreeWidth.value < minWidth) fileTreeWidth.value = minWidth;
  if (fileTreeWidth.value > maxTreeWidth) fileTreeWidth.value = maxTreeWidth;
};

// 预览面板缩放（修正为调整previewPanelWidth）
const handlePreviewResize = (deltaX) => {
  previewPanelWidth.value += deltaX;
  if (previewPanelWidth.value < minWidth) previewPanelWidth.value = minWidth;
  if (previewPanelWidth.value > maxPreviewWidth) previewPanelWidth.value = maxPreviewWidth;
};


const handlePreviewContentClick = async (event) => {
  if (event.button !== 0) return; // 只处理左键
  let target = event.target;
  // 兼容 a 标签内有 span、strong 等嵌套
  while (target && target.tagName !== 'A' && target !== event.currentTarget) {
    target = target.parentNode;
  }
  if (target && target.tagName === 'A') {
    const url = target.getAttribute('href');
    // 外部链接放行
    if (/^(http|https):\/\//.test(url)) return;
    // 锚点放行
    if (url && url.startsWith('#')) return;
    event.preventDefault();
    event.stopPropagation();
    if (!url) return;
    const file = findFileByUrl(url);
    if (file) {
      await loadPreview(file);
    } else {
      await loadPreviewContent(url);
    }
  }
};

// 挂载时添加全局点击事件监听
onMounted(async () => {
  console.log('WorkbenchView: 组件挂载')
  document.addEventListener('click', handleGlobalClick);
  
  console.log('WorkbenchView: 开始获取资源树')
  try {
    await store.fetchResourceTree();
    console.log('WorkbenchView: 资源树获取成功')
  } catch (error) {
    console.error('WorkbenchView: 获取资源树失败', error);
    // 不阻止页面渲染，只记录错误
  }
});

// 卸载前移除事件监听
onBeforeUnmount(() => {
  document.removeEventListener('click', handleGlobalClick);
});

// 组件卸载时清理状态
onUnmounted(() => {
  console.log('WorkbenchView: 组件卸载，清理状态')
  // 清理编辑状态
  isEditing.value = false;
  editableContent.value = '';
  saveStatus.value = '';
  saveError.value = '';
  
  // 清理预览状态
  previewFile.value = null;
  previewContent.value = '';
  previewError.value = '';
  
  // 清理右键菜单状态
  showContextMenu.value = false;
  selectedNode.value = null;
  
  // 清理当前文件状态
  store.closeCurrentFile();
});
</script>

<style scoped>
.workbench-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
  position: relative;
}

.workbench-layout {
  display: flex;
  height: calc(100% - 60px); /* 减去标题的高度 */
  position: relative; /* 让 Resizer 定位生效 */
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

/* 预览面板样式 */
.preview-panel {
  position: relative; /* 为 Resizer 提供定位上下文 */
  height: 100%;
  border-right: 1px solid #e0e0e0;
  background-color: #f9f9f9;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  z-index: 1;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #f0f0f0;
}

.preview-title {
  overflow: hidden;
}

.preview-title h3 {
  font-size: 14px;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.preview-path {
  font-size: 12px;
  color: #666;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.preview-close {
  margin-left: 10px;
}

.preview-content {
  padding: 15px;
  overflow-y: auto;
  flex: 1;
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

/* 右键菜单样式 */
.context-menu {
  position: fixed;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 180px;
}

.menu-item {
  padding: 8px 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.menu-item:hover {
  background-color: #f5f5f5;
}

.menu-item.danger {
  color: #dc3545;
}

.menu-item.danger:hover {
  background-color: #f8d7da;
}

.menu-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 4px 0;
}

.menu-icon {
  margin-right: 10px;
  font-size: 16px;
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

.file-actions {
  display: flex;
  gap: 10px;
}

.edit-button, .save-button, .cancel-button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.edit-button {
  background-color: #4caf50;
  color: white;
}

.edit-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.save-button {
  background-color: #2196f3;
  color: white;
}

.cancel-button {
  background-color: #f44336;
  color: white;
}

.edit-button:hover:not(:disabled) {
  background-color: #45a049;
}

.save-button:hover {
  background-color: #0b7dda;
}

.cancel-button:hover {
  background-color: #d32f2f;
}

.editor-container {
  height: calc(100vh - 250px);
}

.content-editor {
  width: 100%;
  height: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
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

.save-status {
  font-size: 14px;
  margin-left: 10px;
  padding: 4px 8px;
  border-radius: 4px;
}

.save-status.success {
  color: #4caf50;
  background-color: rgba(76, 175, 80, 0.1);
}

.save-status.error {
  color: #f44336;
  background-color: rgba(244, 67, 54, 0.1);
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
  margin-left: 10px;
}

.close-button:hover {
  background-color: #f44336;
  color: white;  border-color: #f44336;
}

/* 菜单分隔线 */
.menu-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 5px 0;
}

/* 修正Resizer在预览面板和右侧内容之间的显示层级 */
:deep(.resizer) {
  z-index: 2;
}

/* 树形结构头部样式 */
.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #f8f9fa;
}

.tree-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.tree-actions {
  display: flex;
  gap: 5px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 4px;
  background-color: #e9ecef;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-btn:hover {
  background-color: #dee2e6;
}

.action-btn .icon {
  font-size: 16px;
}
</style>
