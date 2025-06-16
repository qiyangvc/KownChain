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
      :key="task.tdID" 
      class="task-card"
      style="border-left: 6px solid #4a6cf7;"
    >
      <div class="task-content">
        <div class="task-info" v-if="editId !== task.tdID">
          <div class="task-title">{{ task.tdContent }}</div>
          <div class="task-time">
            {{ task.tdStartTime }}<span v-if="task.tdStartTime"> - {{ task.tdEndTime }}</span>
          </div>
        </div>
        <!-- 编辑模式 -->
        <div class="task-info" v-else>
          <input v-model="editTask.tdContent" class="edit-input" placeholder="任务名称" />
          <div style="display: flex; align-items: center; gap: 6px;">
            <input type="time" v-model="editTask.tdStartTime" class="edit-input" style="width:90px" />
            <span style="margin:0 4px;">-</span>
            <input type="time" v-model="editTask.tdEndTime" class="edit-input" style="width:90px" />
          </div>
        </div>
      </div>
      <div class="task-actions">
        <button
          class="icon-btn edit-btn"
          v-if="editId !== task.tdID"
          @click="startEdit(task)"
          title="编辑"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M4 21h4l11-11-4-4L4 17v4z" stroke="#1976d2" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span class="btn-text">编辑</span>
        </button>
        <button
          class="icon-btn delete-btn"
          v-if="editId !== task.tdID"
          @click="$emit('delete-task', task.tdID)"
          title="删除"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M6 6L18 18M6 18L18 6" stroke="#f44336" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span class="btn-text">删除</span>
        </button>
        <button
          class="icon-btn complete-btn"
          @click="$emit('toggle-complete', { ...task, tdFinishFlag: task.tdFinishFlag ? 0 : 1 })"
          :title="task.tdFinishFlag ? '标记为未完成' : '标记为已完成'"
        >
          <span v-if="task.tdFinishFlag">✔️</span>
          <span v-else>⬜</span>
        </button>
        <template v-if="editId === task.tdID">
          <input v-model="editTask.tdContent" class="edit-input" placeholder="任务名称" />
          <div style="display: flex; align-items: center; gap: 6px;">
            <input type="time" v-model="editTask.tdStartTime" class="edit-input" style="width:90px" />
            <span style="margin:0 4px;">-</span>
            <input type="time" v-model="editTask.tdEndTime" class="edit-input" style="width:90px" />
          </div>
          <div class="edit-actions">
            <button class="save-btn" @click="saveEdit">保存</button>
            <button class="cancel-btn" @click="cancelEdit">取消</button>
          </div>
        </template>
      </div>
    </div>

    <!-- 新建任务弹窗 -->
    <div v-if="showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <h3>新建任务</h3>
          <input v-model="newTask.tdContent" placeholder="任务名称" class="edit-input" />
          <div style="display: flex; align-items: center; gap: 6px;">
            <input type="time" v-model="newTask.tdStartTime" class="edit-input" style="width:90px" />
            <span style="margin:0 4px;">-</span>
            <input type="time" v-model="newTask.tdEndTime" class="edit-input" style="width:90px" />
          </div>
          <div class="edit-actions">
            <button @click="confirmAddTask" class="save-btn">确认</button>
            <button @click="closeModal" class="cancel-btn">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  tasks: { type: Array, required: true },
  date: { type: Date, required: true }
});

const emit = defineEmits(['delete-task', 'toggle-complete', 'update-task', 'add-task']);

const editId = ref(null);
const newTask = ref({
  tdContent: '',
  tdStartTime: '',
  tdEndTime: '',
  tdFinishFlag: 0
});
const editTask = ref({
  tdID: null,
  tdContent: '',
  tdStartTime: '',
  tdEndTime: '',
  tdFinishFlag: 0
});
const showModal = ref(false);

function startEdit(task) {
  editId.value = task.tdID;
  editTask.value = { ...task };
}
function saveEdit() {
  if (!editTask.value.tdContent.trim()) {
    alert('请填写任务名称');
    return;
  }
  if (editTask.value.tdStartTime && editTask.value.tdEndTime && editTask.value.tdStartTime >= editTask.value.tdEndTime) {
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
  newTask.value = { tdContent: '', tdStartTime: '', tdEndTime: '', tdFinishFlag: 0 };
}
function confirmAddTask() {
  if (!newTask.value.tdContent.trim()) {
    alert('请填写任务名称');
    return;
  }
  if (newTask.value.tdStartTime && newTask.value.tdEndTime && newTask.value.tdStartTime >= newTask.value.tdEndTime) {
    alert('开始时间必须早于结束时间');
    return;
  }
  emit('add-task', {
    tdContent: newTask.value.tdContent,
    tdStartTime: newTask.value.tdStartTime,
    tdEndTime: newTask.value.tdEndTime,
    tdFinishFlag: 0,
    tdDate: props.date.toISOString().slice(0, 10) // 关键：带上当前列表的日期
  });
  closeModal();
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
.icon-btn {
  background: #fff;
  border: 1px solid #e0e0e0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;
  margin-right: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.icon-btn.edit-btn:hover {
  background: #e3f2fd;
  border-color: #90caf9;
}
.icon-btn.delete-btn:hover {
  background: #ffebee;
  border-color: #f44336;
}
.icon-btn.complete-btn:hover {
  background: #e8f5e9;
  border-color: #4caf50;
}
.btn-text {
  font-size: 13px;
  margin-left: 2px;
  color: #888;
  display: none;
}
.icon-btn:hover .btn-text {
  display: inline;
  color: #1976d2;
}
.icon-btn.delete-btn:hover .btn-text {
  color: #f44336;
}
.edit-input {
  width: 100%;
  margin-bottom: 6px;
  padding: 5px 8px;
  border-radius: 5px;
  border: 1px solid #e0e0e0;
  font-size: 15px;
  box-sizing: border-box;
  background: #f8fafc;
  transition: border 0.2s;
}
.edit-input:focus {
  border: 1.5px solid #4a6cf7;
  background: #fff;
}
.edit-actions {
  display: flex;
  gap: 10px;
  margin-top: 6px;
}
.save-btn {
  background: #4a6cf7;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 6px 18px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}
.save-btn:hover {
  background: #3a56e0;
}
.cancel-btn {
  background: #f1f5f9;
  color: #333;
  border: none;
  border-radius: 6px;
  padding: 6px 18px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}
.cancel-btn:hover {
  background: #e2e8f0;
}
.task-description {
  color: #64748b;
  font-size: 14px;
  padding-top: 8px;
  border-top: 1px solid #f1f5f9;
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