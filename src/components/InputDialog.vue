<template>
  <BaseDialog
    :show="show"
    :title="title"
    :description="description"
    :confirm-text="confirmText"
    :cancel-text="cancelText"
    :error-message="errorMessage"
    @confirm="handleConfirm"
    @cancel="$emit('cancel')"
    @close="$emit('close')"
  >
    <input 
      ref="inputRef"
      v-model="inputValue" 
      :placeholder="placeholder"
      @keyup.enter="handleConfirm"
      @keyup.escape="$emit('cancel')"
      class="input-field"
    />
  </BaseDialog>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import BaseDialog from './BaseDialog.vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  confirmText: {
    type: String,
    default: '确认'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  errorMessage: {
    type: String,
    default: ''
  },
  initialValue: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['confirm', 'cancel', 'close']);

const inputValue = ref('');
const inputRef = ref(null);

// 监听show属性变化，自动聚焦和设置初始值
watch(() => props.show, (newShow) => {
  if (newShow) {
    inputValue.value = props.initialValue;
    nextTick(() => {
      if (inputRef.value) {
        inputRef.value.focus();
        if (props.initialValue) {
          inputRef.value.select();
        }
      }
    });
  }
});

const handleConfirm = () => {
  if (!inputValue.value.trim()) {
    return;
  }
  emit('confirm', inputValue.value.trim());
};
</script>

<style scoped>
.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.input-field:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}
</style>
