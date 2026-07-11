<template>
  <BaseConfirmDialog
    v-model:visible="visible"
    :header="header"
    :message="message"
    icon="pi pi-trash"
    icon-color="var(--eams-danger)"
    accept-label="Delete"
    severity="danger"
    @accept="$emit('confirm')"
    @reject="$emit('cancel')"
  />
</template>

<script setup lang="ts">
import BaseConfirmDialog from './BaseConfirmDialog.vue'

withDefaults(
  defineProps<{
    header?: string
    message?: string
  }>(),
  {
    header: 'Delete Confirmation',
    message: 'Are you sure you want to delete this item? This action cannot be undone.'
  }
)

const visible = defineModel<boolean>('visible', { default: false })

defineEmits<{
  confirm: []
  cancel: []
}>()
</script>
