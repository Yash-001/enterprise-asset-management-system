<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <h1 class="register-title">Create Account</h1>
        <p class="register-subtitle">Join the Enterprise Asset Management System</p>
      </div>

      <form class="register-form" @submit.prevent="handleRegister">
        <div class="form-row">
          <div class="field">
            <label for="firstName">First Name</label>
            <InputText id="firstName" v-model="form.firstName" placeholder="John" class="w-full" />
          </div>
          <div class="field">
            <label for="lastName">Last Name</label>
            <InputText id="lastName" v-model="form.lastName" placeholder="Doe" class="w-full" />
          </div>
        </div>

        <div class="field">
          <label for="email">Email</label>
          <InputText id="email" v-model="form.email" type="email" placeholder="john@example.com" class="w-full" />
        </div>

        <div class="field">
          <label for="password">Password</label>
          <Password
            id="password"
            v-model="form.password"
            placeholder="Min 8 characters"
            toggle-mask
            class="w-full"
            input-class="w-full"
          />
        </div>

        <Message v-if="error" severity="error" :closable="false" class="error-msg">
          {{ error }}
        </Message>

        <Message v-if="success" severity="success" :closable="false" class="error-msg">
          {{ success }}
        </Message>

        <Button
          type="submit"
          label="Register"
          icon="pi pi-user-plus"
          :loading="isLoading"
          class="w-full"
        />

        <div class="login-link">
          <span>Already have an account?</span>
          <router-link :to="{ name: ROUTE_NAMES.LOGIN }">Sign In</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import Message from 'primevue/message'
import { useAuthStore } from '../store'
import { ROUTE_NAMES } from '@/shared/constants'
import type { RegisterPayload } from '../types'

const authStore = useAuthStore()

const form = reactive<RegisterPayload>({
  firstName: '',
  lastName: '',
  email: '',
  password: ''
})
const error = ref('')
const success = ref('')
const isLoading = ref(false)

async function handleRegister(): Promise<void> {
  error.value = ''
  success.value = ''
  isLoading.value = true
  try {
    await authStore.register(form)
    success.value = 'Account created successfully! You can now sign in.'
    form.firstName = ''
    form.lastName = ''
    form.email = ''
    form.password = ''
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } } }
    error.value = axiosErr.response?.data?.message || 'Registration failed. Please try again.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--p-surface-ground);
  padding: 1rem;
}

.register-card {
  background: var(--p-surface-card);
  border-radius: 16px;
  padding: 2.5rem;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.register-header {
  text-align: center;
  margin-bottom: 2rem;
}

.register-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--p-text-color);
}

.register-subtitle {
  color: var(--p-text-muted-color);
  font-size: 0.9rem;
  margin-top: 0.25rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.field {
  margin-bottom: 1.25rem;
}

.field label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--p-text-color);
}

.w-full {
  width: 100%;
}

.error-msg {
  margin-bottom: 1rem;
}

.login-link {
  text-align: center;
  margin-top: 1.25rem;
  font-size: 0.85rem;
  color: var(--p-text-muted-color);
}

.login-link a {
  margin-left: 0.25rem;
  color: var(--p-primary-color);
  text-decoration: none;
  font-weight: 600;
}
</style>
