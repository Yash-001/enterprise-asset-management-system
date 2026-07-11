<template>
  <div>
    <BasePageHeader title="Work Orders" subtitle="Manage work orders and service requests">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.WORKORDER_CREATE)"
          label="New Work Order"
          icon="pi pi-plus"
          @click="router.push({ name: ROUTE_NAMES.WORK_ORDER_CREATE })"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="workOrderStore.workOrders"
        :loading="loadingStore.isLoading"
        :total-records="workOrderStore.pagination.totalElements"
        :rows="workOrderStore.pagination.size"
        searchable
        search-placeholder="Search work orders..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="workOrderNumber" header="WO Number" sortable style="width: 140px" />
        <Column field="title" header="Title" sortable />
        <Column field="assignedTechnician" header="Technician" sortable style="width: 160px" />
        <Column field="priority" header="Priority" sortable style="width: 120px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.priority" />
          </template>
        </Column>
        <Column field="status" header="Status" sortable style="width: 140px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.status" />
          </template>
        </Column>
        <Column field="scheduledDate" header="Scheduled" sortable style="width: 140px">
          <template #body="{ data }">
            {{ data.scheduledDate ? formatDate(data.scheduledDate) : '—' }}
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.WORKORDER_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="router.push({ name: ROUTE_NAMES.WORK_ORDER_EDIT, params: { id: data.id } })"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.WORKORDER_DELETE)"
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
import { useWorkOrderStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS, ROUTE_NAMES } from '@/shared/constants'
import { formatDate } from '@/shared/utils'
import type { WorkOrderListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const router = useRouter()
const workOrderStore = useWorkOrderStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await workOrderStore.fetchWorkOrders({ title: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  workOrderStore.pagination.page = event.page
  workOrderStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  workOrderStore.pagination.sortBy = event.sortField || 'id'
  workOrderStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  workOrderStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  workOrderStore.pagination.page = 0
  loadData()
}

function handleDelete(workOrder: WorkOrderListItem): void {
  confirmDelete('Work Order', async () => {
    try {
      await workOrderStore.deleteWorkOrder(workOrder.id)
      showSuccess('Work order deleted', `${workOrder.workOrderNumber} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
