<template>
  <Dialog
    v-model:visible="visible"
    :header="header"
    :modal="true"
    :closable="true"
    :style="{ width: '400px' }"
    class="base-confirm-dialog"
  >
    <div class="confirm-body">
      <i :class="['confirm-icon', icon]" :style="{ color: iconColor }"></i>
      <p class="confirm-message">{{ message }}</p>
    </div>

    <template #footer>
      <Button :label="rejectLabel" severity="secondary" text @click="handleReject" />
      <Button :label="acceptLabel" :severity="severity" @click="handleAccept" :loading="loading" />
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Dialog from 'primevue/dialog'
import Button from 'primevue/button'

const props = withDefaults(
  defineProps<{
    header?: string
    message?: string
    icon?: string
    iconColor?: string
    acceptLabel?: string
    rejectLabel?: string
    severity?: 'primary' | 'danger' | 'warning' | 'success' | 'secondary'
  }>(),
  {
    header: 'Confirm',
    message: 'Are you sure you want to proceed?',
    icon: 'pi pi-exclamation-triangle',
    iconColor: 'var(--eams-warning)',
    acceptLabel: 'Confirm',
    rejectLabel: 'Cancel',
    severity: 'primary'
  }
)

const visible = defineModel<boolean>('visible', { default: false })
const loading = ref(false)

const emit = defineEmits<{
  accept: []
  reject: []
}>()

function handleAccept(): void {
  emit('accept')
  visible.value = false
}

function handleReject(): void {
  emit('reject')
  visible.value = false
}
</script>

<style scoped>
.confirm-body {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 0.5rem 0;
}

.confirm-icon {
  font-size: 1.75rem;
  flex-shrink: 0;
  margin-top: 0.1rem;
}

.confirm-message {
  font-size: 0.9rem;
  color: var(--eams-text);
  line-height: 1.5;
  margin: 0;
}
</style>
