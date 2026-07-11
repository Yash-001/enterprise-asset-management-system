import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createMemoryHistory, type RouteRecordRaw } from 'vue-router'
import { vi } from 'vitest'

/**
 * Create a fresh Pinia instance for testing.
 */
export function createTestPinia() {
  const pinia = createPinia()
  setActivePinia(pinia)
  return pinia
}

/**
 * Create a mock router for testing.
 */
export function createTestRouter(routes: RouteRecordRaw[] = []) {
  return createRouter({
    history: createMemoryHistory(),
    routes: routes.length > 0 ? routes : [
      { path: '/', name: 'dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/login', name: 'login', component: { template: '<div>Login</div>' } }
    ]
  })
}

/**
 * Create a mock API response.
 */
export function createPageResponse<T>(content: T[], totalElements?: number) {
  return {
    content,
    totalElements: totalElements ?? content.length,
    totalPages: Math.ceil((totalElements ?? content.length) / 20),
    number: 0,
    size: 20,
    first: true,
    last: true,
    empty: content.length === 0
  }
}

/**
 * Create a mock Axios error.
 */
export function createAxiosError(status: number, message: string) {
  return {
    response: {
      status,
      data: { status, message, error: 'Error', path: '/test', timestamp: new Date().toISOString() }
    },
    message: `Request failed with status code ${status}`
  }
}

/**
 * Mock the apiClient module.
 */
export function mockApiClient() {
  return {
    get: vi.fn(),
    getPaged: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
    upload: vi.fn(),
    download: vi.fn()
  }
}
