<template>
  <div class="m-6">
    <DataTable :value="state.data as any[]" tableStyle="min-width: 50rem" stripedRows
      :loading="asyncStatus == 'loading'">
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Usuarios</span>
          <router-link to="/admin/personal/crear">
            <Button icon="pi pi-plus" rounded raised />
          </router-link>
        </div>
      </template>
      <Column header="Nombre Completo">
        <template #body="slotProps">
          <template v-if="slotProps.data.firstName !== null">
            {{ `${slotProps.data.firstName} ${slotProps.data.lastName}` }}
          </template>
          <template v-else>
            {{ `Admin` }}
          </template>
        </template>
      </Column>
      <Column header="Area">
        <template #body="slotProps">
          <Tag :value="slotProps.data.employeeType.name" />
        </template>
      </Column>
      <Column header="Acciones">
        <template #body="slotProps">
            <RouterLink :to="`/admin/personal/${slotProps.data.id}`">
              <Button label="Ver" severity="info" rounded text/>
            </RouterLink>
          <Button v-if="slotProps.data.firstName !== null" label="Deshabilitar" severity="danger" rounded text />
        </template>
      </Column>
      <template #footer> Hay en total {{ state.data ? (state.data as any[]).length : 0 }} usuarios. </template>
    </DataTable>
  </div>
</template>
<script setup lang="ts">
import { useQuery } from '@pinia/colada';
import { RouterLink } from 'vue-router';
import { getAllEmployees, type Employee } from '~/lib/api/admin/employee';

const { state, asyncStatus } = useQuery({
  key: ['empleados'],
  query: () => getAllEmployees()
})
</script>
