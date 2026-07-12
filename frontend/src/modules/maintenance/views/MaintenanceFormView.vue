<template>
  <div>
    <BasePageHeader :title="isEdit ? 'Edit Maintenance Plan' : 'Create Maintenance Plan'" :subtitle="isEdit ? `Editing ${selectedPlan?.planCode}` : 'Schedule a new maintenance plan'">
      <template #actions>
        <Button label="Back" icon="pi pi-arrow-left" severity="secondary" text @click="router.back()" />
      </template>
    </BasePageHeader>

    <BaseCard>
      <form class="form-grid" @submit.prevent="handleSubmit">
        <div class="field">
          <label for="planCode">Plan Code *</label>
          <InputText id="planCode" v-model="form.planCode" class="w-full" :disabled="isEdit" placeholder="MP-HVAC-001" />
          <small v-if="errors.planCode" class="p-error">{{ errors.planCode }}</small>
        </div>

        <div class="field">
          <label for="planName">Plan Name *</label>
          <InputText id="planName" v-model="form.planName" class="w-full" placeholder="Quarterly HVAC Inspection" />
          <small v-if="errors.planName" class="p-error">{{ errors.planName }}</small>
        </div>

        <div class="field">
          <label for="assetId">Asset ID *</label>
          <InputNumber id="assetId" v-model="form.assetId" class="w-full" :use-grouping="false" />
          <small v-if="errors.assetId" class="p-error">{{ errors.assetId }}</small>
        </div>

        <div class="field">
          <label for="maintenanceType">Maintenance Type *</label>
          <Select id="maintenanceType" v-model="form.maintenanceType" :options="typeOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div class="field">
          <label for="frequencyType">Frequency Type *</label>
          <Select id="frequencyType" v-model="form.frequencyType" :options="frequencyTypeOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div class="field">
          <label for="frequencyValue">Frequency Value *</label>
          <InputNumber id="frequencyValue" v-model="form.frequencyValue" class="w-full" :min="1" />
          <small v-if="errors.frequencyValue" class="p-error">{{ errors.frequencyValue }}</small>
        </div>

        <div class="field">
          <label for="nextMaintenanceDate">Next Maintenance Date *</label>
          <DatePicker id="nextMaintenanceDate" v-model="form.nextMaintenanceDate" date-format="yy-mm-dd" class="w-full" />
          <small v-if="errors.nextMaintenanceDate" class="p-error">{{ errors.nextMaintenanceDate }}</small>
        </div>

        <div class="field">
          <label for="priority">Priority *</label>
          <Select id="priority" v-model="form.priority" :options="priorityOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div class="field">
          <label for="estimatedDurationHours">Estimated Duration (hours)</label>
          <InputNumber id="estimatedDurationHours" v-model="form.estimatedDurationHours" class="w-full" :min-fraction-digits="1" />
        </div>

        <div class="field col-span-2">
          <label for="description">Description</label>
          <Textarea id="description" v-model="form.description" rows="3" class="w-full" />
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
import { useMaintenanceStore } from '../store'
import { maintenanceService } from '../services'
import { useAppToast } from '@/shared/composables'
import { ROUTE_NAMES } from '@/shared/constants'
import { toISODate } from '@/shared/utils'

const router = useRouter()
const route = useRoute()
const maintenanceStore = useMaintenanceStore()
const { showSuccess, showApiError } = useAppToast()

const isEdit = computed(() => !!route.params.id)
const selectedPlan = computed(() => maintenanceStore.selectedPlan)
const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  planCode: '',
  planName: '',
  assetId: null as number | null,
  description: '',
  maintenanceType: 'PREVENTIVE',
  frequencyType: 'MONTHLY',
  frequencyValue: 1 as number | null,
  nextMaintenanceDate: null as Date | null,
  priority: 'MEDIUM',
  estimatedDurationHours: null as number | null
})

const typeOptions = [
  { label: 'Preventive', value: 'PREVENTIVE' },
  { label: 'Corrective', value: 'CORRECTIVE' },
  { label: 'Predictive', value: 'PREDICTIVE' }
]

const frequencyTypeOptions = [
  { label: 'Daily', value: 'DAILY' },
  { label: 'Weekly', value: 'WEEKLY' },
  { label: 'Monthly', value: 'MONTHLY' },
  { label: 'Yearly', value: 'YEARLY' }
]

const priorityOptions = [
  { label: 'Low', value: 'LOW' },
  { label: 'Medium', value: 'MEDIUM' },
  { label: 'High', value: 'HIGH' },
  { label: 'Critical', value: 'CRITICAL' }
]

onMounted(async () => {
  if (isEdit.value) {
    try {
      const plan = await maintenanceService.getById(Number(route.params.id))
      if (plan) {
        form.planCode = plan.planCode
        form.planName = plan.planName
        form.assetId = plan.assetId
        form.description = plan.description || ''
        form.maintenanceType = plan.maintenanceType
        form.frequencyType = plan.frequencyType
        form.frequencyValue = plan.frequencyValue
        form.nextMaintenanceDate = plan.nextMaintenanceDate ? new Date(plan.nextMaintenanceDate) : null
        form.priority = plan.priority
        form.estimatedDurationHours = plan.estimatedDurationHours ?? null
      }
    } catch { /* stay on empty form */ }
  }
})

async function handleSubmit(): Promise<void> {
  if (!validate()) return
  isSubmitting.value = true
  try {
    const payload = {
      planCode: form.planCode.trim().toUpperCase(),
      planName: form.planName.trim(),
      assetId: form.assetId!,
      description: form.description || undefined,
      maintenanceType: form.maintenanceType,
      frequencyType: form.frequencyType,
      frequencyValue: form.frequencyValue!,
      nextMaintenanceDate: toISODate(form.nextMaintenanceDate),
      priority: form.priority,
      estimatedDurationHours: form.estimatedDurationHours || undefined
    }

    if (isEdit.value) {
      await maintenanceStore.updatePlan(Number(route.params.id), payload)
      showSuccess('Maintenance plan updated')
    } else {
      await maintenanceStore.createPlan(payload as any)
      showSuccess('Maintenance plan created')
    }
    router.push({ name: ROUTE_NAMES.MAINTENANCE })
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.planCode = form.planCode.trim() ? '' : 'Plan code is required'
  errors.planName = form.planName.trim() ? '' : 'Plan name is required'
  errors.assetId = form.assetId && form.assetId > 0 ? '' : 'Asset ID is required'
  errors.frequencyValue = form.frequencyValue && form.frequencyValue > 0 ? '' : 'Frequency value is required'
  errors.nextMaintenanceDate = form.nextMaintenanceDate ? '' : 'Next maintenance date is required'
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
