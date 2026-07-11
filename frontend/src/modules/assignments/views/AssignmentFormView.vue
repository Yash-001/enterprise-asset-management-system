<template>
  <div>
    <BasePageHeader title="Create Assignment" subtitle="Assign an asset to an employee">
      <template #actions>
        <Button label="Back" icon="pi pi-arrow-left" severity="secondary" text @click="router.back()" />
      </template>
    </BasePageHeader>

    <BaseCard>
      <form class="form-grid" @submit.prevent="handleSubmit">
        <div class="field">
          <label for="assetId">Asset ID *</label>
          <InputNumber id="assetId" v-model="form.assetId" class="w-full" :use-grouping="false" />
          <small v-if="errors.assetId" class="p-error">{{ errors.assetId }}</small>
        </div>

        <div class="field">
          <label for="employeeId">Employee ID *</label>
          <InputNumber id="employeeId" v-model="form.employeeId" class="w-full" :use-grouping="false" />
          <small v-if="errors.employeeId" class="p-error">{{ errors.employeeId }}</small>
        </div>

        <div class="field">
          <label for="assignedDate">Assigned Date *</label>
          <DatePicker id="assignedDate" v-model="form.assignedDate" date-format="yy-mm-dd" class="w-full" />
          <small v-if="errors.assignedDate" class="p-error">{{ errors.assignedDate }}</small>
        </div>

        <div class="field">
          <label for="expectedReturnDate">Expected Return Date *</label>
          <DatePicker id="expectedReturnDate" v-model="form.expectedReturnDate" date-format="yy-mm-dd" class="w-full" />
          <small v-if="errors.expectedReturnDate" class="p-error">{{ errors.expectedReturnDate }}</small>
        </div>

        <div class="field col-span-2">
          <label for="remarks">Remarks</label>
          <Textarea id="remarks" v-model="form.remarks" rows="3" class="w-full" />
        </div>

        <div class="form-actions col-span-2">
          <Button label="Cancel" severity="secondary" @click="router.back()" />
          <Button type="submit" label="Assign Asset" icon="pi pi-check" :loading="isSubmitting" />
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import InputNumber from 'primevue/inputnumber'
import Textarea from 'primevue/textarea'
import DatePicker from 'primevue/datepicker'
import Button from 'primevue/button'
import { useAssignmentStore } from '../store'
import { useAppToast } from '@/shared/composables'
import { ROUTE_NAMES } from '@/shared/constants'
import { toISODate } from '@/shared/utils'

const router = useRouter()
const assignmentStore = useAssignmentStore()
const { showSuccess, showApiError } = useAppToast()

const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  assetId: null as number | null,
  employeeId: null as number | null,
  assignedDate: null as Date | null,
  expectedReturnDate: null as Date | null,
  remarks: ''
})

async function handleSubmit(): Promise<void> {
  if (!validate()) return
  isSubmitting.value = true
  try {
    await assignmentStore.createAssignment({
      assetId: form.assetId!,
      employeeId: form.employeeId!,
      assignedDate: toISODate(form.assignedDate),
      expectedReturnDate: toISODate(form.expectedReturnDate),
      remarks: form.remarks || undefined
    })
    showSuccess('Asset assigned successfully')
    router.push({ name: ROUTE_NAMES.ASSIGNMENTS })
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.assetId = form.assetId && form.assetId > 0 ? '' : 'Asset ID is required'
  errors.employeeId = form.employeeId && form.employeeId > 0 ? '' : 'Employee ID is required'
  errors.assignedDate = form.assignedDate ? '' : 'Assigned date is required'
  errors.expectedReturnDate = form.expectedReturnDate ? '' : 'Expected return date is required'
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
