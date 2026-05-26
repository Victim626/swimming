import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 80,
    host: '0.0.0.0',
    proxy: {
      '/mySpringBoot': {
        target: 'http://127.0.0.1:8081',
        changeOrigin: true
      }
    }
  }
})
