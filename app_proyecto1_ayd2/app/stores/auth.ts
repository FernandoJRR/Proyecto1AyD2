import { defineStore } from "pinia";
import { toast } from "vue-sonner";
import type { Employee } from "~/lib/api/admin/employee";

export interface User {
  username: string
}

export interface LoginPayload {
  username: string,
  password: string
}

export const useAuthStore = defineStore('auth', {
  persist: true,
  state: () => ({
    authenticated: false,
    loading: false,
    user: null as User | null,
    employee: null as Employee | null,
    staffRoles: [] as any[]
  }),
  actions: {
    async login(payload: LoginPayload) {
      this.loading = true

      const router = useRouter()

      const { data, error } = await $api<any>(
        '/login',
        {
          method: 'POST',
          body: payload
        }
      )
      if (error.value) {
        if (error.value.data) {
          toast.error(error.value.data.meesage)
          error.value = null
          this.loading = false
          return
        }
        if (error.value.cause) {
          toast.error(error.value!.meesage)
          error.value = null
          this.loading = false
          return
        }
      }

      console.log("RESPONSE LOGIN")
      console.log(data)
      // Exito
      const tokenCookie = useCookie('proyecto1ayd2-user-token')
      tokenCookie.value = data?.value?.token

      this.user = {username: data?.value?.username ?? null } 
      this.employee = data?.value?.employee ?? null
      this.authenticated = true

      toast.success('Bienvenido!')
      router.push('/')

      this.loading = false
      return { data, error: false }
    },
    async logout() {
      this.loading = true
      const tokenCookie = useCookie('proyecto1ayd2-user-token')
      tokenCookie.value = null

      this.user = null
      this.employee = null
      this.authenticated = false

      this.loading = false
      const router = useRouter()
      router.push('/login')
    }
  }
})
