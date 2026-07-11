<template>
  <div>
    <BasePageHeader :title="isEdit ? 'Edit Purchase Order' : 'Create Purchase Order'" :subtitle="isEdit ? `Editing ${editPoNumber}` : 'Create a new procurement request'">
      <template #actions>
        <Button label="Back" icon="pi pi-arrow-left" severity="secondary" text @click="router.back()" />
      </template>
    </BasePageHeader>

    <BaseCard>
      <form class="form-grid" @submit.prevent="handleSubmit">
        <div class="field">
          <label for="poNumber">PO Number *</label>
          <InputText id="poNumber" v-model="form.poNumber" class="w-full" :disabled="isEdit" placeholder="PO-2026-001" />
          <small v-if="errors.poNumber" class="p-error">{{ errors.poNumber }}</small>
        </div>

        <div class="field">
          <label for="vendorId">Vendor ID *</label>
          <InputNumber id="vendorId" v-model="form.vendorId" class="w-full" :use-grouping="false" />
          <small v-if="errors.vendorId" class="p-error">{{ errors.vendorId }}</small>
        </div>

        <div class="field">
          <label for="status">Status</label>
          <Select id="status" v-model="form.status" :options="statusOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div class="field">
          <label for="expectedDeliveryDate">Expected Delivery Date</label>
          <DatePicker id="expectedDeliveryDate" v-model="form.expectedDeliveryDate" date-format="yy-mm-dd" class="w-full" />
        </div>

        <div class="field col-span-2">
          <label for="remarks">Remarks</label>
          <Textarea id="remarks" v-model="form.remarks" rows="3" class="w-full" />
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
import { usePurchaseOrderStore } from '../store'
import { purchaseOrderService } from '../services'
import { useAppToast } from '@/shared/composables'
import { ROUTE_NAMES } from '@/shared/constants'
import { toISODate } from '@/shared/utils'

const router = useRouter()
const route = useRoute()
const poStore = usePurchaseOrderStore()
const { showSuccess, showApiError } = useAppToast()

const isEdit = computed(() => !!route.params.id)
const editPoNumber = ref('')
const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  poNumber: '',
  vendorId: null as number | null,
  status: 'DRAFT',
  expectedDeliveryDate: null as Date | null,
  remarks: ''
})

const statusOptions = [
  { label: 'Draft', value: 'DRAFT' },
  { label: 'Approved', value: 'APPROVED' },
  { label: 'Ordered', value: 'ORDERED' },
  { label: 'Received', value: 'RECEIVED' },
  { label: 'Cancelled', value: 'CANCELLED' }
]

onMounted(async () => {
  if (isEdit.value) {
    try {
      const po = await purchaseOrderService.getById(Number(route.params.id))
      if (po) {
        editPoNumber.value = po.poNumber
        form.poNumber = po.poNumber
        form.vendorId = po.vendorId
        form.status = po.status
        form.expectedDeliveryDate = po.expectedDeliveryDate ? new Date(po.expectedDeliveryDate) : null
        form.remarks = po.remarks || ''
      }
    } catch { /* stay on empty form */ }
  }
})

async function handleSubmit(): Promise<void> {
  if (!validate()) return
  isSubmitting.value = true
  try {
    const payload = {
      poNumber: form.poNumber.trim().toUpperCase(),
      vendorId: form.vendorId!,
      status: form.status,
      expectedDeliveryDate: form.expectedDeliveryDate ? toISODate(form.expectedDeliveryDate) : undefined,
      remarks: form.remarks || undefined,
      active: true,
      items: []
    }

    if (isEdit.value) {
      await poStore.updateOrder(Number(route.params.id), payload)
      showSuccess('Purchase order updated')
    } else {
      await poStore.createOrder(payload as any)
      showSuccess('Purchase order created')
    }
    router.push({ name: ROUTE_NAMES.PURCHASE_ORDERS })
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.poNumber = form.poNumber.trim() ? '' : 'PO number is required'
  errors.vendorId = form.vendorId && form.vendorId > 0 ? '' : 'Vendor ID is required'
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
