<template>
  <div>
    <BasePageHeader title="User Management" subtitle="Manage system users and their roles">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.USER_CREATE)"
          label="New User"
          icon="pi pi-user-plus"
          @click="showCreateDialog = true"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="userStore.users"
        :loading="loadingStore.isLoading"
        :total-records="userStore.pagination.totalElements"
        :rows="userStore.pagination.size"
        searchable
        search-placeholder="Search users..."
        @page="handlePage"
        @sort="handleSort"
        @search="handleSearch"
      >
        <Column field="firstName" header="First Name" sortable />
        <Column field="lastName" header="Last Name" sortable />
        <Column field="email" header="Email" sortable />
        <Column field="role" header="Role" sortable style="width: 120px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.role" />
          </template>
        </Column>
        <Column field="active" header="Status" style="width: 100px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.active ? 'ACTIVE' : 'INACTIVE'" />
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.USER_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="openEdit(data)"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.USER_DELETE)"
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

    <!-- Create/Edit Dialog -->
    <UserFormDialog
      v-model:visible="showFormDialog"
      :user="editingUser"
      :mode="formMode"
      @saved="onSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Column from 'primevue/column'
import Button from 'primevue/button'
import { useUserStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS } from '@/shared/constants'
import UserFormDialog from '../components/UserFormDialog.vue'
import type { UserListItem } from '../types'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const userStore = useUserStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()

const showFormDialog = ref(false)
const showCreateDialog = ref(false)
const editingUser = ref<UserListItem | null>(null)
const formMode = ref<'create' | 'edit'>('create')
const searchQuery = ref('')

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await userStore.fetchUsers({ email: searchQuery.value || undefined })
}

function handlePage(event: DataTablePageEvent): void {
  userStore.pagination.page = event.page
  userStore.pagination.size = event.rows
  loadData()
}

function handleSort(event: DataTableSortEvent): void {
  userStore.pagination.sortBy = event.sortField || 'id'
  userStore.pagination.sortDirection = event.sortOrder === -1 ? 'DESC' : 'ASC'
  userStore.pagination.page = 0
  loadData()
}

function handleSearch(query: string): void {
  searchQuery.value = query
  userStore.pagination.page = 0
  loadData()
}

function openEdit(user: UserListItem): void {
  editingUser.value = user
  formMode.value = 'edit'
  showFormDialog.value = true
}

// Watch showCreateDialog to open form in create mode
import { watch } from 'vue'
watch(showCreateDialog, (val) => {
  if (val) {
    editingUser.value = null
    formMode.value = 'create'
    showFormDialog.value = true
    showCreateDialog.value = false
  }
})

function handleDelete(user: UserListItem): void {
  confirmDelete('User', async () => {
    try {
      await userStore.deleteUser(user.id)
      showSuccess('User deleted', `${user.firstName} ${user.lastName} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}

function onSaved(): void {
  showFormDialog.value = false
  showSuccess('User saved successfully')
  loadData()
}
</script>
