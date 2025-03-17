function getCookie(name: string): string | null {
  if (typeof document === 'undefined') return null;
  const cookies = document.cookie.split('; ');
  for (const cookie of cookies) {
    const [key, value] = cookie.split('=');
    if (key === name) {
      return decodeURIComponent(value);
    }
  }
  return null;
}

export const $api = $fetch.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  onRequest({ request, options, error }) {
    const userAuth = getCookie('proyecto1ayd2-user-token')
    options.headers.set('Authorization',userAuth ? `Bearer ${userAuth}` : '')
  },
})
