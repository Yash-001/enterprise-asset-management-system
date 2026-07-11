<template>
  <div class="base-data-table">
    <!-- Toolbar -->
    <div v-if="$slots.toolbar || searchable" class="base-data-table__toolbar">
      <div class="base-data-table__search" v-if="searchable">
        <span class="p-input-icon-left">
          <i class="pi pi-search" />
          <InputText
            v-model="searchQuery"
            :placeholder="searchPlaceholder"
            class="w-full"
            @input="onSearchDebounced"
          />
        </span>
      </div>
      <div class="base-data-table__toolbar-actions">
        <slot name="toolbar" />
      </div>
    </div>

    <!-- Table -->
    <DataTable
      :value="data"
      :loading="loading"
      :paginator="paginator"
      :rows="rows"
      :totalRecords="totalRecords"
      :lazy="lazy"
      :stripedRows="true"
      :rowHover="true"
      :dataKey="dataKey"
      :sortField="sortField"
      :sortOrder="sortOrder"
      removableSort
      responsiveLayout="scroll"
      @page="onPage"
      @sort="onSort"
    >
      <slot />

      <template #empty>
        <div class="empty-state">
          <i class="pi pi-inbox empty-state__icon"></i>
          <p class="empty-state__title">{{ emptyMessage }}</p>
        </div>
      </template>

      <template #loading>
        <div class="loading-state">
          <i class="pi pi-spin pi-spinner"></i>
          <span>Loading data...</span>
        </div>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import DataTable from 'primevue/datatable'
import InputText from 'primevue/inputtext'
import { APP_CONSTANTS } from '@/shared/constants'
import type { DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

const props = withDefaults(
  defineProps<{
    data: unknown[]
    loading?: boolean
    paginator?: boolean
    rows?: number
    totalRecords?: number
    lazy?: boolean
    dataKey?: string
    sortField?: string
    sortOrder?: 1 | -1 | null
    searchable?: boolean
    searchPlaceholder?: string
    emptyMessage?: string
  }>(),
  {
    loading: false,
    paginator: true,
    rows: 20,
    totalRecords: 0,
    lazy: true,
    dataKey: 'id',
    sortField: undefined,
    sortOrder: null,
    searchable: false,
    searchPlaceholder: 'Search...',
    emptyMessage: 'No records found'
  }
)

const emit = defineEmits<{
  page: [event: DataTablePageEvent]
  sort: [event: DataTableSortEvent]
  search: [query: string]
}>()

const searchQuery = ref('')

const onSearchDebounced = useDebounceFn(() => {
  emit('search', searchQuery.value)
}, APP_CONSTANTS.DEBOUNCE_DELAY)

function onPage(event: DataTablePageEvent): void {
  emit('page', event)
}

function onSort(event: DataTableSortEvent): void {
  emit('sort', event)
}
</script>

<style scoped>
.base-data-table__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.base-data-table__search {
  min-width: 250px;
}

.base-data-table__toolbar-actions {
  display: flex;
  gap: 0.5rem;
}

.empty-state {
  text-align: center;
  padding: 2.5rem;
  color: var(--eams-text-muted);
}

.empty-state__icon {
  font-size: 2.5rem;
  opacity: 0.4;
  margin-bottom: 0.75rem;
  display: block;
}

.empty-state__title {
  font-size: 0.9rem;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 2rem;
  color: var(--eams-text-muted);
}

.w-full {
  width: 100%;
}
</style>
