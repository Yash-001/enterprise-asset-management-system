<template>
  <div>
    <BasePageHeader :title="isEdit ? 'Edit Vendor' : 'Add Vendor'" :subtitle="isEdit ? `Editing ${editVendorCode}` : 'Register a new vendor'">
      <template #actions>
        <Button label="Back" icon="pi pi-arrow-left" severity="secondary" text @click="router.back()" />
      </template>
    </BasePageHeader>

    <BaseCard>
      <form class="form-grid" @submit.prevent="handleSubmit">
        <div class="field">
          <label for="vendorCode">Vendor Code *</label>
          <InputText id="vendorCode" v-model="form.vendorCode" class="w-full" :disabled="isEdit" placeholder="VND-001" />
          <small v-if="errors.vendorCode" class="p-error">{{ errors.vendorCode }}</small>
        </div>

        <div class="field">
          <label for="vendorName">Vendor Name *</label>
          <InputText id="vendorName" v-model="form.vendorName" class="w-full" placeholder="Acme Supplies Inc." />
          <small v-if="errors.vendorName" class="p-error">{{ errors.vendorName }}</small>
        </div>

        <div class="field">
          <label for="contactPerson">Contact Person</label>
          <InputText id="contactPerson" v-model="form.contactPerson" class="w-full" placeholder="Jane Smith" />
        </div>

        <div class="field">
          <label for="email">Email</label>
          <InputText id="email" v-model="form.email" type="email" class="w-full" placeholder="contact@acme.com" />
        </div>

        <div class="field">
          <label for="phone">Phone</label>
          <InputText id="phone" v-model="form.phone" class="w-full" placeholder="+1 555-0100" />
        </div>

        <div class="field col-span-2">
          <label for="address">Address</label>
          <Textarea id="address" v-model="form.address" rows="2" class="w-full" />
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
import Textarea from 'primevue/textarea'
import Button from 'primevue/button'
import { useVendorStore } from '../store'
import { vendorService } from '../services'
import { useAppToast } from '@/shared/composables'
import { ROUTE_NAMES } from '@/shared/constants'

const router = useRouter()
const route = useRoute()
const vendorStore = useVendorStore()
const { showSuccess, showApiError } = useAppToast()

const isEdit = computed(() => !!route.params.id)
const editVendorCode = ref('')
const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  vendorCode: '',
  vendorName: '',
  contactPerson: '',
  email: '',
  phone: '',
  address: ''
})

onMounted(async () => {
  if (isEdit.value) {
    try {
      const vendor = await vendorService.getById(Number(route.params.id))
      if (vendor) {
        editVendorCode.value = vendor.vendorCode
        form.vendorCode = vendor.vendorCode
        form.vendorName = vendor.vendorName
        form.contactPerson = vendor.contactPerson || ''
        form.email = vendor.email || ''
        form.phone = vendor.phone || ''
        form.address = vendor.address || ''
      }
    } catch { /* stay on empty form */ }
  }
})

async function handleSubmit(): Promise<void> {
  if (!validate()) return
  isSubmitting.value = true
  try {
    const payload = {
      vendorCode: form.vendorCode.trim().toUpperCase(),
      vendorName: form.vendorName.trim(),
      contactPerson: form.contactPerson || undefined,
      email: form.email || undefined,
      phone: form.phone || undefined,
      address: form.address || undefined,
      active: true
    }

    if (isEdit.value) {
      await vendorStore.updateVendor(Number(route.params.id), payload)
      showSuccess('Vendor updated')
    } else {
      await vendorStore.createVendor(payload as any)
      showSuccess('Vendor created')
    }
    router.push({ name: ROUTE_NAMES.VENDORS })
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.vendorCode = form.vendorCode.trim() ? '' : 'Vendor code is required'
  errors.vendorName = form.vendorName.trim() ? '' : 'Vendor name is required'
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
