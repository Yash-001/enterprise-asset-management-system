<template>
  <div>
    <BasePageHeader title="Assets" subtitle="Manage your organization's physical assets">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.ASSET_CREATE)"
          label="New Asset"
          icon="pi pi-plus"
          @click="router.push({ name: ROUTE_NAMES.ASSET_CREATE })"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="assetStore.assets"
        :loading="loadingStore.isLoading"
        :total-records="assetStore.pagination.totalElements"
        :rows="assetStore.pagination.size"
        searchable
        search-placeholder="Search assets..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="assetCode" header="Code" sortable style="width: 120px" />
        <Column field="assetName" header="Name" sortable />
        <Column field="manufacturer" header="Manufacturer" sortable style="width: 150px" />
        <Column field="status" header="Status" sortable style="width: 140px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.status" />
          </template>
        </Column>
        <Column field="purchasePrice" header="Price" sortable style="width: 120px">
          <template #body="{ data }">
            {{ formatCurrency(data.purchasePrice) }}
          </template>
        </Column>
        <Column field="active" header="Active" style="width: 80px">
          <template #body="{ data }">
            <span :aria-label="data.active ? 'Active' : 'Inactive'">
              <i :class="['pi', data.active ? 'pi-check-circle text-success' : 'pi-times-circle text-danger']"></i>
              <span class="hide-mobile" style="margin-left: 0.25rem; font-size: 0.75rem;">{{ data.active ? 'Yes' : 'No' }}</span>
            </span>
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.ASSET_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="router.push({ name: ROUTE_NAMES.ASSET_EDIT, params: { id: data.id } })"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.ASSET_DELETE)"
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
import { useAssetStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS, ROUTE_NAMES } from '@/shared/constants'
import { formatCurrency } from '@/shared/utils'
import type { AssetListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const router = useRouter()
const assetStore = useAssetStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await assetStore.fetchAssets({ assetName: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  assetStore.pagination.page = event.page
  assetStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  assetStore.pagination.sortBy = event.sortField || 'id'
  assetStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  assetStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  assetStore.pagination.page = 0
  loadData()
}

function handleDelete(asset: AssetListItem): void {
  confirmDelete('Asset', async () => {
    try {
      await assetStore.deleteAsset(asset.id)
      showSuccess('Asset deleted', `${asset.assetCode} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
