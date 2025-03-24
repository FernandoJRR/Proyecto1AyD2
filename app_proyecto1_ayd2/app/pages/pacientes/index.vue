<template>
  <div class="m-2">
    <h1 class="text-black mb-2 text-2xl">Pacientes</h1>

    <div class="flex justify-between items-center mb-4 gap-4">
      <div class="flex items-center gap-2">
        <InputText
          v-model="searchTerm"
          placeholder="Buscar por nombre..."
          class="p-inputtext-sm w-64"
          @keyup.enter="buscarPacientes"
        />
        <Button
          icon="pi pi-search"
          label="Buscar"
          @click="buscarPacientes"
          rounded
          outlined
          severity="info"
        />
      </div>

      <div class="flex items-center gap-2">
        <router-link to="/pacientes/crear">
          <Button icon="pi pi-plus" label="Crear Paciente" rounded raised />
        </router-link>

        <Button
          icon="pi pi-refresh"
          label="Recargar Página"
          @click="recargarDatos"
          rounded
          outlined
          severity="help"
        />
      </div>
    </div>

    <DataTable
      :value="patientsState.data as any[]"
      tableStyle="min-width: 50rem"
      stripedRows
      :loading="asyncStatus === 'loading'"
      class="mb-10"
    >
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Listado de Pacientes</span>
        </div>
      </template>

      <Column field="id" header="ID"></Column>
      <Column field="firstnames" header="Nombres"></Column>
      <Column field="lastnames" header="Apellidos"></Column>
      <Column field="dpi" header="DPI"></Column>

      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/pacientes/${slotProps.data.id}`">
            <Button label="Ver" severity="info" rounded text />
          </RouterLink>
          <RouterLink :to="`/pacientes/editar/${slotProps.data.id}`">
            <Button label="Editar" severity="warning" rounded text />
          </RouterLink>
        </template>
      </Column>

      <template #footer>
        Hay en total
        {{ patientsState.data ? (patientsState.data as any[]).length : 0 }}
        pacientes registrados.
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { RouterLink } from "vue-router"
import { getAllPatients } from "~/lib/api/patients/patients"
import { InputText, Button } from "primevue"

const searchTerm = ref("")

const {
  state: patientsState,
  asyncStatus,
  refetch: refetchPatients
} = useQuery({
  key: ["patients"],
  query: () => getAllPatients(searchTerm.value === "" ? null : searchTerm.value)
})

const buscarPacientes = () => {
  refetchPatients()
}

const recargarDatos = () => {
  refetchPatients()
}
</script>
