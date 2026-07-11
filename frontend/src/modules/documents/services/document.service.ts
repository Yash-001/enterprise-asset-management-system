import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { DocumentListItem } from '../types'

export class DocumentService {
  async search(params: Record<string, unknown> = {}): Promise<PageResponse<DocumentListItem>> {
    return apiClient.getPaged<DocumentListItem>(ENDPOINTS.DOCUMENTS.BASE, params)
  }

  async getById(id: number): Promise<DocumentListItem> {
    return apiClient.get<DocumentListItem>(ENDPOINTS.DOCUMENTS.BY_ID(id))
  }

  async upload(formData: FormData): Promise<DocumentListItem> {
    return apiClient.post<DocumentListItem>(ENDPOINTS.DOCUMENTS.BASE, formData)
  }

  async download(id: number): Promise<Blob> {
    return apiClient.get<Blob>(ENDPOINTS.DOCUMENTS.DOWNLOAD(id))
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.DOCUMENTS.BY_ID(id))
  }
}

export const documentService = new DocumentService()
