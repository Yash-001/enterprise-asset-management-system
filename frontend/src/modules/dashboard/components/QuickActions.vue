<template>
  <BaseCard title="Quick Actions">
    <div class="quick-actions">
      <Button
        v-if="hasPermission(PERMISSIONS.ASSET_CREATE)"
        label="New Asset"
        icon="pi pi-plus"
        severity="primary"
        outlined
        @click="router.push({ name: ROUTE_NAMES.ASSET_CREATE })"
      />
      <Button
        v-if="hasPermission(PERMISSIONS.WORKORDER_CREATE)"
        label="Create Work Order"
        icon="pi pi-wrench"
        severity="warning"
        outlined
        @click="router.push({ name: ROUTE_NAMES.WORK_ORDER_CREATE })"
      />
      <Button
        v-if="hasPermission(PERMISSIONS.PURCHASE_CREATE)"
        label="New Purchase Order"
        icon="pi pi-shopping-cart"
        severity="success"
        outlined
        @click="router.push({ name: ROUTE_NAMES.PURCHASE_ORDER_CREATE })"
      />
      <Button
        v-if="hasPermission(PERMISSIONS.INVENTORY_CREATE)"
        label="Add Spare Part"
        icon="pi pi-warehouse"
        severity="info"
        outlined
        @click="router.push({ name: ROUTE_NAMES.SPARE_PART_CREATE })"
      />
    </div>
  </BaseCard>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import { usePermission } from '@/shared/composables'
import { PERMISSIONS, ROUTE_NAMES } from '@/shared/constants'

const router = useRouter()
const { hasPermission } = usePermission()
</script>

<style scoped>
.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}
</style>
