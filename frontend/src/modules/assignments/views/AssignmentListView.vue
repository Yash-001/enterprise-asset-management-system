<template>
  <div>
    <BasePageHeader title="Assignments" subtitle="Manage asset assignments to employees" />

    <BaseCard>
      <BaseDataTable
        :data="assignmentStore.assignments"
        :loading="loadingStore.isLoading"
        :total-records="assignmentStore.assignments.length"
        :rows="20"
      >
        <Column field="assetId" header="Asset ID" sortable style="width: 120px" />
        <Column field="employeeId" header="Employee ID" sortable style="width: 140px" />
        <Column field="status" header="Status" sortable style="width: 120px">
          <template #body="{ data }">
            <BaseStatusChip :status="data.status" />
          </template>
        </Column>
        <Column field="assignedDate" header="Assigned Date" sortable style="width: 150px">
          <template #body="{ data }">
            {{ formatDate(data.assignedDate) }}
          </template>
        </Column>
        <Column field="returnedDate" header="Returned Date" sortable style="width: 150px">
          <template #body="{ data }">
            {{ data.returnedDate ? formatDate(data.returnedDate) : '—' }}
          </template>
        </Column>
        <Column header="Actions" style="width: 100px">
          <template #body="{ data }">
            <Button
              v-if="hasPermission(PERMISSIONS.ASSIGNMENT_RETURN) && data.status === 'ACTIVE'"
              icon="pi pi-undo"
              text
              rounded
              size="small"
              tooltip="Return Asset"
              @click="handleReturn(data)"
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
import { useAssignmentStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { usePermission, useAppToast, useAppConfirm } from '@/shared/composables'
import { PERMISSIONS } from '@/shared/constants'
import { formatDate } from '@/shared/utils'
import type { AssignmentListItem } from '../types'

const assignmentStore = useAssignmentStore()
const loadingStore = useLoadingStore()
const { hasPermission } = usePermission()
const { showSuccess, showApiError } = useAppToast()
const { confirmDelete } = useAppConfirm()

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await assignmentStore.fetchAssignments()
}

function handleReturn(assignment: AssignmentListItem): void {
  confirmDelete('Return this asset assignment', async () => {
    try {
      await assignmentStore.returnAssignment(assignment.id)
      showSuccess('Asset returned', 'Assignment has been marked as returned')
      await loadData()
    } catch (err) {
      showApiError(err)
    }
  })
}
</script>
