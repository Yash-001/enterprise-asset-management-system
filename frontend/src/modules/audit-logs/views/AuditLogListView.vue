<template>
  <div>
    <BasePageHeader title="Audit Logs" subtitle="View system audit trail and change history" />

    <BaseCard>
      <BaseDataTable
        :data="auditLogStore.auditLogs"
        :loading="loadingStore.isLoading"
        :total-records="auditLogStore.pagination.totalElements"
        :rows="auditLogStore.pagination.size"
        searchable
        search-placeholder="Search audit logs..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="entityName" header="Entity" sortable style="width: 140px" />
        <Column field="action" header="Action" sortable style="width: 120px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.action" />
          </template>
        </Column>
        <Column field="entityId" header="Entity ID" sortable style="width: 100px" />
        <Column field="performedBy" header="Performed By" sortable style="width: 160px" />
        <Column field="performedAt" header="Performed At" sortable style="width: 180px">
          <template #body="{ data }">
            {{ formatDate(data.performedAt) }}
          </template>
        </Column>
        <Column field="ipAddress" header="IP Address" sortable style="width: 140px">
          <template #body="{ data }">
            {{ data.ipAddress || '—' }}
          </template>
        </Column>
      </BaseDataTable>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import Column from 'primevue/column'
import { useAuditLogStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { formatDate } from '@/shared/utils'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const auditLogStore = useAuditLogStore()
const loadingStore = useLoadingStore()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await auditLogStore.fetchAuditLogs({ entityName: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  auditLogStore.pagination.page = event.page
  auditLogStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  auditLogStore.pagination.sortBy = event.sortField || 'id'
  auditLogStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  auditLogStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  auditLogStore.pagination.page = 0
  loadData()
}
</script>
