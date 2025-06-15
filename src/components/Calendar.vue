<template>
  <div class="calendar-container">
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

const emit = defineEmits(['date-selected']);

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
  const dayTasks = props.tasks.filter(task => {
    const taskDate = new Date(task.date);
    return taskDate.getDate() === date.getDate() &&
           taskDate.getMonth() === date.getMonth() &&
           taskDate.getFullYear() === date.getFullYear();
  });
  
  // 返回任务颜色数组（最多3个）
  return dayTasks.slice(0, 3).map(task => task.color);
};
</script>

<style scoped>
.calendar-container {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
  max-width: 500px;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 10px;
}

.weekday {
  padding: 8px 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1/1;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 8px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.calendar-day:hover {
  background-color: #f8fafc;
}

.calendar-day.current-month {
  background-color: white;
}

.calendar-day:not(.current-month) {
  color: #cbd5e1;
}

.calendar-day.today {
  background-color: #dbeafe;
  font-weight: bold;
}

.calendar-day.selected {
  background-color: #eff6ff;
  font-weight: bold;
  color: #1d4ed8;
}

.day-number {
  font-size: 16px;
  font-weight: 500;
}

.events-indicator {
  display: flex;
  justify-content: center;
  gap: 3px;
  width: 100%;
}

.event-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

@media (max-width: 768px) {
  .calendar-container {
    max-width: 100%;
  }
}
</style>