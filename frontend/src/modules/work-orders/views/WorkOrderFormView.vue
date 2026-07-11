<template>
  <div>
    <BasePageHeader :title="isEdit ? 'Edit Work Order' : 'Create Work Order'" :subtitle="isEdit ? `Editing ${selectedWorkOrder?.workOrderNumber}` : 'Schedule a new work order'">
      <template #actions>
        <Button label="Back" icon="pi pi-arrow-left" severity="secondary" text @click="router.back()" />
      </template>
    </BasePageHeader>

    <BaseCard>
      <form class="form-grid" @submit.prevent="handleSubmit">
        <div class="field">
          <label for="workOrderNumber">Work Order Number *</label>
          <InputText id="workOrderNumber" v-model="form.workOrderNumber" class="w-full" :disabled="isEdit" placeholder="WO-2026-001" />
          <small v-if="errors.workOrderNumber" class="p-error">{{ errors.workOrderNumber }}</small>
        </div>

        <div class="field">
          <label for="title">Title *</label>
          <InputText id="title" v-model="form.title" class="w-full" placeholder="Replace HVAC Compressor" />
          <small v-if="errors.title" class="p-error">{{ errors.title }}</small>
        </div>

        <div class="field">
          <label for="assetId">Asset ID *</label>
          <InputNumber id="assetId" v-model="form.assetId" class="w-full" :use-grouping="false" placeholder="1" />
          <small v-if="errors.assetId" class="p-error">{{ errors.assetId }}</small>
        </div>

        <div class="field">
          <label for="priority">Priority *</label>
          <Select id="priority" v-model="form.priority" :options="priorityOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div class="field">
          <label for="status">Status</label>
          <Select id="status" v-model="form.status" :options="statusOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div class="field">
          <label for="assignedTechnician">Assigned Technician</label>
          <InputText id="assignedTechnician" v-model="form.assignedTechnician" class="w-full" placeholder="John Doe" />
        </div>

        <div class="field">
          <label for="scheduledDate">Scheduled Date</label>
          <DatePicker id="scheduledDate" v-model="form.scheduledDate" date-format="yy-mm-dd" class="w-full" />
        </div>

        <div class="field">
          <label for="estimatedHours">Estimated Hours</label>
          <InputNumber id="estimatedHours" v-model="form.estimatedHours" class="w-full" :min-fraction-digits="1" :max-fraction-digits="2" />
        </div>

        <div class="field col-span-2">
          <label for="description">Description</label>
          <Textarea id="description" v-model="form.description" rows="3" class="w-full" />
        </div>

        <div class="field col-span-2">
          <label for="remarks">Remarks</label>
          <Textarea id="remarks" v-model="form.remarks" rows="2" class="w-full" />
        </div>

        <div class="form-actions col-span-2">
          <Button label="Cancel" severity="secondary" @click="router.back()" />
          <Button type="submit" :label="isEdit ? 'Update' : 'Create'" icon="pi pi-check" :loading="isSubmitting" />
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Textarea from 'primevue/textarea'
import Select from 'primevue/select'
import DatePicker from 'primevue/datepicker'
import Button from 'primevue/button'
import { useWorkOrderStore } from '../store'
import { useAppToast } from '@/shared/composables'
import { ROUTE_NAMES } from '@/shared/constants'
import { toISODate } from '@/shared/utils'
import type { WorkOrderStatus, MaintenancePriority } from '../types'

const router = useRouter()
const route = useRoute()
const workOrderStore = useWorkOrderStore()
const { showSuccess, showApiError } = useAppToast()

const isEdit = computed(() => !!route.params.id)
const selectedWorkOrder = computed(() => workOrderStore.selectedWorkOrder)
const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  workOrderNumber: '',
  title: '',
  assetId: null as number | null,
  description: '',
  assignedTechnician: '',
  priority: 'MEDIUM' as MaintenancePriority,
  status: 'REQUESTED' as WorkOrderStatus,
  scheduledDate: null as Date | null,
  estimatedHours: null as number | null,
  remarks: ''
})

const priorityOptions = [
  { label: 'Low', value: 'LOW' },
  { label: 'Medium', value: 'MEDIUM' },
  { label: 'High', value: 'HIGH' },
  { label: 'Critical', value: 'CRITICAL' }
]

const statusOptions = [
  { label: 'Requested', value: 'REQUESTED' },
  { label: 'Assigned', value: 'ASSIGNED' },
  { label: 'In Progress', value: 'IN_PROGRESS' },
  { label: 'Completed', value: 'COMPLETED' },
  { label: 'Cancelled', value: 'CANCELLED' }
]

onMounted(async () => {
  if (isEdit.value) {
    const id = Number(route.params.id)
    try {
      const wo = await workOrderStore.fetchWorkOrderById(id)
      if (wo) {
        form.workOrderNumber = wo.workOrderNumber
        form.title = wo.title
        form.assetId = wo.assetId
        form.description = wo.description || ''
        form.assignedTechnician = wo.assignedTechnician || ''
        form.priority = wo.priority
        form.status = wo.status
        form.scheduledDate = wo.scheduledDate ? new Date(wo.scheduledDate) : null
        form.estimatedHours = wo.estimatedHours
        form.remarks = wo.remarks || ''
      }
    } catch {
      // If fetch fails, stay on empty form
    }
  }
})

async function handleSubmit(): Promise<void> {
  if (!validate()) return
  isSubmitting.value = true
  try {
    const payload = {
      workOrderNumber: form.workOrderNumber.trim().toUpperCase(),
      assetId: form.assetId!,
      title: form.title.trim(),
      description: form.description || undefined,
      assignedTechnician: form.assignedTechnician || undefined,
      priority: form.priority,
      status: form.status,
      scheduledDate: form.scheduledDate ? toISODate(form.scheduledDate) : undefined,
      estimatedHours: form.estimatedHours || undefined,
      remarks: form.remarks || undefined
    }

    if (isEdit.value) {
      await workOrderStore.updateWorkOrder(Number(route.params.id), payload)
      showSuccess('Work order updated successfully')
    } else {
      await workOrderStore.createWorkOrder(payload)
      showSuccess('Work order created successfully')
    }
    router.push({ name: ROUTE_NAMES.WORK_ORDERS })
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.workOrderNumber = form.workOrderNumber.trim() ? '' : 'Work order number is required'
  errors.title = form.title.trim() ? '' : 'Title is required'
  errors.assetId = form.assetId && form.assetId > 0 ? '' : 'Asset ID is required'
  Object.values(errors).forEach((e) => { if (e) valid = false })
  return valid
}
</script>

<style scoped>
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; }
.col-span-2 { grid-column: span 2; }
.field { display: flex; flex-direction: column; gap: 0.4rem; }
.field label { font-size: 0.85rem; font-weight: 600; color: var(--eams-text); }
.w-full { width: 100%; }
.p-error { color: var(--eams-danger); font-size: 0.75rem; }
.form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; padding-top: 1rem; border-top: 1px solid var(--eams-border); }
@media (max-width: 768px) { .form-grid { grid-template-columns: 1fr; } .col-span-2 { grid-column: span 1; } }
</style>
