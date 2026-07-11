import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { userService } from '../services'
import type { UserListItem, UserSearchFilters, UserCreatePayload, UserUpdatePayload } from '../types'
import type { PaginationState } from '@/shared/types'

export const useUserStore = defineStore('users', () => {
  const loadingStore = useLoadingStore()

  const users = ref<UserListItem[]>([])
  const selectedUser = ref<UserListItem | null>(null)
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

  async function fetchUsers(filters: UserSearchFilters = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const params = {
        ...filters,
        page: filters.page ?? pagination.value.page,
        size: filters.size ?? pagination.value.size,
        sortBy: filters.sortBy ?? pagination.value.sortBy,
        sortDirection: filters.sortDirection ?? pagination.value.sortDirection
      }
      const response = await userService.search(params)
      users.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function fetchUserById(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      selectedUser.value = await userService.getById(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createUser(payload: UserCreatePayload): Promise<UserListItem> {
    loadingStore.startLoading()
    try {
      return await userService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateUser(id: number, payload: UserUpdatePayload): Promise<UserListItem> {
    loadingStore.startLoading()
    try {
      return await userService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteUser(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await userService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    users.value = []
    selectedUser.value = null
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    users,
    selectedUser,
    pagination,
    fetchUsers,
    fetchUserById,
    createUser,
    updateUser,
    deleteUser,
    $reset
  }
})
