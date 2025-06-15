<template>
  <div class="schedule-container">
    <div class="header">
      <div class="header-left">
        <h1>日程管理</h1>
        <div class="date-info">
          <h2>{{ selectedDateFormatted }}</h2>
          <p>{{ getDayOfWeek(selectedDate) }}</p>
        </div>
      </div>
      
      <div class="calendar-float">
        <CalendarComponent 
          :current-date="currentDate"
          :selected-date="selectedDate"
          :tasks="tasks"
          @date-selected="selectDate"
        />
      </div>
    </div>

    <div class="content">
      <div class="task-controls">
        <div class="add-task">
          <input 
            type="text" 
            v-model="newTask" 
            placeholder="添加新任务..." 
            @keyup.enter="addTask"
          >
          <button @click="addTask">添加</button>
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
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import CalendarComponent from '@/components/Calendar.vue';
import TaskList from '@/components/TaskList.vue';

// 当前日期
const today = new Date();
const currentDate = ref(new Date());
const selectedDate = ref(new Date());
const newTask = ref('');

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
  return `${year}-${month}-${day}`;
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
  if (newTask.value.trim() === '') return;
  
  const colors = ['#4a6cf7', '#ff9800', '#4caf50', '#e91e63', '#9c27b0'];
  
  tasks.value.push({
    id: Date.now(),
    title: newTask.value,
    time: '09:00',
    date: new Date(selectedDate.value),
    description: '',
    completed: false,
    color: colors[Math.floor(Math.random() * colors.length)]
  });
  
  newTask.value = '';
};

// 删除任务
const deleteTask = (id) => {
  tasks.value = tasks.value.filter(task => task.id !== id);
};

// 切换任务完成状态
const toggleComplete = (task) => {
  task.completed = !task.completed;
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eaeaea;
  position: relative;
}

.header-left {
  flex: 1;
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
  width: 340px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 15px;
  z-index: 10;
}

.task-controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 25px;
  gap: 20px;
}

.add-task {
  flex: 1;
  display: flex;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  max-width: 500px;
}

.add-task input {
  flex: 1;
  border: none;
  padding: 15px 20px;
  font-size: 16px;
  outline: none;
}

.add-task button {
  background: #4a6cf7;
  color: white;
  border: none;
  padding: 0 25px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
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

@media (max-width: 992px) {
  .header {
    flex-direction: column;
    gap: 20px;
  }
  
  .calendar-float {
    position: relative;
    width: 100%;
    max-width: 100%;
    margin-top: 15px;
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