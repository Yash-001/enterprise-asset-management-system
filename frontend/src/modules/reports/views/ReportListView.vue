<template>
  <div>
    <BasePageHeader title="Reports" subtitle="Generate and view analytical reports" />

    <BaseCard class="mb-4">
      <div class="grid">
        <div class="col-12 md:col-4">
          <label class="block text-sm font-medium mb-1">Date From</label>
          <input
            v-model="reportStore.filters.dateFrom"
            type="date"
            class="w-full p-inputtext p-component"
          />
        </div>
        <div class="col-12 md:col-4">
          <label class="block text-sm font-medium mb-1">Date To</label>
          <input
            v-model="reportStore.filters.dateTo"
            type="date"
            class="w-full p-inputtext p-component"
          />
        </div>
        <div class="col-12 md:col-4">
          <label class="block text-sm font-medium mb-1">Category</label>
          <input
            v-model="reportStore.filters.category"
            type="text"
            placeholder="Enter category"
            class="w-full p-inputtext p-component"
          />
        </div>
        <div class="col-12 md:col-4">
          <label class="block text-sm font-medium mb-1">Department</label>
          <input
            v-model="reportStore.filters.department"
            type="text"
            placeholder="Enter department"
            class="w-full p-inputtext p-component"
          />
        </div>
        <div class="col-12 md:col-4 flex align-items-end">
          <Button
            label="Generate Report"
            icon="pi pi-chart-bar"
            :loading="loadingStore.isLoading"
            @click="handleGenerate"
          />
        </div>
      </div>
    </BaseCard>

    <BaseCard>
      <div v-if="!reportStore.results" class="text-center py-6">
        <i class="pi pi-chart-line text-4xl text-gray-300 mb-3"></i>
        <p class="text-gray-500">No report generated yet</p>
        <p class="text-sm text-gray-400">Configure filters above and click "Generate Report"</p>
      </div>

      <div v-else>
        <pre class="text-sm">{{ JSON.stringify(reportStore.results, null, 2) }}</pre>
      </div>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'
import { useReportStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { useAppToast } from '@/shared/composables'

const reportStore = useReportStore()
const loadingStore = useLoadingStore()
const { showApiError } = useAppToast()

async function handleGenerate(): Promise<void> {
  try {
    await reportStore.generateReport()
  } catch (err) {
    showApiError(err)
  }
}
</script>
