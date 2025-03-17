import { defineStore } from "pinia";
import { toast } from "vue-sonner";
import type { Entity } from "~/lib/api/utils/entity";

export interface User extends Entity {
  username: string
  employee_id: string
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
    staffRoles: [] as any[]
  }),
  actions: {
    async login(payload: LoginPayload) {
      this.loading = true

      const router = useRouter()

      /*
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
      */
      const data = { value: { token: 'token', user: { id: '', username: 'frodriguez', employee_id: 'id'} } }

      // Exito
      const tokenCookie = useCookie('proyecto1ayd2-user-token')
      tokenCookie.value = data?.value?.token

      this.user = data?.value?.user ?? null
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
      this.authenticated = false

      this.loading = false
      const router = useRouter()
      router.push('/login')
    }
  }
})
