<template>
  <div 
    class="resizer" 
    :class="{ 'vertical': direction === 'vertical', 'horizontal': direction === 'horizontal' }"
    @mousedown="startResize"
  ></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps({
  direction: {
    type: String,
    default: 'vertical', // 'vertical' 或 'horizontal'
    validator: (value) => ['vertical', 'horizontal'].includes(value)
  },
  onResize: {
    type: Function,
    required: true
  }
});

// 拖动状态
const isDragging = ref(false);
const startX = ref(0);
const startY = ref(0);

// 开始调整大小
const startResize = (e) => {
  isDragging.value = true;
  startX.value = e.clientX;
  startY.value = e.clientY;
  document.addEventListener('mousemove', onMouseMove);
  document.addEventListener('mouseup', stopResize);
  
  // 添加全局类，用于调整光标样式
  if (props.direction === 'vertical') {
    document.body.classList.add('resizing-vertical');
  } else {
    document.body.classList.add('resizing-horizontal');
  }
  
  // 阻止选择
  document.body.style.userSelect = 'none';
};

// 鼠标移动时调整大小
const onMouseMove = (e) => {
  if (!isDragging.value) return;
  
  const deltaX = e.clientX - startX.value;
  const deltaY = e.clientY - startY.value;
  
  // 调用父组件传递的回调函数
  props.onResize(props.direction === 'vertical' ? deltaX : deltaY);
  
  // 更新起始点
  startX.value = e.clientX;
  startY.value = e.clientY;
};

// 停止调整大小
const stopResize = () => {
  isDragging.value = false;
  document.removeEventListener('mousemove', onMouseMove);
  document.removeEventListener('mouseup', stopResize);
  
  // 移除全局类
  document.body.classList.remove('resizing-vertical');
  document.body.classList.remove('resizing-horizontal');
  
  // 恢复选择
  document.body.style.userSelect = '';
};

// 组件卸载前确保事件监听器被移除
onBeforeUnmount(() => {
  if (isDragging.value) {
    document.removeEventListener('mousemove', onMouseMove);
    document.removeEventListener('mouseup', stopResize);
    document.body.classList.remove('resizing-vertical');
    document.body.classList.remove('resizing-horizontal');
    document.body.style.userSelect = '';
  }
});
</script>

<style>
/* 全局样式，当正在调整大小时应用 */
body.resizing-vertical {
  cursor: col-resize !important;
}

body.resizing-horizontal {
  cursor: row-resize !important;
}
</style>

<style scoped>
.resizer {
  position: absolute;
  z-index: 10;
}

.resizer.vertical {
  width: 6px;
  height: 100%;
  top: 0;
  right: -3px;
  cursor: col-resize;
  background: transparent;
}

.resizer.horizontal {
  width: 100%;
  height: 6px;
  left: 0;
  bottom: -3px;
  cursor: row-resize;
  background: transparent;
}

.resizer:hover {
  background: rgba(0, 0, 0, 0.1);
}

.resizer:active {
  background: rgba(0, 0, 0, 0.2);
}
</style>
