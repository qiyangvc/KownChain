<template>
  <div class="schedule-container">
    <h1>日程管理</h1>
    <div class="calendar-panel">
      <CalendarComponent
        :current-date="currentDate"
        :selected-date="selectedDate"
        :tasks="tasks"
        @date-selected="selectDate"
        @prev-month="prevMonth"
        @next-month="nextMonth"
      />
    </div>
    <div class="main-content">
      <div class="content">
        <DDLQueue />
      </div>
    </div>
    <!-- 横向分布的当日/次日待办 -->
    <div class="todo-row">
      <div class="todo-col">
        <h2 class="todo-title">当日待办</h2>
        <TaskList 
          :tasks="selectedDayTasks"
          :date="selectedDate"
          @add-task="addTask"
          @delete-task="deleteTask"
          @toggle-complete="toggleComplete"
          @update-task="updateTask"
        />
      </div>
      <div class="todo-col">
        <h2 class="todo-title">次日待办</h2>
        <TaskList 
          :tasks="nextDayTasks"
          :date="nextDayDate"
          @add-task="addTask"
          @delete-task="deleteTask"
          @toggle-complete="toggleComplete"
          @update-task="updateTask"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import CalendarComponent from '@/components/Calendar.vue';
import TaskList from '@/components/TaskList.vue';
import DDLQueue from '@/components/DDLQueue.vue'; // 新增引入
import api from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore();
const currentUserId = computed(() => store.userID || 1);

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

// 获取当天待办
async function fetchTasks() {
  const start = calendarStartDate.value;
  const end = calendarEndDate.value;
  const days = [];
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    days.push(d.toISOString().slice(0, 10));
  }
  // 并发获取所有日期的任务
  const results = await Promise.all(
    days.map(tdDate => api.getTodoByUidAndDate({ uid: currentUserId.value, tdDate }))
  );
  // 合并所有任务
  const all = results.flatMap(res => res.data.list || []);
  // 去重
  tasks.value = Array.from(new Map(all.map(item => [item.tdID, item])).values());
}

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

// 当日任务
const selectedDayStr = computed(() => selectedDate.value.toISOString().slice(0, 10));
const selectedDayTasks = computed(() => tasks.value.filter(task => task.tdDate === selectedDayStr.value));

const nextDayStr = computed(() => {
  const d = new Date(selectedDate.value);
  d.setDate(d.getDate() + 1);
  return d.toISOString().slice(0, 10);
});
const nextDayDate = computed(() => {
  const d = new Date(selectedDate.value);
  d.setDate(d.getDate() + 1);
  return d;
});
const nextDayTasks = computed(() => tasks.value.filter(task => task.tdDate === nextDayStr.value));

// 计算当前日历视图的起止日期（6周42天）
const calendarStartDate = computed(() => {
  const firstDay = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1);
  const start = new Date(firstDay);
  start.setDate(firstDay.getDate() - firstDay.getDay());
  return start;
});
const calendarEndDate = computed(() => {
  const end = new Date(calendarStartDate.value);
  end.setDate(end.getDate() + 41); // 42天
  return end;
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
async function addTask(task) {
  await api.addTodo({
    ...task,
    uid: currentUserId.value
    // 不要覆盖 tdDate
  });
  await fetchTasks();
}

// 删除任务
async function deleteTask(tdID) {
  await api.deleteTodo({ tdID });
  await fetchTasks();
}

// 切换任务完成状态
async function toggleComplete(task) {
  await api.modifyTodo(task); // 这里 task 已经是切换后的状态
  await fetchTasks();         // 重新拉取最新数据
}

// 更新任务
async function updateTask(task) {
  await api.modifyTodo(task);
  await fetchTasks();
}

onMounted(() => {
  // 初始化选中日期为今天
  selectedDate.value = new Date();
  fetchTasks();
});
watch(selectedDate, fetchTasks);
</script>

<style scoped>
.schedule-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 8px 20px 20px 20px; /* 顶部padding由20px改为8px或0，更紧凑 */
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  position: relative;
  padding-right: 340px; /* 预留日历宽度 */
}

.header {
  display: none; /* 隐藏原有 header 区域 */
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
  position: fixed;      /* 关键：fixed 定位到屏幕 */
  top: 0;               /* 顶部对齐 */
  right: 0;             /* 右侧对齐 */
  width: 320px;
  min-width: 220px;
  background: none;
  border-radius: 0;
  box-shadow: none;
  padding: 0;
  height: auto;
  z-index: 100;
  display: block;
}

.content {
  flex: 1;
  min-width: 0;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  padding: 4px 4px 2px 4px;    /* 更小的内边距 */
  margin-bottom: 8px;            /* 更小的下间距 */
}

.todo-title {
  font-size: 20px;
  font-weight: bold;
  color: #2d3748;
  margin: 18px 0 12px 0;
}

.todo-row {
  display: flex;
  gap: 16px;                      /* 缩小间距 */
  margin-top: 16px;               /* 缩小与上方的距离 */
  margin-bottom: 16px;            /* 缩小与下方的距离 */
  justify-content: space-between;
}

.todo-col {
  flex: 1 1 0;
  min-width: 320px;
  max-width: 600px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  padding: 8px 8px 4px 8px;      /* 更小的内边距 */
  margin: 0 2px;                 /* 更小的左右间距 */
  display: flex;
  flex-direction: column;
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
  .todo-row {
    flex-direction: column;
    gap: 20px;
  }
  .todo-col {
    max-width: 100%;
    margin: 0;
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