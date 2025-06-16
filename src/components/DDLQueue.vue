<template>
  <div class="ddl-queue-container">
    <div class="ddl-header-row">
      <h2>DDL卡片队列</h2>
      <button class="add-btn" @click="showModal = true" v-if="!adding">添加DDL卡片</button>
    </div>
    <div class="ddl-list-header">
      <div class="ddl-card-list">
        <div v-for="(card, idx) in sortedCards" :key="card.dID" class="ddl-card" :style="{ background: getCardColor(card.dEndTime) }">
          <div class="ddl-title">{{ card.dTitle }}</div>
          <div class="ddl-deadline">截止：{{ formatDeadline(card.dEndTime) }}</div>
          <div class="ddl-remaining">剩余：{{ getRemainDetail(card.dEndTime) }}</div>
          <div class="ddl-desc" v-if="card.dNotes">{{ card.dNotes }}</div>
          <div class="ddl-actions">
            <button
              class="icon-btn edit-btn"
              v-if="editId !== card.dID"
              @click="startEdit(card)"
              title="编辑"
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M4 21h4l11-11-4-4L4 17v4z" stroke="#1976d2" stroke-width="2" stroke-linecap="round"/>
              </svg>
              <span class="btn-text">编辑</span>
            </button>
            <button
              class="icon-btn delete-btn"
              v-if="editId !== card.dID"
              @click="deleteCard(card.dID)"
              title="删除"
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M6 6L18 18M6 18L18 6" stroke="#f44336" stroke-width="2" stroke-linecap="round"/>
              </svg>
              <span class="btn-text">删除</span>
            </button>
            <template v-if="editId === card.dID">
              <input v-model="editCard.dTitle" placeholder="任务名称" class="edit-input" />
              <input type="datetime-local" v-model="editCard.dEndTime" class="edit-input" />
              <textarea v-model="editCard.dNotes" placeholder="备注（可选）" class="edit-textarea"></textarea>
              <div class="edit-actions">
                <button class="save-btn" @click="saveEdit(card.dID)">保存</button>
                <button class="cancel-btn" @click="cancelEdit">取消</button>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
    <!-- 新建DDL卡片弹窗 -->
    <div v-if="showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <h3>新建DDL卡片</h3>
          <input v-model="newCard.dTitle" placeholder="任务名称" class="edit-input" />
          <input type="datetime-local" v-model="newCard.dEndTime" class="edit-input" />
          <textarea v-model="newCard.dNotes" placeholder="备注（可选）" class="edit-textarea"></textarea>
          <div class="edit-actions">
            <button @click="confirmAddCard" class="save-btn">确认</button>
            <button @click="closeModal" class="cancel-btn">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import api from '@/api/auth'
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore();
const currentUserId = computed(() => store.userID || 1); // mock 测试用，默认1

const cards = ref([]);
const showModal = ref(false);
const newCard = ref({
  dTitle: '',
  dEndTime: '',
  dNotes: ''
});

const editId = ref(null);
const editCard = ref({});

function closeModal() {
  showModal.value = false;
  newCard.value = { dTitle: '', dEndTime: '', dNotes: '' };
}
// 新增DDL时
async function confirmAddCard() {
  if (!newCard.value.dTitle.trim() || !newCard.value.dEndTime) {
    alert('请填写任务名称和截止时间');
    return;
  }
  const card = {
    dTitle: newCard.value.dTitle,
    dEndTime: newCard.value.dEndTime,
    dNotes: newCard.value.dNotes,
    uid: currentUserId.value
  };
  await api.addDDL(card);
  await fetchDDLList();
  closeModal();
}

function startEdit(card) {
  editId.value = card.dID;
  editCard.value = { ...card };
}
async function saveEdit(dID) {
  if (!editCard.value.dTitle.trim() || !editCard.value.dEndTime) {
    alert('请填写任务名称和截止时间');
    return;
  }
  await api.modifyDDL(editCard.value); // 调用mockApi或后端接口修改
  await fetchDDLList();                // 重新拉取列表，确保已修改
  editId.value = null;
}
function cancelEdit() {
  editId.value = null;
}
async function deleteCard(dID) {
  await api.deleteDDL({ dID }); // 调用mockApi或后端接口删除
  await fetchDDLList();         // 重新拉取列表，确保已删除
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
  cards.value.sort((a, b) => new Date(a.dEndTime) - new Date(b.dEndTime));
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
  return [...cards.value].sort((a, b) => new Date(a.dEndTime) - new Date(b.dEndTime));
});

async function fetchDDLList() {
  const res = await api.getDDLByUid(currentUserId.value);
  cards.value = res.data.list;
}

onMounted(fetchDDLList);
</script>

<style scoped>
.ddl-queue-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 24px 0;
}
.ddl-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.add-btn {
  background: #4a6cf7;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 20px;
  font-size: 16px;
  cursor: pointer;
  height: 40px;
  align-self: center;
  white-space: nowrap;
  margin-bottom: 0;
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
  gap: 12px;                /* 缩小卡片间距 */
  padding-bottom: 6px;      /* 缩小底部间距 */
  margin-bottom: 4px;       /* 缩小底部外边距 */
  overflow-x: auto;
  padding-bottom: 12px;
  margin-bottom: 8px;
  width: 100%;
}
.ddl-card {
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  padding: 10px 8px 8px 8px;   /* 缩小内边距 */
  margin-bottom: 10px;         /* 缩小卡片底部间距 */
  position: relative;
  transition: background 0.3s;
  min-width: 160px;            /* 缩小宽度 */
  max-width: 180px;
  flex: 0 0 160px;
}
.ddl-card[style] {
  /* 保持原有背景色逻辑 */
}
.ddl-card-edit,
.ddl-actions .edit-input,
.ddl-actions .edit-textarea {
  background: #f4f8ff;
  border: 1.5px solid #4a6cf7;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(74,108,247,0.06);
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
.icon-btn {
  background: #fff;              /* 白色底 */
  border: 1px solid #e0e0e0;     /* 浅灰边框 */
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;
  margin-right: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08); /* 轻微阴影 */
}

.icon-btn.edit-btn:hover {
  background: #e3f2fd;
  border-color: #90caf9;
}

.icon-btn.delete-btn:hover {
  background: #ffebee;
  border-color: #f44336;
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
.edit-input, .edit-textarea {
  width: 100%;
  margin-bottom: 8px;
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid #e0e0e0;
  font-size: 15px;
  box-sizing: border-box;
  background: #f8fafc;
  transition: border 0.2s;
}
.edit-input:focus, .edit-textarea:focus {
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