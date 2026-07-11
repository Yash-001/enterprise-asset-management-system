<template>
  <div>
    <BasePageHeader title="Departments" subtitle="Manage organizational departments">
      <template #actions>
        <Button
          v-if="hasPermission(PERMISSIONS.DEPARTMENT_CREATE)"
          label="New Department"
          icon="pi pi-plus"
          @click="handleCreate"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <BaseDataTable
        :data="departmentStore.departments"
        :loading="loadingStore.isLoading"
        :total-records="departmentStore.departments.length"
        :rows="20"
      >
        <Column field="departmentCode" header="Code" sortable style="width: 120px" />
        <Column field="departmentName" header="Department Name" sortable />
        <Column field="description" header="Description" sortable />
        <Column field="active" header="Status" style="width: 100px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.active ? 'ACTIVE' : 'INACTIVE'" />
          </template>
        </Column>
        <Column header="Actions" style="width: 120px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.DEPARTMENT_EDIT)"
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              @click="handleEdit(data)"
            />
            <Button
              v-if="hasPermission(PERMISSIONS.DEPARTMENT_EDIT)"
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
import { onMounted } from 'vue'
import Column from 'primevue/column'
import Button from 'primevue/button'
import { useDepartmentStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS } from '@/shared/constants'
import type { DepartmentListItem } from '../types'

const departmentStore = useDepartmentStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await departmentStore.fetchDepartments()
}

function handleCreate(): void {
  // TODO: Open create dialog or navigate to create form
}

function handleEdit(_department: DepartmentListItem): void {
  // TODO: Open edit dialog or navigate to edit form
}

function handleDelete(department: DepartmentListItem): void {
  confirmDelete('Department', async () => {
    try {
      await departmentStore.deleteDepartment(department.id)
      showSuccess('Department deleted', `${department.departmentCode} has been removed`)
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
