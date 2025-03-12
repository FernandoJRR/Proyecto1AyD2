<template>
  <div class="m-6 ml-12">
    <Button text><i class="pi pi-arrow-left"/><router-link to="/admin/personal">Ver Todos</router-link></Button>
    <div>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { Usuario } from '~/lib/api/admin/usuarios';

const { state } = useQuery({
  key: ['usuario'],
  query: () => $api<Usuario>(`/names/${useRoute().params.id}`)
})
</script>
