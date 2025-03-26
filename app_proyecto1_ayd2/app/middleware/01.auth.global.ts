import { storeToRefs } from 'pinia'
import { toast } from 'vue-sonner'

export default defineNuxtRouteMiddleware((to, _from) => {
  const { user } = storeToRefs(useAuthStore())

  const token = useCookie('proyecto1ayd2-user-token')

  if (!token.value && !to?.fullPath.includes('login')) {
    toast.error("Debes loguearte para acceder al sitio")
    return navigateTo('/login')
  }

  if (token.value && to.fullPath.includes('login')) {
    return navigateTo('/')
  }
})
