import { test, expect } from '@playwright/test'

test.describe('Authentication', () => {
  test('redirects unauthenticated users to login', async ({ page }) => {
    await page.goto('/dashboard')
    await expect(page).toHaveURL(/\/login/)
  })

  test('login page renders correctly', async ({ page }) => {
    await page.goto('/login')
    await expect(page.locator('h1')).toContainText('EAMS')
    await expect(page.locator('input[type="email"]')).toBeVisible()
    await expect(page.locator('button[type="submit"]')).toBeVisible()
  })

  test('shows error with invalid credentials', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[type="email"]', 'wrong@example.com')
    await page.locator('input[type="password"]').fill('wrongpass')
    await page.click('button[type="submit"]')

    // Should show error message (API returns 401)
    await expect(page.locator('.p-message-error, [class*="error"]')).toBeVisible({ timeout: 5000 })
  })

  test('register page is accessible', async ({ page }) => {
    await page.goto('/register')
    await expect(page.locator('h1')).toContainText('Create Account')
  })

  test('navigates between login and register', async ({ page }) => {
    await page.goto('/login')
    await page.click('a[href="/register"]')
    await expect(page).toHaveURL(/\/register/)

    await page.click('a[href="/login"]')
    await expect(page).toHaveURL(/\/login/)
  })
})
