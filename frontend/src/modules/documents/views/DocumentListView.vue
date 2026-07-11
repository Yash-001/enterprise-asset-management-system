<template>
  <div>
    <BasePageHeader title="Documents" subtitle="Manage uploaded documents and attachments">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.DOCUMENT_UPLOAD)"
          label="Upload Document"
          icon="pi pi-upload"
          @click="handleUpload"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="documentStore.documents"
        :loading="loadingStore.isLoading"
        :total-records="documentStore.pagination.totalElements"
        :rows="documentStore.pagination.size"
        searchable
        search-placeholder="Search documents..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="originalFileName" header="File Name" sortable />
        <Column field="contentType" header="Type" sortable style="width: 140px" />
        <Column field="fileSize" header="Size" sortable style="width: 120px">
          <template #body="{ data }">
            {{ formatFileSize(data.fileSize) }}
          </template>
        </Column>
        <Column field="referenceType" header="Reference" sortable style="width: 140px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.referenceType" />
          </template>
        </Column>
        <Column field="uploadedBy" header="Uploaded By" sortable style="width: 150px" />
        <Column field="uploadedAt" header="Uploaded At" sortable style="width: 160px">
          <template #body="{ data }">
            {{ formatDate(data.uploadedAt) }}
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              icon="pi pi-download"
              text
              rounded
              size="small"
              @click="handleDownload(data)"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.DOCUMENT_DELETE)"
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
import Column from 'primevue/column'
import Button from 'primevue/button'
import { useDocumentStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS } from '@/shared/constants'
import { formatDate, formatFileSize } from '@/shared/utils'
import type { DocumentListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const documentStore = useDocumentStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await documentStore.fetchDocuments({ originalFileName: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  documentStore.pagination.page = event.page
  documentStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  documentStore.pagination.sortBy = event.sortField || 'id'
  documentStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  documentStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  documentStore.pagination.page = 0
  loadData()
}

function handleUpload(): void {
  // TODO: Open upload dialog
}

async function handleDownload(document: DocumentListItem): Promise<void> {
  try {
    const blob = await documentStore.downloadDocument(document.id)
    const url = window.URL.createObjectURL(blob)
    const link = window.document.createElement('a')
    link.href = url
    link.download = document.originalFileName
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (err) {
    showApiError(err)
  }
}

function handleDelete(document: DocumentListItem): void {
  confirmDelete('Document', async () => {
    try {
      await documentStore.deleteDocument(document.id)
      showSuccess('Document deleted', `${document.originalFileName} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
