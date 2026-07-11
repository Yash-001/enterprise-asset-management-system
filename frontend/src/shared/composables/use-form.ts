import { ref, reactive, computed } from 'vue'
import type { FormValidationState } from '@/shared/types'
import { validate as runValidation } from '@/shared/utils'

type ValidationRules = Record<string, Array<(value: unknown) => string | true>>

/**
 * Composable for form state management with validation.
 *
 * Usage:
 *   const { form, errors, isValid, isDirty, validateField, validateAll, resetForm } = useForm(initialData, rules)
 */
export function useForm<T extends Record<string, unknown>>(
  initialValues: T,
  rules: ValidationRules = {}
) {
  const form = reactive<T>({ ...initialValues })
  const errors = reactive<Record<string, string>>({})
  const isDirty = ref(false)
  const isSubmitting = ref(false)

  const isValid = computed(() => Object.values(errors).every((e) => !e))

  const state = computed<FormValidationState>(() => ({
    isValid: isValid.value,
    isDirty: isDirty.value,
    isSubmitting: isSubmitting.value,
    errors: { ...errors }
  }))

  function validateField(field: string): boolean {
    const fieldRules = rules[field]
    if (!fieldRules) {
      errors[field] = ''
      return true
    }
    const value = (form as Record<string, unknown>)[field]
    const error = runValidation(value, fieldRules)
    errors[field] = error || ''
    return !error
  }

  function validateAll(): boolean {
    let valid = true
    for (const field of Object.keys(rules)) {
      if (!validateField(field)) {
        valid = false
      }
    }
    return valid
  }

  function clearErrors(): void {
    Object.keys(errors).forEach((key) => {
      errors[key] = ''
    })
  }

  function setServerErrors(serverErrors: Record<string, string>): void {
    Object.entries(serverErrors).forEach(([field, message]) => {
      errors[field] = message
    })
  }

  function resetForm(): void {
    Object.assign(form, { ...initialValues })
    clearErrors()
    isDirty.value = false
    isSubmitting.value = false
  }

  function markDirty(): void {
    isDirty.value = true
  }

  function setSubmitting(value: boolean): void {
    isSubmitting.value = value
  }

  return {
    form,
    errors,
    isDirty,
    isSubmitting,
    isValid,
    state,
    validateField,
    validateAll,
    clearErrors,
    setServerErrors,
    resetForm,
    markDirty,
    setSubmitting
  }
}
