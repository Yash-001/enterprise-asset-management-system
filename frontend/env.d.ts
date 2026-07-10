/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_APP_TITLE: string
  readonly VITE_FEATURE_NOTIFICATIONS: string
  readonly VITE_FEATURE_REPORTS: string
  readonly VITE_FEATURE_DOCUMENTS: string
  readonly VITE_FEATURE_ANALYTICS: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
