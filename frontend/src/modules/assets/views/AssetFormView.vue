<template>
  <div>
    <BasePageHeader :title="isEdit ? 'Edit Asset' : 'Create Asset'" :subtitle="isEdit ? `Editing ${selectedAsset?.assetCode}` : 'Register a new asset'">
      <template #actions>
        <Button label="Back" icon="pi pi-arrow-left" severity="secondary" text @click="router.back()" />
      </template>
    </BasePageHeader>

    <BaseCard>
      <form class="form-grid" @submit.prevent="handleSubmit">
        <div class="field">
          <label for="assetCode">Asset Code *</label>
          <InputText id="assetCode" v-model="form.assetCode" class="w-full" :disabled="isEdit" placeholder="AST-001" />
          <small v-if="errors.assetCode" class="p-error">{{ errors.assetCode }}</small>
        </div>

        <div class="field">
          <label for="assetName">Asset Name *</label>
          <InputText id="assetName" v-model="form.assetName" class="w-full" placeholder="Developer Laptop" />
          <small v-if="errors.assetName" class="p-error">{{ errors.assetName }}</small>
        </div>

        <div class="field">
          <label for="manufacturer">Manufacturer</label>
          <InputText id="manufacturer" v-model="form.manufacturer" class="w-full" placeholder="Apple" />
        </div>

        <div class="field">
          <label for="model">Model</label>
          <InputText id="model" v-model="form.model" class="w-full" placeholder="MacBook Pro M3" />
        </div>

        <div class="field">
          <label for="serialNumber">Serial Number</label>
          <InputText id="serialNumber" v-model="form.serialNumber" class="w-full" />
        </div>

        <div class="field">
          <label for="status">Status *</label>
          <Select id="status" v-model="form.status" :options="statusOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div class="field">
          <label for="purchaseDate">Purchase Date *</label>
          <DatePicker id="purchaseDate" v-model="form.purchaseDate" date-format="yy-mm-dd" class="w-full" />
          <small v-if="errors.purchaseDate" class="p-error">{{ errors.purchaseDate }}</small>
        </div>

        <div class="field">
          <label for="purchasePrice">Purchase Price *</label>
          <InputNumber id="purchasePrice" v-model="form.purchasePrice" mode="currency" currency="USD" class="w-full" />
          <small v-if="errors.purchasePrice" class="p-error">{{ errors.purchasePrice }}</small>
        </div>

        <div class="field">
          <label for="warrantyExpiry">Warranty Expiry</label>
          <DatePicker id="warrantyExpiry" v-model="form.warrantyExpiry" date-format="yy-mm-dd" class="w-full" />
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
import { useAssetStore } from '../store'
import { useAppToast } from '@/shared/composables'
import { ROUTE_NAMES } from '@/shared/constants'
import { toISODate } from '@/shared/utils'
import type { AssetStatus } from '../types'

const router = useRouter()
const route = useRoute()
const assetStore = useAssetStore()
const { showSuccess, showApiError } = useAppToast()

const isEdit = computed(() => !!route.params.id)
const selectedAsset = computed(() => assetStore.selectedAsset)
const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  assetCode: '',
  assetName: '',
  description: '',
  serialNumber: '',
  manufacturer: '',
  model: '',
  purchaseDate: null as Date | null,
  purchasePrice: null as number | null,
  warrantyExpiry: null as Date | null,
  status: 'AVAILABLE' as AssetStatus
})

const statusOptions = [
  { label: 'Available', value: 'AVAILABLE' },
  { label: 'Assigned', value: 'ASSIGNED' },
  { label: 'In Maintenance', value: 'IN_MAINTENANCE' },
  { label: 'Disposed', value: 'DISPOSED' }
]

onMounted(async () => {
  if (isEdit.value) {
    const id = Number(route.params.id)
    await assetStore.fetchAssetById(id)
    if (assetStore.selectedAsset) {
      const a = assetStore.selectedAsset
      form.assetCode = a.assetCode
      form.assetName = a.assetName
      form.description = a.description || ''
      form.serialNumber = a.serialNumber || ''
      form.manufacturer = a.manufacturer || ''
      form.model = a.model || ''
      form.purchaseDate = a.purchaseDate ? new Date(a.purchaseDate) : null
      form.purchasePrice = a.purchasePrice
      form.warrantyExpiry = a.warrantyExpiry ? new Date(a.warrantyExpiry) : null
      form.status = a.status
    }
  }
})

async function handleSubmit(): Promise<void> {
  if (!validate()) return
  isSubmitting.value = true
  try {
    const payload = {
      assetCode: form.assetCode.trim().toUpperCase(),
      assetName: form.assetName.trim(),
      description: form.description || undefined,
      serialNumber: form.serialNumber || undefined,
      manufacturer: form.manufacturer || undefined,
      model: form.model || undefined,
      purchaseDate: toISODate(form.purchaseDate),
      purchasePrice: form.purchasePrice!,
      warrantyExpiry: form.warrantyExpiry ? toISODate(form.warrantyExpiry) : undefined,
      status: form.status,
      active: true
    }

    if (isEdit.value) {
      await assetStore.updateAsset(Number(route.params.id), payload)
      showSuccess('Asset updated successfully')
    } else {
      await assetStore.createAsset(payload)
      showSuccess('Asset created successfully')
    }
    router.push({ name: ROUTE_NAMES.ASSETS })
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.assetCode = form.assetCode.trim() ? '' : 'Asset code is required'
  errors.assetName = form.assetName.trim() ? '' : 'Asset name is required'
  errors.purchaseDate = form.purchaseDate ? '' : 'Purchase date is required'
  errors.purchasePrice = form.purchasePrice && form.purchasePrice > 0 ? '' : 'Purchase price is required'
  Object.values(errors).forEach((e) => { if (e) valid = false })
  return valid
}
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.25rem;
}

.col-span-2 { grid-column: span 2; }

.field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.field label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--eams-text);
}

.w-full { width: 100%; }
.p-error { color: var(--eams-danger); font-size: 0.75rem; }

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 1px solid var(--eams-border);
}

@media (max-width: 768px) {
  .form-grid { grid-template-columns: 1fr; }
  .col-span-2 { grid-column: span 1; }
}
</style>
