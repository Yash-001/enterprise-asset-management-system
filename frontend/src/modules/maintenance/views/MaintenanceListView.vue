<template>
  <div>
    <BasePageHeader title="Maintenance Plans" subtitle="Manage preventive and corrective maintenance schedules">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.MAINTENANCE_CREATE)"
          label="New Plan"
          icon="pi pi-plus"
          @click="router.push({ name: ROUTE_NAMES.MAINTENANCE_CREATE })"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="maintenanceStore.plans"
        :loading="loadingStore.isLoading"
        :total-records="maintenanceStore.pagination.totalElements"
        :rows="maintenanceStore.pagination.size"
        searchable
        search-placeholder="Search maintenance plans..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="planCode" header="Code" sortable style="width: 120px" />
        <Column field="planName" header="Plan Name" sortable />
        <Column field="maintenanceType" header="Type" sortable style="width: 140px" />
        <Column field="nextMaintenanceDate" header="Next Date" sortable style="width: 140px">
          <template #body="{ data }">
            {{ data.nextMaintenanceDate ? formatDate(data.nextMaintenanceDate) : '—' }}
          </template>
        </Column>
        <Column field="status" header="Status" sortable style="width: 140px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.status" />
          </template>
        </Column>
        <Column field="priority" header="Priority" sortable style="width: 120px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.priority" />
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.MAINTENANCE_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="router.push({ name: ROUTE_NAMES.MAINTENANCE_EDIT, params: { id: data.id } })"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.MAINTENANCE_DELETE)"
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
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Column from 'primevue/column'
import Button from 'primevue/button'
import { useMaintenanceStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS, ROUTE_NAMES } from '@/shared/constants'
import { formatDate } from '@/shared/utils'
import type { MaintenancePlanListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const maintenanceStore = useMaintenanceStore()
const loadingStore = useLoadingStore()
const router = useRouter()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await maintenanceStore.fetchPlans({ planName: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  maintenanceStore.pagination.page = event.page
  maintenanceStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  maintenanceStore.pagination.sortBy = event.sortField || 'id'
  maintenanceStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  maintenanceStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  maintenanceStore.pagination.page = 0
  loadData()
}

function handleDelete(plan: MaintenancePlanListItem): void {
  confirmDelete('Maintenance Plan', async () => {
    try {
      await maintenanceStore.deletePlan(plan.id)
      showSuccess('Plan deleted', `${plan.planCode} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
