<template>
  <div class="m-6">
    <DataTable :value="state.data as any[]" tableStyle="min-width: 50rem" stripedRows :loading="asyncStatus == 'loading'">
      <template #header>
          <div class="flex flex-wrap items-center justify-between gap-2">
    <span class="text-xl font-bold">Usuarios</span>
              <Button icon="pi pi-plus" rounded raised />
          </div>
      </template>
      <Column field="id" header="ID"></Column>
      <Column field="name" header="Nombre"></Column>
      <Column field="area" header="Area">
        <template #body="slotProps">
          <Tag :value="slotProps.data.area"/>
        </template>
      </Column>
      <Column header="Acciones">
        <template #body="slotProps">
          <Button label="Ver" severity="info" rounded text>
            <RouterLink :to="`/admin/personal/${slotProps.data.id}`">Ver</RouterLink>
          </Button>
          <Button label="Deshabilitar" severity="danger" rounded text />
        </template>
      </Column>
      <template #footer> Hay en total {{ state.data ? (state.data as any[]).length : 0 }} usuarios. </template>
    </DataTable>
  </div>
</template>
<script setup lang="ts">
import { RouterLink } from 'vue-router';
import type { Usuario } from '~/lib/api/admin/usuarios';

const { state, asyncStatus } = useQuery({
  key: ['usuarios'],
  query: () => $api<Usuario[]>('/names')
})
</script>
