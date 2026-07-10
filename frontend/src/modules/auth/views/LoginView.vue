<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">EAMS</h1>
        <p class="login-subtitle">Enterprise Asset Management System</p>
      </div>

      <form class="login-form" @submit.prevent="handleLogin">
        <div class="field">
          <label for="email">Email</label>
          <InputText
            id="email"
            v-model="form.email"
            type="email"
            placeholder="admin@eams.com"
            class="w-full"
            :invalid="!!error"
          />
        </div>

        <div class="field">
          <label for="password">Password</label>
          <Password
            id="password"
            v-model="form.password"
            placeholder="Enter password"
            :feedback="false"
            toggle-mask
            class="w-full"
            input-class="w-full"
            :invalid="!!error"
          />
        </div>

        <Message v-if="error" severity="error" :closable="false" class="error-msg">
          {{ error }}
        </Message>

        <Button
          type="submit"
          label="Sign In"
          icon="pi pi-sign-in"
          :loading="isLoading"
          class="w-full"
        />

        <div class="register-link">
          <span>Don't have an account?</span>
          <router-link :to="{ name: ROUTE_NAMES.REGISTER }">Register</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import Message from 'primevue/message'
import { useAuthStore } from '../store'
import { useLoadingStore } from '@/shared/stores'
import { ROUTE_NAMES } from '@/shared/constants'
import type { LoginCredentials } from '../types'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loadingStore = useLoadingStore()

const form = reactive<LoginCredentials>({ email: '', password: '' })
const error = ref('')
const isLoading = ref(false)

async function handleLogin(): Promise<void> {
  error.value = ''
  isLoading.value = true
  try {
    await authStore.login(form)
    const redirect = (route.query.redirect as string) || '/'
    await router.push(redirect)
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } } }
    error.value = axiosErr.response?.data?.message || 'Invalid email or password'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--p-surface-ground);
  padding: 1rem;
}

.login-card {
  background: var(--p-surface-card);
  border-radius: 16px;
  padding: 2.5rem;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-title {
  font-size: 2rem;
  font-weight: 800;
  color: var(--p-primary-color);
}

.login-subtitle {
  color: var(--p-text-muted-color);
  font-size: 0.9rem;
  margin-top: 0.25rem;
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

.register-link {
  text-align: center;
  margin-top: 1.25rem;
  font-size: 0.85rem;
  color: var(--p-text-muted-color);
}

.register-link a {
  margin-left: 0.25rem;
  color: var(--p-primary-color);
  text-decoration: none;
  font-weight: 600;
}
</style>
