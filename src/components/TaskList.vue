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
        <div class="task-info">
          <div class="task-title">{{ task.title }}</div>
          <div class="task-time">{{ formatTime(task.time) }}</div>
        </div>
        <div class="task-actions">
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
      <div v-if="task.description" class="task-description">
        {{ task.description }}
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  tasks: {
    type: Array,
    required: true
  }
});

defineEmits(['delete-task', 'toggle-complete']);

// 格式化时间
const formatTime = (time) => {
  return time;
};
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