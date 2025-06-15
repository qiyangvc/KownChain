<template>
  <div class="schedule-container">
    <div class="header">
      <h1>日程管理</h1>
    </div>
    <div class="main-content">
      <!-- 左侧待办 -->
      <div class="content">
        <!-- DDL卡片队列放在待办事项上方 -->
        <DDLQueue />
        <div class="task-controls">
          <div class="add-task">
            <div class="add-task-inputs">
              <input 
                type="text" 
                v-model="newTask" 
                placeholder="添加新任务..." 
                @keyup.enter="addTask"
              >
              <input 
                type="time"
                v-model="newTaskStart"
                style="width: 110px; margin-left: 10px;"
                placeholder="开始时间"
              >
              <span style="margin: 0 6px;">-</span>
              <input 
                type="time"
                v-model="newTaskEnd"
                style="width: 110px;"
                placeholder="结束时间"
              >
            </div>
            <textarea
              v-model="newTaskDesc"
              placeholder="备注（可选）"
              class="add-task-desc"
              rows="2"
              style="margin: 10px 20px 0 20px; resize: vertical;"
            ></textarea>
            <div class="add-task-btn-row">
              <button @click="addTask">添加</button>
            </div>
          </div>
          <div class="date-navigation">
            <button class="nav-btn" @click="prevMonth">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6 1.41-1.41z"/>
              </svg>
            </button>
            <div class="current-date">{{ currentMonth }} {{ currentYear }}</div>
            <button class="nav-btn" @click="nextMonth">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
              </svg>
            </button>
            <button class="today-btn" @click="goToToday">今天</button>
          </div>
        </div>
        <TaskList 
          :tasks="filteredTasks"
          @delete-task="deleteTask"
          @toggle-complete="toggleComplete"
          @update-task="updateTask"
        />
      </div>
      <!-- 右侧日历 -->
      <div class="calendar-panel">
        <div class="date-info merged-date-info">
          <h2>{{ selectedDateFormatted }}</h2>
          <p>{{ getDayOfWeek(selectedDate) }}</p>
        </div>
        <CalendarComponent 
          :current-date="currentDate"
          :selected-date="selectedDate"
          :tasks="tasks"
          @date-selected="selectDate"
          @prev-month="prevMonth"
          @next-month="nextMonth"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import CalendarComponent from '@/components/Calendar.vue';
import TaskList from '@/components/TaskList.vue';
import DDLQueue from '@/components/DDLQueue.vue'; // 新增引入

// 当前日期
const today = new Date();
const currentDate = ref(new Date());
const selectedDate = ref(new Date());
const newTask = ref('');
const newTaskStart = ref('09:00');
const newTaskEnd = ref('10:00');
const newTaskDesc = ref(''); // 新增

// 任务数据
const tasks = ref([
  { id: 1, title: '团队会议', time: '09:30', date: new Date(), description: '讨论项目进度', completed: false, color: '#4a6cf7' },
  { id: 2, title: '提交项目报告', time: '14:00', date: new Date(), description: '包含数据分析部分', completed: false, color: '#ff9800' },
  { id: 3, title: '客户电话', time: '11:00', date: new Date(), description: '讨论新需求', completed: true, color: '#4caf50' },
  { id: 4, title: '产品演示', time: '15:30', date: new Date(new Date().setDate(new Date().getDate() + 1)), description: '为新客户展示产品功能', completed: false, color: '#e91e63' },
  { id: 5, title: '项目截止', time: '17:00', date: new Date(new Date().setDate(new Date().getDate() + 3)), description: '完成所有开发任务', completed: false, color: '#9c27b0' },
]);

// 日期计算
const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => {
  const months = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'];
  return months[currentDate.value.getMonth()];
});

// 格式化日期
const selectedDateFormatted = computed(() => {
  const year = selectedDate.value.getFullYear();
  const month = (selectedDate.value.getMonth() + 1).toString().padStart(2, '0');
  const day = selectedDate.value.getDate().toString().padStart(2, '0');
  return `${year}年${month}月${day}日`;
});

// 获取星期几
const getDayOfWeek = (date) => {
  const days = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  return days[date.getDay()];
};

// 筛选当前选中日期的任务
const filteredTasks = computed(() => {
  return tasks.value
    .filter(task => {
      const taskDate = new Date(task.date);
      return taskDate.toDateString() === selectedDate.value.toDateString();
    })
    .sort((a, b) => {
      // 按时间排序
      if (a.time < b.time) return -1;
      if (a.time > b.time) return 1;
      return 0;
    });
});

// 切换月份
const prevMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1);
};

const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1);
};

// 回到今天
const goToToday = () => {
  currentDate.value = new Date();
  selectedDate.value = new Date();
};

// 选择日期
const selectDate = (date) => {
  selectedDate.value = date;
};

// 添加任务
const addTask = () => {
  if (newTask.value.trim() === '' || !newTaskStart.value || !newTaskEnd.value) return;

  if (newTaskStart.value >= newTaskEnd.value) {
    alert('开始时间必须早于结束时间');
    return;
  }

  const colors = ['#4a6cf7', '#ff9800', '#4caf50', '#e91e63', '#9c27b0'];

  tasks.value.push({
    id: Date.now(),
    title: newTask.value,
    start: newTaskStart.value,
    end: newTaskEnd.value,
    date: new Date(selectedDate.value),
    description: newTaskDesc.value, // 保存备注
    completed: false,
    color: colors[Math.floor(Math.random() * colors.length)]
  });

  newTask.value = '';
  newTaskStart.value = '09:00';
  newTaskEnd.value = '10:00';
  newTaskDesc.value = ''; // 清空备注
};

// 删除任务
const deleteTask = (id) => {
  tasks.value = tasks.value.filter(task => task.id !== id);
};

// 切换任务完成状态
const toggleComplete = (task) => {
  task.completed = !task.completed;
};

// 更新任务
const updateTask = (updatedTask) => {
  const idx = tasks.value.findIndex(t => t.id === updatedTask.id);
  if (idx !== -1) {
    tasks.value[idx] = { ...tasks.value[idx], ...updatedTask };
  }
};

onMounted(() => {
  // 初始化选中日期为今天
  selectedDate.value = new Date();
});
</script>

<style scoped>
.schedule-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header {
  margin-bottom: 20px;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 10px;
}

.header-left {
  flex: 1;
  max-width: 60%;
}

h1 {
  color: #2d3748;
  font-size: 28px;
  margin: 0 0 15px 0;
}

.date-info h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: #1e293b;
}

.date-info p {
  margin: 0;
  color: #64748b;
  font-size: 16px;
}

.calendar-float {
  position: absolute;
  top: 0;
  right: 0;
  width: 320px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 15px;
  z-index: 10;
}

.merged-date-info {
  margin-bottom: 10px;
  text-align: center;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 10px;
}

.calendar-float .date-info h2 {
  font-size: 22px;
  margin-bottom: 2px;
}

.calendar-float .date-info p {
  font-size: 15px;
  color: #64748b;
}

.task-controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 25px;
  gap: 20px;
}

.add-task {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  max-width: 500px;
  padding: 10px 0;
}

.add-task-inputs {
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 0;
}

.add-task input {
  flex: 1;
  border: none;
  padding: 15px 10px;
  font-size: 16px;
  outline: none;
}

.add-task-desc {
  width: 100%;
  border: none;
  border-top: 1px solid #f1f5f9;
  padding: 10px;
  font-size: 15px;
  background: #f8fafc;
  border-radius: 0 0 8px 8px;
  outline: none;
  box-sizing: border-box;
}

.add-task-btn-row {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  padding: 0 20px 10px 20px;
}

.add-task button {
  background: #4a6cf7;
  color: white;
  border: none;
  padding: 0 25px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-left: 0;
  align-self: flex-end;
}

.add-task button:hover {
  background: #3a56e0;
}

.date-navigation {
  display: flex;
  align-items: center;
  gap: 15px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 8px 15px;
}

.current-date {
  font-size: 16px;
  font-weight: 600;
  min-width: 130px;
  text-align: center;
}

.nav-btn {
  background: none;
  border: none;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.nav-btn:hover {
  background-color: #f1f5f9;
}

.nav-btn svg {
  fill: #4a5568;
}

.today-btn {
  background: #f1f5f9;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.today-btn:hover {
  background: #e2e8f0;
}

.main-content {
  display: flex;
  gap: 32px;
}

.calendar-panel {
  width: 340px;
  min-width: 260px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 18px 12px 18px 12px;
  height: fit-content;
}

.content {
  flex: 1;
  min-width: 0;
}

/* 响应式：小屏幕上下布局 */
@media (max-width: 900px) {
  .main-content {
    flex-direction: column;
    gap: 20px;
  }
  .calendar-panel {
    width: 100%;
    min-width: 0;
    margin-bottom: 10px;
  }
}

@media (max-width: 992px) {
  .header {
    flex-direction: column;
    gap: 20px;
  }
  
  .header-left {
    max-width: 100%;
  }
  
  .calendar-float {
    position: relative;
    width: 100%;
    max-width: 100%;
    margin-top: 15px;
    right: auto;
    top: auto;
  }
  
  .task-controls {
    flex-direction: column;
  }
  
  .add-task {
    max-width: 100%;
  }
  
  .date-navigation {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .date-navigation {
    flex-wrap: wrap;
    justify-content: center;
    padding: 10px;
  }
  
  .current-date {
    min-width: 100%;
    text-align: center;
    margin: 5px 0;
    order: -1;
  }
}
</style>