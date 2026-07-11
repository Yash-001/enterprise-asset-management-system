import { defineConfig, type UserConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig(({ mode }): UserConfig => {
  const isProd = mode === 'production'

  return {
    plugins: [vue()],

    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
        '@modules': resolve(__dirname, 'src/modules'),
        '@shared': resolve(__dirname, 'src/shared')
      }
    },

    server: {
      port: 3000,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        },
        '/management': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },

    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/variables" as *;\n`
        }
      },
      devSourcemap: !isProd
    },

    build: {
      target: 'es2022',
      outDir: 'dist',
      sourcemap: false,
      minify: 'terser',
      cssMinify: true,

      // Chunk size warning threshold
      chunkSizeWarningLimit: 500,

      terserOptions: {
        compress: {
          drop_console: isProd,
          drop_debugger: isProd,
          pure_funcs: isProd ? ['console.log', 'console.info', 'console.debug'] : []
        },
        format: {
          comments: false
        }
      },

      rollupOptions: {
        output: {
          // Vendor chunking strategy
          manualChunks(id) {
            // Core Vue ecosystem
            if (id.includes('node_modules/vue') ||
                id.includes('node_modules/vue-router') ||
                id.includes('node_modules/pinia')) {
              return 'vue-vendor'
            }
            // PrimeVue (large, separate chunk)
            if (id.includes('node_modules/primevue') ||
                id.includes('node_modules/@primevue')) {
              return 'primevue-vendor'
            }
            // Axios + VueUse
            if (id.includes('node_modules/axios') ||
                id.includes('node_modules/@vueuse')) {
              return 'utils-vendor'
            }
            // PrimeIcons (fonts/icons)
            if (id.includes('node_modules/primeicons')) {
              return 'icons'
            }
          },

          // Asset file naming with content hash for cache busting
          chunkFileNames: 'js/[name]-[hash].js',
          entryFileNames: 'js/[name]-[hash].js',
          assetFileNames: (assetInfo) => {
            const name = assetInfo.name || ''
            if (/\.(woff2?|ttf|eot)$/.test(name)) return 'fonts/[name]-[hash][extname]'
            if (/\.(png|jpe?g|gif|svg|webp|ico)$/.test(name)) return 'images/[name]-[hash][extname]'
            if (/\.css$/.test(name)) return 'css/[name]-[hash][extname]'
            return 'assets/[name]-[hash][extname]'
          }
        }
      }
    },

    // Dependency pre-bundling optimization
    optimizeDeps: {
      include: [
        'vue',
        'vue-router',
        'pinia',
        'axios',
        '@vueuse/core',
        'mitt'
      ]
    },

    // Preview server (for testing production build locally)
    preview: {
      port: 4173,
      strictPort: true
    }
  }
})
