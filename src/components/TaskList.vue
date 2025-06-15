<template>
  <div class="tasks-container">
    <div class="tasklist-header">
      <slot name="title"></slot>
      <button class="add-task-btn" @click="showModal = true">创建新任务</button>
    </div>
    <div v-if="tasks.length === 0" class="empty-tasks">
      <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%23a0aec0' d='M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z'/%3E%3C/svg%3E" 
      alt="No tasks" class="empty-icon">
      <p>今日无任务</p>
    </div>
    
    <div 
      v-for="task in tasks" 
      :key="task.id" 
      class="task-card"
      :style="{ borderLeftColor: task.color }"
    >
      <div class="task-content">
        <div class="task-info" v-if="editId !== task.id">
          <div class="task-title">{{ task.title }}</div>
          <div class="task-time">
            {{ task.start || task.time }}<span v-if="task.start"> - {{ task.end }}</span>
          </div>
        </div>
        <!-- 编辑模式 -->
        <div class="task-info" v-else>
          <input v-model="editTask.title" class="edit-input" />
          <input type="time" v-model="editTask.start" class="edit-input" style="width:90px" />
          <span style="margin:0 4px;">-</span>
          <input type="time" v-model="editTask.end" class="edit-input" style="width:90px" />
          <textarea v-model="editTask.description" class="edit-textarea" rows="2"></textarea>
          <!-- 新增颜色选择器 -->
          <div style="margin: 8px 0;">
            <label style="font-size:13px;color:#888;">卡片颜色：</label>
            <input type="color" v-model="editTask.color" style="vertical-align: middle; width: 32px; height: 24px; border: none; background: none;"/>
            <span v-for="color in defaultColors" :key="color"
                  :style="{background: color, width: '22px', height: '22px', display: 'inline-block', borderRadius: '4px', marginLeft: '6px', cursor: 'pointer', border: editTask.color === color ? '2px solid #333' : '1px solid #eee'}"
                  @click="editTask.color = color">
            </span>
          </div>
        </div>
        <div class="task-actions">
          <button v-if="editId !== task.id" @click="startEdit(task)">编辑</button>
          <button v-else @click="saveEdit(task)">保存</button>
          <button v-if="editId === task.id" @click="cancelEdit">取消</button>
          <button @click="$emit('toggle-complete', task)" class="complete-btn">
            <svg v-if="task.completed" viewBox="0 0 24 24" width="20" height="20">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="20" height="20">
              <path d="M19 5v14H5V5h14m0-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z"/>
            </svg>
          </button>
          <button @click="$emit('delete-task', task.id)" class="delete-btn">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
            </svg>
          </button>
        </div>
      </div>
      <div v-if="editId !== task.id && task.description" class="task-description">
        {{ task.description }}
      </div>
    </div>

    <!-- 新建任务弹窗 -->
    <div v-if="showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <h3>新建任务</h3>
          <input v-model="newTask.title" placeholder="任务名称" />
          <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px;">
            <input type="time" v-model="newTask.start" style="width:90px" placeholder="开始时间" />
            <span>-</span>
            <input type="time" v-model="newTask.end" style="width:90px" placeholder="结束时间" />
          </div>
          <textarea v-model="newTask.description" placeholder="备注（可选）" class="edit-textarea" rows="2"></textarea>
          <!-- 新增颜色选择器 -->
          <div style="margin: 8px 0;">
            <label style="font-size:13px;color:#888;">卡片颜色：</label>
            <input type="color" v-model="newTask.color" style="vertical-align: middle; width: 32px; height: 24px; border: none; background: none;"/>
            <span v-for="color in defaultColors" :key="color"
                  :style="{background: color, width: '22px', height: '22px', display: 'inline-block', borderRadius: '4px', marginLeft: '6px', cursor: 'pointer', border: newTask.color === color ? '2px solid #333' : '1px solid #eee'}"
                  @click="newTask.color = color">
            </span>
          </div>
          <div class="edit-actions">
            <button @click="confirmAddTask">确认</button>
            <button @click="closeModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  tasks: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['delete-task', 'toggle-complete', 'update-task', 'add-task']);

const editId = ref(null);
const editTask = ref({});

const showModal = ref(false);
const newTask = ref({
  title: '',
  start: '',
  end: '',
  description: '',
  color: '#4a6cf7' // 默认颜色
});
const defaultColors = ['#4a6cf7', '#ff9800', '#4caf50', '#e91e63', '#9c27b0', '#ff0000', '#00bcd4']; // 默认颜色数组

function startEdit(task) {
  editId.value = task.id;
  editTask.value = { ...task };
}
function saveEdit(task) {
  // 校验开始时间必须早于结束时间
  if (editTask.value.start && editTask.value.end && editTask.value.start >= editTask.value.end) {
    alert('开始时间必须早于结束时间');
    return;
  }
  emit('update-task', { ...editTask.value });
  editId.value = null;
}
function cancelEdit() {
  editId.value = null;
}

function closeModal() {
  showModal.value = false;
  newTask.value = { title: '', start: '', end: '', description: '', color: '#4a6cf7' };
}
function confirmAddTask() {
  if (!newTask.value.title.trim()) {
    alert('请填写任务名称');
    return;
  }
  if (newTask.value.start && newTask.value.end && newTask.value.start >= newTask.value.end) {
    alert('开始时间必须早于结束时间');
    return;
  }
  // 随机颜色已由用户选择覆盖
  emit('add-task', {
    id: Date.now(),
    title: newTask.value.title,
    start: newTask.value.start,
    end: newTask.value.end,
    description: newTask.value.description,
    completed: false,
    color: newTask.value.color || '#4a6cf7',
    date: new Date() // 可根据需要调整
  });
  closeModal();
}
function setColor(color) {
  if (editId.value !== null) {
    editTask.value.color = color;
  } else {
    newTask.value.color = color;
  }
}
</script>

<style scoped>
.tasks-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.tasklist-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.add-task-btn {
  background: #4a6cf7;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 6px 18px;
  font-size: 15px;
  cursor: pointer;
  height: 36px;
  white-space: nowrap;
}
.task-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 16px;
  border-left: 4px solid;
}
.task-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.task-info {
  flex: 1;
}
.task-title {
  font-weight: 600;
  margin-bottom: 4px;
}
.task-time {
  color: #64748b;
  font-size: 14px;
}
.task-actions {
  display: flex;
  gap: 8px;
}
.complete-btn, .delete-btn {
  background: none;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
}
.complete-btn {
  color: #4caf50;
}
.complete-btn:hover {
  background-color: rgba(76, 175, 80, 0.1);
}
.delete-btn {
  color: #f44336;
}
.delete-btn:hover {
  background-color: rgba(244, 67, 54, 0.1);
}
.task-description {
  color: #64748b;
  font-size: 14px;
  padding-top: 8px;
  border-top: 1px solid #f1f5f9;
}
.edit-input, .edit-textarea {
  margin: 4px 0;
  font-size: 15px;
  width: 100%;
  box-sizing: border-box;
}
.edit-textarea {
  resize: vertical;
}
.empty-tasks {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 40px 20px;
  text-align: center;
}
.empty-icon {
  width: 60px;
  height: 60px;
  opacity: 0.5;
  margin-bottom: 15px;
}
.empty-tasks p {
  color: #64748b;
  margin: 0;
}
.modal-mask {
  position: fixed;
  z-index: 9999;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-wrapper {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-container {
  background: #fff;
  border-radius: 10px;
  padding: 32px 28px 20px 28px;
  min-width: 320px;
  max-width: 90vw;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.modal-container h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  font-weight: bold;
}
</style>