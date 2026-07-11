import { describe, it, expect, beforeEach } from 'vitest'
import { useLoadingStore } from '../loading.store'
import { createTestPinia } from '@/shared/test-utils'

describe('useLoadingStore', () => {
  beforeEach(() => {
    createTestPinia()
  })

  it('starts with isLoading false', () => {
    const store = useLoadingStore()
    expect(store.isLoading).toBe(false)
    expect(store.loadingCount).toBe(0)
  })

  it('sets isLoading true when startLoading called', () => {
    const store = useLoadingStore()
    store.startLoading()
    expect(store.isLoading).toBe(true)
    expect(store.loadingCount).toBe(1)
  })

  it('tracks multiple concurrent requests', () => {
    const store = useLoadingStore()
    store.startLoading()
    store.startLoading()
    expect(store.loadingCount).toBe(2)
    expect(store.isLoading).toBe(true)

    store.stopLoading()
    expect(store.loadingCount).toBe(1)
    expect(store.isLoading).toBe(true)

    store.stopLoading()
    expect(store.loadingCount).toBe(0)
    expect(store.isLoading).toBe(false)
  })

  it('does not go below zero', () => {
    const store = useLoadingStore()
    store.stopLoading()
    store.stopLoading()
    expect(store.loadingCount).toBe(0)
  })

  it('resets to zero', () => {
    const store = useLoadingStore()
    store.startLoading()
    store.startLoading()
    store.reset()
    expect(store.loadingCount).toBe(0)
    expect(store.isLoading).toBe(false)
  })
})
