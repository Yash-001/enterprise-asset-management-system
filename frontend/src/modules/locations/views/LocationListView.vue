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
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import Column from 'primevue/column'
import Button from 'primevue/button'
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

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await locationStore.fetchLocations()
}

function handleCreate(): void {
  // TODO: Open create dialog or navigate to create form
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
