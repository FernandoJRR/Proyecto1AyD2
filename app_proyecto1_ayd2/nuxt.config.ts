// https://nuxt.com/docs/api/configuration/nuxt-config
import tailwindcss from "@tailwindcss/vite"
import Aura from "@primeuix/themes/aura"
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  srcDir: 'app/',
  css: ['~/assets/css/main.css'],
  modules: ['@primevue/nuxt-module', '@pinia/nuxt', '@pinia/colada-nuxt'],
  primevue:{
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