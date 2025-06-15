<template>
  <div class="ddl-queue-container">
    <h2>DDL卡片队列</h2>
    <div class="ddl-list-header">
      <div class="ddl-card-list">
        <div v-for="(card, idx) in sortedCards" :key="card.id" class="ddl-card" :style="{ background: getCardColor(card.deadline) }">
          <div class="ddl-title">{{ card.title }}</div>
          <div class="ddl-deadline">截止：{{ formatDeadline(card.deadline) }}</div>
          <div class="ddl-remaining">剩余：{{ getRemainDetail(card.deadline) }}</div>
          <div class="ddl-desc" v-if="card.description">{{ card.description }}</div>
          <div class="ddl-actions">
            <button v-if="editId !== card.id" @click="startEdit(card)">编辑</button>
            <button v-if="editId !== card.id" @click="deleteCard(card.id)">删除</button>
            <template v-if="editId === card.id">
              <input v-model="editCard.title" placeholder="任务名称" />
              <input type="datetime-local" v-model="editCard.deadline" />
              <textarea v-model="editCard.description" placeholder="备注（可选）"></textarea>
              <div class="edit-actions">
                <button @click="saveEdit(card.id)">保存</button>
                <button @click="cancelEdit">取消</button>
              </div>
            </template>
          </div>
        </div>
      </div>
      <button class="add-btn" @click="showModal = true" v-if="!adding">添加DDL卡片</button>
    </div>
    <!-- 弹窗 -->
    <div v-if="showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <h3>新建DDL卡片</h3>
          <input v-model="newCard.title" placeholder="任务名称" />
          <input type="datetime-local" v-model="newCard.deadline" />
          <textarea v-model="newCard.description" placeholder="备注（可选）"></textarea>
          <div class="edit-actions">
            <button @click="confirmAddCard">确认</button>
            <button @click="closeModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const cards = ref([]);
const showModal = ref(false);
const newCard = ref({ title: '', deadline: '', description: '' });

const editId = ref(null);
const editCard = ref({});

function closeModal() {
  showModal.value = false;
  newCard.value = { title: '', deadline: '', description: '' };
}
function confirmAddCard() {
  if (!newCard.value.title.trim() || !newCard.value.deadline) {
    alert('请填写任务名称和截止时间');
    return;
  }
  const card = {
    id: Date.now(),
    title: newCard.value.title,
    deadline: newCard.value.deadline,
    description: newCard.value.description
  };
  cards.value.push(card);
  closeModal();
  sortCards();
}

function startEdit(card) {
  editId.value = card.id;
  editCard.value = { ...card };
}
function saveEdit(id) {
  if (!editCard.value.title.trim() || !editCard.value.deadline) {
    alert('请填写任务名称和截止时间');
    return;
  }
  const idx = cards.value.findIndex(c => c.id === id);
  if (idx !== -1) {
    cards.value[idx] = { ...editCard.value, id };
    sortCards();
  }
  editId.value = null;
}
function cancelEdit() {
  editId.value = null;
}
function deleteCard(id) {
  cards.value = cards.value.filter(c => c.id !== id);
}

function getCardColor(deadline) {
  const now = new Date();
  const end = new Date(deadline);
  const diff = (end - now) / 1000 / 3600; // 小时
  if (diff < 48) return '#ff0000';
  if (diff < 120) return '#ff6600';
  if (diff < 168) return '#fff27f';
  return '#95ff2b';
}
function formatDeadline(deadline) {
  if (!deadline) return '';
  const d = new Date(deadline);
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
}
function sortCards() {
  cards.value.sort((a, b) => new Date(a.deadline) - new Date(b.deadline));
}
function getRemainDays(deadline) {
  if (!deadline) return '-';
  const now = new Date();
  const end = new Date(deadline);
  const diff = end - now;
  if (diff < 0) return 0;
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
}
function getRemainDetail(deadline) {
  if (!deadline) return '-';
  const now = new Date();
  const end = new Date(deadline);
  let diff = end - now;
  if (diff < 0) return '0天0小时0分钟';
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  diff -= days * 1000 * 60 * 60 * 24;
  const hours = Math.floor(diff / (1000 * 60 * 60));
  diff -= hours * 1000 * 60 * 60;
  const minutes = Math.floor(diff / (1000 * 60));
  return `${days}天${hours}小时${minutes}分钟`;
}
const sortedCards = computed(() => {
  return [...cards.value].sort((a, b) => new Date(a.deadline) - new Date(b.deadline));
});
</script>

<style scoped>
.ddl-queue-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 24px 0;
}
.ddl-list-header {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 16px;
}
.ddl-card-list {
  display: flex;
  flex-direction: row;
  gap: 24px;
  overflow-x: auto;
  padding-bottom: 12px;
  margin-bottom: 8px;
  width: 100%;
}
.add-btn {
  background: #4a6cf7;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 20px;
  font-size: 16px;
  margin-bottom: 18px;
  cursor: pointer;
  height: 48px;
  align-self: center;
  white-space: nowrap;
}
.ddl-card {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 18px 16px 12px 16px;
  margin-bottom: 18px;
  position: relative;
  transition: background 0.3s;
  min-width: 320px;
  max-width: 360px;
  flex: 0 0 320px;
}
.ddl-title {
  font-weight: 600;
  font-size: 18px;
  margin-bottom: 6px;
}
.ddl-deadline {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}
.ddl-remaining {
  font-size: 14px;
  color: #222; /* 黑色 */
  margin-bottom: 4px;
  font-weight: bold;
}
.ddl-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}
.ddl-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 8px;
}
.ddl-card-edit input,
.ddl-card-edit textarea,
.ddl-actions input,
.ddl-actions textarea {
  display: block;
  width: 100%;
  margin-bottom: 8px;
  font-size: 15px;
  padding: 6px 8px;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  box-sizing: border-box;
}
.edit-actions {
  display: flex;
  gap: 10px;
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
.modal-container input,
.modal-container textarea {
  width: 100%;
  font-size: 15px;
  padding: 8px 10px;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  margin-bottom: 8px;
  box-sizing: border-box;
}
</style>