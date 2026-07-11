import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { assignmentService } from '../services'
import type { AssignmentListItem, AssignmentCreatePayload } from '../types'

export const useAssignmentStore = defineStore('assignments', () => {
  const loadingStore = useLoadingStore()

  const assignments = ref<AssignmentListItem[]>([])
  const selectedAssignment = ref<AssignmentListItem | null>(null)

  async function fetchAssignments(): Promise<void> {
    loadingStore.startLoading()
    try {
      const response = await assignmentService.getHistory()
      assignments.value = response.content
    } catch {
      assignments.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createAssignment(payload: AssignmentCreatePayload): Promise<AssignmentListItem> {
    loadingStore.startLoading()
    try {
      return await assignmentService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function returnAssignment(id: number): Promise<AssignmentListItem> {
    loadingStore.startLoading()
    try {
      return await assignmentService.returnAsset(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    assignments.value = []
    selectedAssignment.value = null
  }

  return {
    assignments,
    selectedAssignment,
    fetchAssignments,
    createAssignment,
    returnAssignment,
    $reset
  }
})
