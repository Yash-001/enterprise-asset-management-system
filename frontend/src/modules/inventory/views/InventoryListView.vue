<template>
  <div>
    <BasePageHeader title="Inventory" subtitle="Manage spare parts and stock levels">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.INVENTORY_CREATE)"
          label="New Spare Part"
          icon="pi pi-plus"
          @click="router.push({ name: ROUTE_NAMES.SPARE_PART_CREATE })"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="inventoryStore.spareParts"
        :loading="loadingStore.isLoading"
        :total-records="inventoryStore.pagination.totalElements"
        :rows="inventoryStore.pagination.size"
        searchable
        search-placeholder="Search spare parts..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="partNumber" header="Part Number" sortable style="width: 140px" />
        <Column field="partName" header="Part Name" sortable />
        <Column field="category" header="Category" sortable style="width: 140px" />
        <Column field="currentStock" header="Current Stock" sortable style="width: 130px">
          <template #body="{ data }">
            <span :class="{ 'text-red-500 font-bold': data.currentStock <= data.minimumStock }">
              {{ data.currentStock }}
            </span>
          </template>
        </Column>
        <Column field="minimumStock" header="Min Stock" sortable style="width: 120px" />
        <Column field="unitCost" header="Unit Cost" sortable style="width: 120px">
          <template #body="{ data }">
            {{ formatCurrency(data.unitCost) }}
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.INVENTORY_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="router.push({ name: ROUTE_NAMES.SPARE_PART_EDIT, params: { id: data.id } })"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.INVENTORY_DELETE)"
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
import { useInventoryStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS, ROUTE_NAMES } from '@/shared/constants'
import { formatCurrency } from '@/shared/utils'
import type { SparePartListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const router = useRouter()
const inventoryStore = useInventoryStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await inventoryStore.fetchSpareParts({ partName: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  inventoryStore.pagination.page = event.page
  inventoryStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  inventoryStore.pagination.sortBy = event.sortField || 'id'
  inventoryStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  inventoryStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  inventoryStore.pagination.page = 0
  loadData()
}

function handleDelete(part: SparePartListItem): void {
  confirmDelete('Spare Part', async () => {
    try {
      await inventoryStore.deleteSparePart(part.id)
      showSuccess('Spare part deleted', `${part.partNumber} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
