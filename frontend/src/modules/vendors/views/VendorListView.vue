<template>
  <div>
    <BasePageHeader title="Vendors" subtitle="Manage vendor and supplier information">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.VENDOR_CREATE)"
          label="New Vendor"
          icon="pi pi-plus"
          @click="router.push({ name: ROUTE_NAMES.VENDOR_CREATE })"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="vendorStore.vendors"
        :loading="loadingStore.isLoading"
        :total-records="vendorStore.pagination.totalElements"
        :rows="vendorStore.pagination.size"
        searchable
        search-placeholder="Search vendors..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="vendorCode" header="Code" sortable style="width: 120px" />
        <Column field="vendorName" header="Vendor Name" sortable />
        <Column field="contactPerson" header="Contact Person" sortable style="width: 160px" />
        <Column field="email" header="Email" sortable style="width: 200px" />
        <Column field="phone" header="Phone" sortable style="width: 140px" />
        <Column field="active" header="Status" style="width: 100px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.active ? 'ACTIVE' : 'INACTIVE'" />
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.VENDOR_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="router.push({ name: ROUTE_NAMES.VENDOR_EDIT, params: { id: data.id } })"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.VENDOR_DELETE)"
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
import { useVendorStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS, ROUTE_NAMES } from '@/shared/constants'
import type { VendorListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const router = useRouter()
const vendorStore = useVendorStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await vendorStore.fetchVendors({ vendorName: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  vendorStore.pagination.page = event.page
  vendorStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  vendorStore.pagination.sortBy = event.sortField || 'id'
  vendorStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  vendorStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  vendorStore.pagination.page = 0
  loadData()
}

function handleDelete(vendor: VendorListItem): void {
  confirmDelete('Vendor', async () => {
    try {
      await vendorStore.deleteVendor(vendor.id)
      showSuccess('Vendor deleted', `${vendor.vendorCode} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
