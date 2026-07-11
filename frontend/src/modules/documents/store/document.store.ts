import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { documentService } from '../services'
import type { DocumentListItem } from '../types'
import type { PaginationState } from '@/shared/types'

export const useDocumentStore = defineStore('documents', () => {
  const loadingStore = useLoadingStore()

  const documents = ref<DocumentListItem[]>([])
  const pagination = ref<PaginationState>({
    page: 0,
    size: 20,
    sortBy: 'id',
    sortDirection: 'ASC',
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true
  })

  async function fetchDocuments(params: Record<string, unknown> = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const searchParams = {
        ...params,
        page: params.page ?? pagination.value.page,
        size: params.size ?? pagination.value.size,
        sortBy: params.sortBy ?? pagination.value.sortBy,
        sortDirection: params.sortDirection ?? pagination.value.sortDirection
      }
      const response = await documentService.search(searchParams)
      documents.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function uploadDocument(formData: FormData): Promise<DocumentListItem> {
    loadingStore.startLoading()
    try {
      return await documentService.upload(formData)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function downloadDocument(id: number): Promise<Blob> {
    loadingStore.startLoading()
    try {
      return await documentService.download(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteDocument(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await documentService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    documents.value = []
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    documents,
    pagination,
    fetchDocuments,
    uploadDocument,
    downloadDocument,
    deleteDocument,
    $reset
  }
})
