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

    <!-- Create Department Dialog -->
    <Dialog
      v-model:visible="showCreateDialog"
      header="Create Department"
      :modal="true"
      :style="{ width: '450px' }"
    >
      <div class="field">
        <label for="departmentCode">Department Code</label>
        <InputText
          id="departmentCode"
          v-model="createForm.departmentCode"
          class="w-full"
          placeholder="e.g. DEPT-001"
        />
      </div>
      <div class="field">
        <label for="departmentName">Department Name</label>
        <InputText
          id="departmentName"
          v-model="createForm.departmentName"
          class="w-full"
          placeholder="e.g. Engineering"
        />
      </div>
      <div class="field">
        <label for="description">Description</label>
        <Textarea
          id="description"
          v-model="createForm.description"
          class="w-full"
          rows="3"
          placeholder="Optional description"
        />
      </div>
      <template #footer>
        <Button label="Cancel" text @click="showCreateDialog = false" />
        <Button label="Create" icon="pi pi-check" @click="submitCreate" />
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
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

const showCreateDialog = ref(false)
const createForm = reactive({
  departmentCode: '',
  departmentName: '',
  description: '',
})

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await departmentStore.fetchDepartments()
}

function handleCreate(): void {
  createForm.departmentCode = ''
  createForm.departmentName = ''
  createForm.description = ''
  showCreateDialog.value = true
}

async function submitCreate(): Promise<void> {
  try {
    await departmentStore.createDepartment({
      departmentCode: createForm.departmentCode,
      departmentName: createForm.departmentName,
      description: createForm.description || undefined,
    })
    showSuccess('Department created', `${createForm.departmentCode} has been created`)
    showCreateDialog.value = false
    await loadData()
  } catch (err) {
    showApiError(err)
  }
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
