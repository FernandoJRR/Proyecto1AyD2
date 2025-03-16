// https://nuxt.com/docs/api/configuration/nuxt-config
import tailwindcss from "@tailwindcss/vite"
import Aura from "@primeuix/themes/aura"
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  srcDir: 'app/',
  css: ['~/assets/css/main.css'],
  // app: {
  //   baseURL: '/app/',              // 👈 Esto asegura que la app corre en /app/
  //   buildAssetsDir: '/app/_nuxt/'  // 👈 Asegura que los assets JS/CSS carguen bien
  // },
  modules: ['@primevue/nuxt-module', '@pinia/nuxt', '@pinia/colada-nuxt'],
  primevue: {
    options: {
      theme: {
        preset: Aura,
        cssLayer: {
          name: 'primevue',
          order: 'tailwind-base, primevue, tailwind-utilities'
        }
      }
    }
  },
  vite: {
    plugins: [
      tailwindcss()
    ]
  }
})