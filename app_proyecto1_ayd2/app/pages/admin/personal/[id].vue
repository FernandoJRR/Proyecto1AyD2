<template>
  <div class="m-6 ml-12">
    <router-link to="/admin/personal">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text/>
    </router-link>
    <div v-if="state.status === 'pending'" class="flex flex-col">
      Cargando...
    </div>
    <div v-else-if="state.status === 'error'">
      Ocurrio un error inesperado
    </div>
    <div v-else>
      <div class="flex flex-row gap-4 mt-4">
        <div class="text-xl">
          {{ state.data.id }}
        </div>
        -
        <h1 class="text-xl font-semibold">{{ state.data.name }}</h1>
        <Tag :value="state.data.area"/>
      </div>
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
