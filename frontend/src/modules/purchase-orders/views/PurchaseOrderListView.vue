<template>
  <div>
    <BasePageHeader title="Purchase Orders" subtitle="Manage purchase orders and procurement">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.PURCHASE_CREATE)"
          label="New Purchase Order"
          icon="pi pi-plus"
          @click="router.push({ name: ROUTE_NAMES.PURCHASE_ORDER_CREATE })"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="purchaseOrderStore.orders"
        :loading="loadingStore.isLoading"
        :total-records="purchaseOrderStore.pagination.totalElements"
        :rows="purchaseOrderStore.pagination.size"
        searchable
        search-placeholder="Search purchase orders..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="poNumber" header="PO Number" sortable style="width: 140px" />
        <Column field="totalAmount" header="Total Amount" sortable style="width: 140px">
          <template #body="{ data }">
            {{ formatCurrency(data.totalAmount) }}
          </template>
        </Column>
        <Column field="status" header="Status" sortable style="width: 140px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.status" />
          </template>
        </Column>
        <Column field="expectedDeliveryDate" header="Expected Delivery" sortable style="width: 160px">
          <template #body="{ data }">
            {{ data.expectedDeliveryDate ? formatDate(data.expectedDeliveryDate) : '—' }}
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.PURCHASE_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="router.push({ name: ROUTE_NAMES.PURCHASE_ORDER_EDIT, params: { id: data.id } })"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.PURCHASE_DELETE)"
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
import { usePurchaseOrderStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS, ROUTE_NAMES } from '@/shared/constants'
import { formatCurrency, formatDate } from '@/shared/utils'
import type { PurchaseOrderListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const router = useRouter()
const purchaseOrderStore = usePurchaseOrderStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await purchaseOrderStore.fetchOrders({ poNumber: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  purchaseOrderStore.pagination.page = event.page
  purchaseOrderStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  purchaseOrderStore.pagination.sortBy = event.sortField || 'id'
  purchaseOrderStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  purchaseOrderStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  purchaseOrderStore.pagination.page = 0
  loadData()
}

function handleDelete(order: PurchaseOrderListItem): void {
  confirmDelete('Purchase Order', async () => {
    try {
      await purchaseOrderStore.deleteOrder(order.id)
      showSuccess('Purchase order deleted', `${order.poNumber} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
