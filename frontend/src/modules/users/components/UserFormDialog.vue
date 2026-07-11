<template>
  <Dialog
    v-model:visible="dialogVisible"
    :header="mode === 'create' ? 'Create User' : 'Edit User'"
    :modal="true"
    :style="{ width: '500px' }"
  >
    <form @submit.prevent="handleSubmit">
      <div class="form-grid">
        <div class="field">
          <label for="firstName">First Name *</label>
          <InputText id="firstName" v-model="form.firstName" class="w-full" />
          <small v-if="errors.firstName" class="p-error">{{ errors.firstName }}</small>
        </div>

        <div class="field">
          <label for="lastName">Last Name *</label>
          <InputText id="lastName" v-model="form.lastName" class="w-full" />
          <small v-if="errors.lastName" class="p-error">{{ errors.lastName }}</small>
        </div>

        <div class="field col-span-2">
          <label for="email">Email *</label>
          <InputText id="email" v-model="form.email" type="email" class="w-full" :disabled="mode === 'edit'" />
          <small v-if="errors.email" class="p-error">{{ errors.email }}</small>
        </div>

        <div v-if="mode === 'create'" class="field col-span-2">
          <label for="password">Password *</label>
          <Password id="password" v-model="form.password" toggle-mask class="w-full" input-class="w-full" :feedback="false" />
          <small v-if="errors.password" class="p-error">{{ errors.password }}</small>
        </div>

        <div class="field">
          <label for="role">Role *</label>
          <Select id="role" v-model="form.role" :options="roleOptions" option-label="label" option-value="value" class="w-full" />
        </div>

        <div v-if="mode === 'edit'" class="field">
          <label for="active">Status</label>
          <Select id="active" v-model="form.active" :options="statusOptions" option-label="label" option-value="value" class="w-full" />
        </div>
      </div>
    </form>

    <template #footer>
      <Button label="Cancel" severity="secondary" text @click="dialogVisible = false" />
      <Button :label="mode === 'create' ? 'Create' : 'Save'" icon="pi pi-check" :loading="isSubmitting" @click="handleSubmit" />
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Select from 'primevue/select'
import Button from 'primevue/button'
import { useUserStore } from '../store'
import { useAppToast } from '@/shared/composables'
import type { UserListItem } from '../types'

const props = defineProps<{
  user: UserListItem | null
  mode: 'create' | 'edit'
}>()

const emit = defineEmits<{ saved: [] }>()
const dialogVisible = defineModel<boolean>('visible', { default: false })

const userStore = useUserStore()
const { showApiError } = useAppToast()
const isSubmitting = ref(false)
const errors = reactive<Record<string, string>>({})

const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  role: 'USER' as string,
  active: true
})

const roleOptions = [
  { label: 'Admin', value: 'ADMIN' },
  { label: 'Manager', value: 'MANAGER' },
  { label: 'User', value: 'USER' }
]

const statusOptions = [
  { label: 'Active', value: true },
  { label: 'Inactive', value: false }
]

watch(
  () => props.user,
  (newUser) => {
    if (newUser && props.mode === 'edit') {
      form.firstName = newUser.firstName
      form.lastName = newUser.lastName
      form.email = newUser.email
      form.role = newUser.role
      form.active = newUser.active
      form.password = ''
    } else {
      form.firstName = ''
      form.lastName = ''
      form.email = ''
      form.password = ''
      form.role = 'USER'
      form.active = true
    }
    Object.keys(errors).forEach((k) => { errors[k] = '' })
  },
  { immediate: true }
)

async function handleSubmit(): Promise<void> {
  if (!validate()) return

  isSubmitting.value = true
  try {
    if (props.mode === 'create') {
      await userStore.createUser({
        firstName: form.firstName,
        lastName: form.lastName,
        email: form.email,
        password: form.password,
        role: form.role as 'ADMIN' | 'MANAGER' | 'USER'
      })
    } else if (props.user) {
      await userStore.updateUser(props.user.id, {
        firstName: form.firstName,
        lastName: form.lastName,
        role: form.role as 'ADMIN' | 'MANAGER' | 'USER',
        active: form.active
      })
    }
    emit('saved')
  } catch (err) {
    showApiError(err)
  } finally {
    isSubmitting.value = false
  }
}

function validate(): boolean {
  let valid = true
  errors.firstName = form.firstName.trim() ? '' : 'First name is required'
  errors.lastName = form.lastName.trim() ? '' : 'Last name is required'
  errors.email = form.email.trim() ? '' : 'Email is required'
  if (props.mode === 'create') {
    errors.password = form.password.length >= 8 ? '' : 'Password must be at least 8 characters'
  }
  Object.values(errors).forEach((e) => { if (e) valid = false })
  return valid
}
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.col-span-2 {
  grid-column: span 2;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.field label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--eams-text);
}

.w-full {
  width: 100%;
}

.p-error {
  color: var(--eams-danger);
  font-size: 0.75rem;
}
</style>
