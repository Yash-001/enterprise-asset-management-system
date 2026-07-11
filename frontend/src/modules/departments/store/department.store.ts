import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { departmentService } from '../services'
import type { DepartmentListItem, DepartmentCreatePayload } from '../types'

export const useDepartmentStore = defineStore('departments', () => {
  const loadingStore = useLoadingStore()

  const departments = ref<DepartmentListItem[]>([])
  const selectedDepartment = ref<DepartmentListItem | null>(null)

  async function fetchDepartments(): Promise<void> {
    loadingStore.startLoading()
    try {
      const response = await departmentService.getAll()
      departments.value = response.content
    } catch {
      departments.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createDepartment(payload: DepartmentCreatePayload): Promise<DepartmentListItem> {
    loadingStore.startLoading()
    try {
      return await departmentService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateDepartment(id: number, payload: Partial<DepartmentCreatePayload>): Promise<DepartmentListItem> {
    loadingStore.startLoading()
    try {
      return await departmentService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteDepartment(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await departmentService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    departments.value = []
    selectedDepartment.value = null
  }

  return {
    departments,
    selectedDepartment,
    fetchDepartments,
    createDepartment,
    updateDepartment,
    deleteDepartment,
    $reset
  }
})
