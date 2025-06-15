<template>
  <div class="tasks-container">
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

const emit = defineEmits(['delete-task', 'toggle-complete', 'update-task']);

const editId = ref(null);
const editTask = ref({});

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
</script>

<style scoped>
.tasks-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
</style>