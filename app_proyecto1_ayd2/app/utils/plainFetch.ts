export const $api = $fetch.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  onRequest({ request, options, error }) {
    const userAuth = useCookie('proyecto1ayd2-user-token')
    options.headers.set('Authorization',userAuth.value ? `Bearer ${userAuth.value}` : '')
  },
})
