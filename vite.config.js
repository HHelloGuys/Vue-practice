import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // Vue의 /api 요청은 메인 eGovFramework 백엔드로만 전달한다.
      '/api': 'http://localhost:8080',
    },
  },
})
