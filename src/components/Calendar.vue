<template>
  <div class="calendar-container">
    <div class="calendar-header">
      <div class="calendar-title">日历</div>
      <div class="calendar-actions">
        <button class="action-btn" @click="$emit('prev-month')">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path d="M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6 1.41-1.41z"/>
          </svg>
        </button>
        <button class="action-btn" @click="$emit('next-month')">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
          </svg>
        </button>
        <button class="today-btn" @click="$emit('date-selected', new Date())">今天</button>
      </div>
    </div>
    <!-- 新增：显示年月日和星期 -->
    <div class="calendar-date-info">
      <div class="calendar-date-title">{{ selectedDateFormatted }}</div>
      <div class="calendar-date-week">{{ getDayOfWeek(props.selectedDate) }}</div>
    </div>
    <div class="weekdays">
      <div v-for="day in ['日', '一', '二', '三', '四', '五', '六']" :key="day" class="weekday">
        {{ day }}
      </div>
    </div>
    <div class="calendar-grid">
      <div 
        v-for="(day, index) in calendarDays" 
        :key="index" 
        class="calendar-day"
        :class="{
          'current-month': day.isCurrentMonth,
          'today': day.isToday,
          'selected': day.isSelected
        }"
        @click="$emit('date-selected', day.date)"
      >
        <div class="calendar-day-cell">
          <div class="day-number">{{ day.date.getDate() }}</div>
          <div class="events-indicator">
            <span 
              v-for="(color, i) in getDayEvents(day.date)" 
              :key="i" 
              class="event-dot" 
              :style="{ backgroundColor: color }"
            ></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  currentDate: {
    type: Date,
    required: true
  },
  selectedDate: {
    type: Date,
    required: true
  },
  tasks: {
    type: Array,
    required: true
  }
});

defineEmits(['date-selected', 'prev-month', 'next-month']);

// 生成日历
const calendarDays = computed(() => {
  const year = props.currentDate.getFullYear();
  const month = props.currentDate.getMonth();
  
  // 当月第一天
  const firstDay = new Date(year, month, 1);
  // 当月最后一天
  const lastDay = new Date(year, month + 1, 0);
  // 日历开始日期（上个月的最后几天）
  const startDay = new Date(firstDay);
  startDay.setDate(firstDay.getDate() - firstDay.getDay());
  
  const today = new Date();
  const days = [];
  
  // 生成42天（6周）的日历
  for (let i = 0; i < 42; i++) {
    const date = new Date(startDay);
    date.setDate(startDay.getDate() + i);
    
    days.push({
      date,
      isCurrentMonth: date.getMonth() === month,
      isToday: date.toDateString() === today.toDateString(),
      isSelected: date.toDateString() === props.selectedDate.toDateString()
    });
  }
  
  return days;
});

// 获取某一天的任务标记
const getDayEvents = (date) => {
  const dateStr = date.toISOString().slice(0, 10);
  const dayTasks = props.tasks.filter(task => task.tdDate === dateStr);
  return Array(dayTasks.length).fill('#4a6cf7');
};

// 新增：格式化年月日
const selectedDateFormatted = computed(() => {
  const year = props.selectedDate.getFullYear();
  const month = (props.selectedDate.getMonth() + 1).toString().padStart(2, '0');
  const day = props.selectedDate.getDate().toString().padStart(2, '0');
  return `${year}年${month}月${day}日`;
});
// 新增：获取星期几
const getDayOfWeek = (date) => {
  const days = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  return days[date.getDay()];
};

console.log('calendar tasks:', props.tasks);
</script>

<style scoped>
.calendar-container {
  position: fixed;
  top: 24px;
  right: 10px; /* 原来是24px，向左移远离DDL卡片 */
  width: 340px;
  max-width: 90vw;
  min-width: 220px;
  z-index: 2000;
  background: white;
  border-radius: 14px;
  box-shadow: 0 6px 32px rgba(60, 60, 100, 0.12), 0 1.5px 6px rgba(0,0,0,0.03);
  padding: 18px 14px 18px 14px;
  box-sizing: border-box;
  transition: box-shadow 0.2s;
  /* margin-left: 32px;  // 移除这行 */
}

@media (max-width: 600px) {
  .calendar-container {
    top: 8px;
    right: 8px;
    width: 98vw;
    min-width: 0;
    padding: 6px 2px;
    border-radius: 8px;
  }
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.calendar-title {
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
}

.calendar-actions {
  display: flex;
  gap: 5px;
}

.action-btn {
  background: #f1f5f9;
  border: none;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e2e8f0;
}

.action-btn svg {
  fill: #4a5568;
}

.today-btn {
  background-color: #1d4ed8;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 6px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.today-btn:hover {
  background-color: #1e40af;
}

.calendar-date-info {
  text-align: center;
  margin-bottom: 10px;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 10px;
}

.calendar-date-title {
  font-size: 20px;
  color: #1e293b;
  font-weight: bold;
}

.calendar-date-week {
  font-size: 15px;
  color: #64748b;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-weight: 500;
  color: #64748b;
  font-size: 14px;
  margin-bottom: 8px;
}

.weekday {
  padding: 5px 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  min-width: 320px;
  width: 100%;
}

.calendar-day {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 保证正方形，适配所有宽度 */
  border-radius: 8px;
  background: white;
  box-sizing: border-box;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.calendar-day:hover {
  background-color: #f8fafc;
}

.calendar-day.current-month {
  background-color: white;
  color: #2d3748;
}

.calendar-day:not(.current-month) {
  color: #cbd5e1;
}

.calendar-day.today {
  background-color: #dbeafe;
  font-weight: bold;
  color: #1d4ed8;
}

.calendar-day.selected {
  background-color: #eff6ff;
  font-weight: bold;
  color: #1d4ed8;
  box-shadow: 0 0 0 2px #93c5fd;
}

.calendar-day-cell {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  height: 100%;
}

.day-number {
  font-weight: 500;
  margin-bottom: auto;
  z-index: 1;
}

.events-indicator {
  margin-top: auto;
  margin-bottom: 4px;
  min-height: 10px;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 2px;
}

.event-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4a6cf7;
  display: inline-block;
}
</style>