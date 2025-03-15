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

const useAuthStore = defineStore('auth', {
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

      // Success
      // Set cookies, user and role
      const tokenCookie = useCookie('cicsapp-user-token')
      const roleCookie = useCookie('cicsapp-roleuser')
      tokenCookie.value = data?.value?.token
      roleCookie.value = 'staff'
      // Set the user in the store
      this.user = data?.value?.staff ?? null
      // Set the authenticated flag
      this.authenticated = true
      // Set the staff roles
      this.staffRoles = data?.value?.staff?.roles ?? []
      // Redirect to the dashboard
      router.push('/')
      // Return the data and error
      this.loading = false
      return { data, error: false }
    }
  }
})
