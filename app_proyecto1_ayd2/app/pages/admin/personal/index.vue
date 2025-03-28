<template>
  <div class="m-6">
    <DataTable :value="state.data as any[]" tableStyle="min-width: 50rem" stripedRows
      :loading="asyncStatus == 'loading'">
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Empleados</span>
          <router-link to="/admin/personal/crear">
            <Button icon="pi pi-plus" rounded raised label="Crear Empleado" />
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
          <Tag v-if="slotProps.data.desactivatedAt !== null" class="ml-2" value="Desactivado" severity="danger" />
        </template>
      </Column>
      <Column header="Acciones">
        <template #body="slotProps">
          <div v-if="slotProps.data.id !== employee?.id">
            <RouterLink :to="`/admin/personal/${slotProps.data.id}`">
              <Button label="Ver" severity="info" rounded text />
            </RouterLink>
            <RouterLink :to="`/admin/personal/editar-${slotProps.data.id}`">
              <Button label="Editar" severity="warn" rounded text />
            </RouterLink>
            <RouterLink v-if="slotProps.data.firstName && !slotProps.data.desactivatedAt"
              :to="`/admin/personal/deshabilitar-${slotProps.data.id}`">
              <Button label="Deshabilitar" severity="danger" rounded text />
            </RouterLink>
            <RouterLink v-if="slotProps.data.firstName && slotProps.data.desactivatedAt"
              :to="`/admin/personal/reactivar-${slotProps.data.id}`">
              <Button  label="Reactivar" severity="help"
                rounded text />
            </RouterLink>
          </div>
          <div v-else class="font-semibold">Eres este usuario</div>
        </template>
      </Column>
      <template #footer> Hay en total {{ state.data ? (state.data as any[]).length : 0 }} usuarios. </template>
    </DataTable>
  </div>
</template>
<script setup lang="ts">
import { RouterLink } from 'vue-router';
import { getAllEmployees, type Employee } from '~/lib/api/admin/employee';

const { employee } = storeToRefs(useAuthStore())

const { state, asyncStatus } = useCustomQuery({
  key: ['empleados'],
  query: () => getAllEmployees()
})

</script>
