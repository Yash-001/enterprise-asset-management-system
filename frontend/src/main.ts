import { createApp } from 'vue'
import App from './App.vue'
import { registerPlugins } from './plugins'
import { setupInterceptors } from './api'

import 'primeicons/primeicons.css'
import './styles/main.scss'

const app = createApp(App)

// Register all plugins (Pinia, Router, PrimeVue, Toast, Confirm, Components)
registerPlugins(app)

// Setup Axios interceptors (JWT attach, 401 handling)
setupInterceptors()

app.mount('#app')
