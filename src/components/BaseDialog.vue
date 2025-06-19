<template>
  <div v-if="show" class="modal-overlay" @click="handleOverlayClick">
    <div class="dialog" :class="dialogClass" @click.stop>
      <div class="dialog-header">
        <h3>{{ title }}</h3>
        <button v-if="showCloseButton" class="close-btn" @click="$emit('close')" title="关闭">
          <span>×</span>
        </button>
      </div>
      
      <div class="dialog-content">
        <p v-if="description" class="dialog-description">{{ description }}</p>
        <slot></slot>
      </div>
      
      <div v-if="showFooter" class="dialog-footer">
        <slot name="footer">
          <button 
            v-if="cancelText" 
            @click="$emit('cancel')" 
            class="btn btn-secondary"
          >
            {{ cancelText }}
          </button>
          <button 
            v-if="confirmText" 
            @click="$emit('confirm')" 
            class="btn"
            :class="confirmButtonClass"
          >
            {{ confirmText }}
          </button>
        </slot>
      </div>
      
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    </div>
  </div>
</template>

<script setup>
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
  confirmText: {
    type: String,
    default: ''
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  confirmButtonClass: {
    type: String,
    default: 'btn-primary'
  },
  dialogClass: {
    type: String,
    default: ''
  },
  showCloseButton: {
    type: Boolean,
    default: false
  },
  showFooter: {
    type: Boolean,
    default: true
  },
  errorMessage: {
    type: String,
    default: ''
  },
  closeOnOverlay: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['close', 'confirm', 'cancel']);

const handleOverlayClick = () => {
  if (props.closeOnOverlay) {
    emit('close');
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.dialog {
  background: white;
  border-radius: 8px;
  min-width: 400px;
  max-width: 90vw;
  max-height: 90vh;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 0 20px;
  margin-bottom: 15px;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid #e0e0e0;
  background-color: #fff;
  color: #666;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: #f44336;
  color: white;
  border-color: #f44336;
}

.dialog-content {
  padding: 0 20px;
  flex: 1;
  overflow-y: auto;
}

.dialog-description {
  margin: 0 0 15px 0;
  color: #666;
  line-height: 1.5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px 20px 20px;
  border-top: 1px solid #f0f0f0;
  margin-top: 15px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background-color: #e9e9e9;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
}

.error-message {
  color: #dc3545;
  font-size: 14px;
  margin-top: 10px;
  padding: 8px 12px;
  background-color: rgba(220, 53, 69, 0.1);
  border-radius: 4px;
  margin: 10px 20px 20px 20px;
}

/* 特殊对话框样式 */
.dialog.danger .dialog-header h3 {
  color: #dc3545;
}

.dialog.large {
  min-width: 500px;
  max-width: 600px;
}

.dialog.extra-large {
  min-width: 600px;
  max-width: 800px;
  max-height: 80vh;
}
</style>
