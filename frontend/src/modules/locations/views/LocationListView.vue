<template>
  <div>
    <BasePageHeader title="Locations" subtitle="Manage facility and storage locations">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.LOCATION_CREATE)"
          label="New Location"
          icon="pi pi-plus"
          @click="handleCreate"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="locationStore.locations"
        :loading="loadingStore.isLoading"
        :total-records="locationStore.locations.length"
        :rows="20"
      >
        <Column field="locationCode" header="Code" sortable style="width: 120px" />
        <Column field="locationName" header="Location Name" sortable />
        <Column field="city" header="City" sortable style="width: 140px" />
        <Column field="state" header="State" sortable style="width: 120px" />
        <Column field="country" header="Country" sortable style="width: 120px" />
        <Column field="active" header="Status" style="width: 100px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.active ? 'ACTIVE' : 'INACTIVE'" />
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.LOCATION_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="handleEdit(data)"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.LOCATION_EDIT)"
              icon="pi pi-trash"
              text
              rounded
              size="small"
              severity="danger"
              @click="handleDelete(data)"
            />
          </template>
        </Column>
      </BaseDataTable>
    </BaseCard>

    <!-- Create Location Dialog -->
    <Dialog
      v-model:visible="showCreateDialog"
      header="Create Location"
      :modal="true"
      :style="{ width: '500px' }"
    >
      <div class="field">
        <label for="locationCode">Location Code</label>
        <InputText
          id="locationCode"
          v-model="createForm.locationCode"
          class="w-full"
          placeholder="e.g. LOC-001"
        />
      </div>
      <div class="field">
        <label for="locationName">Location Name</label>
        <InputText
          id="locationName"
          v-model="createForm.locationName"
          class="w-full"
          placeholder="e.g. Main Warehouse"
        />
      </div>
      <div class="field">
        <label for="address">Address</label>
        <InputText
          id="address"
          v-model="createForm.address"
          class="w-full"
          placeholder="Street address"
        />
      </div>
      <div class="field">
        <label for="city">City</label>
        <InputText
          id="city"
          v-model="createForm.city"
          class="w-full"
          placeholder="City"
        />
      </div>
      <div class="field">
        <label for="state">State</label>
        <InputText
          id="state"
          v-model="createForm.state"
          class="w-full"
          placeholder="State / Province"
        />
      </div>
      <div class="field">
        <label for="country">Country</label>
        <InputText
          id="country"
          v-model="createForm.country"
          class="w-full"
          placeholder="Country"
        />
      </div>
      <template #footer>
        <Button label="Cancel" text @click="showCreateDialog = false" />
        <Button label="Create" icon="pi pi-check" @click="submitCreate" />
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import { useLocationStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS } from '@/shared/constants'
import type { LocationListItem } from '../types'

const locationStore = useLocationStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()

const showCreateDialog = ref(false)
const createForm = reactive({
  locationCode: '',
  locationName: '',
  address: '',
  city: '',
  state: '',
  country: '',
})

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await locationStore.fetchLocations()
}

function handleCreate(): void {
  createForm.locationCode = ''
  createForm.locationName = ''
  createForm.address = ''
  createForm.city = ''
  createForm.state = ''
  createForm.country = ''
  showCreateDialog.value = true
}

async function submitCreate(): Promise<void> {
  try {
    await locationStore.createLocation({
      locationCode: createForm.locationCode,
      locationName: createForm.locationName,
      address: createForm.address || undefined,
      city: createForm.city || undefined,
      state: createForm.state || undefined,
      country: createForm.country || undefined,
    })
    showSuccess('Location created', `${createForm.locationCode} has been created`)
    showCreateDialog.value = false
    await loadData()
  } catch (err) {
    showApiError(err)
  }
}

function handleEdit(_location: LocationListItem): void {
  // TODO: Open edit dialog or navigate to edit form
}

function handleDelete(location: LocationListItem): void {
  confirmDelete('Location', async () => {
    try {
      await locationStore.deleteLocation(location.id)
      showSuccess('Location deleted', `${location.locationCode} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
