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
  onRequest({ options }) {
    const userAuth = getCookie('proyecto1ayd2-user-token')
    options.headers.set('Authorization', userAuth ? `Bearer ${userAuth}` : '')
  },
  async onResponseError({ response }) {
    const errorData = await response._data
    console.error('API Error:', response)

    throw createError({
      statusCode: response.status,
      message: errorData?.message || 'Ha ocurrido un error'
    })
  },
  async onRequestError({ request }) {
    console.error('API Error:', request)

    throw createError({
      message: 'Ha ocurrido un error'
    })
  }
})


export const genParams = (objectParams: any): string => {
  if (!objectParams) return "";
  const params = new URLSearchParams();
  Object.entries(objectParams).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== "") {
      params.append(key, String(value));
    }
  });
  const query = params.toString();
  return query ? "?" + query : "";
};
