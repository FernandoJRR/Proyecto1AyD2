import { onServerPrefetch } from 'vue'
import { useQuery } from '@pinia/colada'

export function useCustomQuery<T>(options: {
  key: any[]
  query: () => Promise<T>
}) {
  const queryOptions = {
    ...options,
    enabled: !import.meta.server
  }
  const query = useQuery(queryOptions)

  if (import.meta.server) {
    onServerPrefetch(async () => {
      await query.refresh().catch((error) => {
        console.error('SSR Query Refresh Error:', error)
      })
    })
  }

  return query
}
