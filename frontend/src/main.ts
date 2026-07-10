import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { primeVuePlugin } from './plugins/primevue'

import 'primeicons/primeicons.css'
import './styles/main.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(primeVuePlugin)

app.mount('#app')
