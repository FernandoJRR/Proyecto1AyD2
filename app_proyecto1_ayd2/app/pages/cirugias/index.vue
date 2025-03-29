<template>
  <div class="m-2">
    <h1 class="text-black mb-2 text-2xl">Tipos de Cirugías</h1>

    <div class="flex justify-between items-center mb-4 gap-4">
      <div class="flex items-center gap-2">
        <InputText
          v-model="searchTerm"
          placeholder="Buscar por tipo..."
          class="p-inputtext-sm w-64"
          @keyup.enter="buscarTiposCirugia"
        />
        <Button
          icon="pi pi-search"
          label="Buscar"
          @click="buscarTiposCirugia"
          rounded
          outlined
          severity="info"
        />
      </div>

      <div class="flex items-center gap-2">
        <router-link to="/cirugias/tipos/crear">
          <Button icon="pi pi-plus" label="Crear Tipo" rounded raised />
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
      :value="surgeryTypesState.data as any[]"
      tableStyle="min-width: 50rem"
      stripedRows
      :loading="asyncStatus === 'loading'"
      class="mb-10"
    >
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Tipos de Cirugía</span>
        </div>
      </template>

      <Column field="id" header="ID" />
      <Column field="type" header="Tipo" />
      <Column field="description" header="Descripción" />
      <Column field="specialistPayment" header="Pago Especialista" />
      <Column field="hospitalCost" header="Costo Hospital" />
      <Column field="surgeryCost" header="Costo Total" />

      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/cirugias/tipos/${slotProps.data.id}`">
            <Button label="Ver" severity="info" rounded text />
          </RouterLink>
          <RouterLink :to="`/cirugias/tipos/editar/${slotProps.data.id}`">
            <Button label="Editar" severity="warning" rounded text />
          </RouterLink>
        </template>
      </Column>

      <template #footer>
        Hay en total
        {{ surgeryTypesState.data ? (surgeryTypesState.data as any[]).length : 0 }}
        tipos de cirugía.
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { getSurgeriesTypes } from "~/lib/api/surgeries/surgeries";
import { InputText, Button } from "primevue";

const searchTerm = ref("");

const {
  state: surgeryTypesState,
  asyncStatus,
  refetch: refetchSurgeryTypes,
} = useCustomQuery({
  key: ["surgery-types"],
  query: () => getSurgeriesTypes(searchTerm.value === "" ? null : searchTerm.value),
});

const buscarTiposCirugia = () => {
  refetchSurgeryTypes();
};

const recargarDatos = () => {
  refetchSurgeryTypes();
};
</script>