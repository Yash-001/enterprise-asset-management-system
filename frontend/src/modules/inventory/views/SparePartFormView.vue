<template>
  <div>
    <BasePageHeader :title="isEdit ? 'Edit Spare Part' : 'Add Spare Part'" :subtitle="isEdit ? `Editing ${editPartNumber}` : 'Register a new spare part in inventory'">
      <template #actions>
        <Button label="Back" icon="pi pi-arrow-left" severity="secondary" text @click="router.back()" />
      </template>
    </BasePageHeader>

    <BaseCard>
      <form class="form-grid" @submit.prevent="handleSubmit">
        <div class="field">
          <label for="partNumber">Part Number *</label>
          <InputText id="partNumber" v-model="form.partNumber" class="w-full" :disabled="isEdit" placeholder="SP-HVAC-001" />
          <small v-if="errors.partNumber" class="p-error">{{ errors.partNumber }}</small>
        </div>

        <div class="field">
          <label for="partName">Part Name *</label>
          <InputText id="partName" v-model="form.partName" class="w-full" placeholder="HVAC Drive Belt" />
          <small v-if="errors.partName" class="p-error">{{ errors.partName }}</small>
        </div>

        <div class="field">
          <label for="category">Category</label>
          <InputText id="category" v-model="form.category" class="w-full" placeholder="Belts & Pulleys" />
        </div>

        <div class="field">
          <label for="manufacturer">Manufacturer</label>
          <InputText id="manufacturer" v-model="form.manufacturer" class="w-full" placeholder="Optibelt" />
        </div>

        <div class="field">
          <label for="unitOfMeasure">Unit of Measure</label>
          <InputText id="unitOfMeasure" v-model="form.unitOfMeasure" class="w-full" placeholder="PCS" />
        </div>

        <div class="field">
          <label for="unitCost">Unit Cost *</label>
          <InputNumber id="unitCost" v-model="form.unitCost" mode="currency" currency="USD" class="w-full" />
          <small v-if="errors.unitCost" class="p-error">{{ errors.unitCost }}</small>
        </div>

        <div class="field">
          <label for="currentStock">Current Stock *</label>
          <InputNumber id="currentStock" v-model="form.currentStock" class="w-full" :min="0" />
          <small v-if="errors.currentStock" class="p-error">{{ errors.currentStock }}</small>
        </div>

        <div class="field">
          <label for="minimumStock">Minimum Stock *</label>
          <InputNumber id="minimumStock" v-model="form.minimumStock" class="w-full" :min="0" />
          <small v-if="errors.minimumStock" class="p-error">{{ errors.minimumStock }}</small>
        </div>

        <div class="field">
          <label for="maximumStock">Maximum Stock</label>
          <InputNumber id="maximumStock" v-model="form.maximumStock" class="w-full" :min="0" />
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
import Button from 'primevue/button'
import { useInventoryStore } from '../store'
import { inventoryService } from '../services'
import { useAppToast } from '@/shared/composables'
import { ROUTE_NAMES } from '@/shared/constants'

const router = useRouter()
const route = useRoute()
const inventoryStore = useInventoryStore()
const { showSuccess, showApiError } = useAppToast()

const isEdit = computed(() => !!route.params.id)
const editPartNumber = ref('')
const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  partNumber: '',
  partName: '',
  description: '',
  manufacturer: '',
  category: '',
  unitOfMeasure: '',
  currentStock: 0 as number | null,
  minimumStock: 5 as number | null,
  maximumStock: null as number | null,
  unitCost: null as number | null
})

onMounted(async () => {
  if (isEdit.value) {
    try {
      const part = await inventoryService.getById(Number(route.params.id))
      if (part) {
        editPartNumber.value = part.partNumber
        form.partNumber = part.partNumber
        form.partName = part.partName
        form.description = part.description || ''
        form.manufacturer = part.manufacturer || ''
        form.category = part.category || ''
        form.unitOfMeasure = part.unitOfMeasure || ''
        form.currentStock = part.currentStock
        form.minimumStock = part.minimumStock
        form.maximumStock = part.maximumStock
        form.unitCost = part.unitCost
      }
    } catch { /* stay on empty form */ }
  }
})

async function handleSubmit(): Promise<void> {
  if (!validate()) return
  isSubmitting.value = true
  try {
    const payload = {
      partNumber: form.partNumber.trim().toUpperCase(),
      partName: form.partName.trim(),
      description: form.description || undefined,
      manufacturer: form.manufacturer || undefined,
      category: form.category || undefined,
      unitOfMeasure: form.unitOfMeasure || undefined,
      currentStock: form.currentStock!,
      minimumStock: form.minimumStock!,
      maximumStock: form.maximumStock || undefined,
      unitCost: form.unitCost!
    }

    if (isEdit.value) {
      await inventoryStore.updateSparePart(Number(route.params.id), payload)
      showSuccess('Spare part updated')
    } else {
      await inventoryStore.createSparePart(payload)
      showSuccess('Spare part created')
    }
    router.push({ name: ROUTE_NAMES.INVENTORY })
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.partNumber = form.partNumber.trim() ? '' : 'Part number is required'
  errors.partName = form.partName.trim() ? '' : 'Part name is required'
  errors.unitCost = form.unitCost && form.unitCost > 0 ? '' : 'Unit cost is required'
  errors.currentStock = form.currentStock !== null && form.currentStock >= 0 ? '' : 'Current stock is required'
  errors.minimumStock = form.minimumStock !== null && form.minimumStock >= 0 ? '' : 'Minimum stock is required'
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
