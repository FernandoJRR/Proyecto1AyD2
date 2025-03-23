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

      try {
        const response = await $api<any>(
          '/v1/login',
          {
            method: 'POST',
            body: payload
          }
        )

        // Exito
        const tokenCookie = useCookie('proyecto1ayd2-user-token')
        tokenCookie.value = response?.token

        this.user = {username: response?.username ?? null } 
        this.employee = response?.employee ?? null
        this.authenticated = true

        toast.success('Bienvenido!')
        router.push('/')

        this.loading = false
        return { response, error: false }
      } catch (error: any) {
        toast.error(error.response._data)
        this.loading = false
        return
      }
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
