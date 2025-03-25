<template>
  <div class="m-2">
    <h1 class="text-black mb-2 text-2xl">Médicos Especialistas</h1>

    <div class="flex justify-between items-center mb-4 gap-4">
      <div class="flex items-center gap-2">
        <InputText
          v-model="searchTerm"
          placeholder="Buscar por nombre..."
          class="p-inputtext-sm w-64"
          @keyup.enter="buscarEspecialistas"
        />
        <Button
          icon="pi pi-search"
          label="Buscar"
          @click="buscarEspecialistas"
          rounded
          outlined
          severity="info"
        />
      </div>

      <div class="flex items-center gap-2">
        <router-link to="/admin/medicos-especialistas/crear">
          <Button icon="pi pi-plus" label="Crear Especialista" rounded raised />
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
      :value="especialistasState.data as any[]"
      tableStyle="min-width: 50rem"
      stripedRows
      :loading="asyncStatus === 'loading'"
      class="mb-10"
    >
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Lista de Especialistas</span>
        </div>
      </template>

      <Column field="id" header="ID" />
      <Column field="nombres" header="Nombres" />
      <Column field="apellidos" header="Apellidos" />
      <Column field="dpi" header="DPI" />

      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/admin/medicos-especialistas/${slotProps.data.id}`">
            <Button label="Ver" severity="info" rounded text />
          </RouterLink>
          <RouterLink
            :to="`/admin/medicos-especialistas/editar/${slotProps.data.id}`"
          >
            <Button label="Editar" severity="warning" rounded text />
          </RouterLink>
        </template>
      </Column>

      <template #footer>
        Hay en total
        {{
          especialistasState.data
            ? (especialistasState.data as any[]).length
            : 0
        }}
        especialistas.
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { getAllSpecialistEmployees } from "~/lib/api/admin/specialist-employee";
import { InputText, Button } from "primevue";

const searchTerm = ref("");

const {
  state: especialistasState,
  asyncStatus,
  refetch: refetchEspecialistas,
} = useQuery({
  key: ["especialistas"],
  query: () =>
    getAllSpecialistEmployees(
      searchTerm.value === "" ? null : searchTerm.value
    ),
});

const buscarEspecialistas = () => {
  refetchEspecialistas();
};

const recargarDatos = () => {
  refetchEspecialistas();
};
</script>
