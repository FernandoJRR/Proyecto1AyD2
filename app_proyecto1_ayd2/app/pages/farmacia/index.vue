<template>
  <div class="m-2">
    <h1 class="text-black mb-2 text-2xl">Farmacia</h1>

    <div class="flex justify-between items-center mb-4 gap-4">
      <div class="flex items-center gap-2">
        <InputText v-model="searchTerm" placeholder="Buscar por nombre..." class="p-inputtext-sm w-64"
          @keyup.enter="buscarMedicamentos" />
        <Button icon="pi pi-search" label="Buscar" @click="buscarMedicamentos" rounded outlined severity="info" />
      </div>

      <div class="flex items-center gap-2">
        <router-link to="/farmacia/crear">
          <Button icon="pi pi-plus" label="Crear" rounded raised />
        </router-link>

        <router-link to="/farmacia/venta">
          <Button icon="pi pi-shopping-cart" label="Iniciar Venta" rounded raised severity="success" />
        </router-link>

        <Button icon="pi pi-refresh" label="Recargar Página" @click="recargarDatos" rounded outlined severity="help" />
      </div>
    </div>

    <DataTable :value="medicinesState.data as any[]" tableStyle="min-width: 50rem" stripedRows
      :loading="asyncStatus === 'loading'" class="mb-10">
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Medicamentos</span>
        </div>
      </template>

      <Column field="name" header="Nombre"></Column>
      <Column field="quantity" header="Existencias"></Column>
      <Column field="price" header="Precio"></Column>
      <Column field="cost" header="Costo"></Column>
      <Column header="Disponible">
        <template #body="slotProps">
          <Tag v-if="slotProps.data.quantity === 0" severity="danger">
            Agotado
          </Tag>
          <Tag v-else-if="slotProps.data.quantity <= slotProps.data.minQuantity" severity="warn">
            Cantidad Muy Baja
          </Tag>
          <Tag v-else severity="success">Disponible</Tag>
        </template>
      </Column>

      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/farmacia/${slotProps.data.id}`">
            <Button label="Ver" severity="info" rounded text />
          </RouterLink>
          <RouterLink :to="`/farmacia/editar/${slotProps.data.id}`">
            <Button label="Editar" severity="warning" rounded text />
          </RouterLink>
        </template>
      </Column>

      <template #footer>
        Hay en total
        {{ medicinesState.data ? (medicinesState.data as any[]).length : 0 }}
        medicamentos.
      </template>
    </DataTable>

    <DataTable :value="medicinesLowStockState.data as any[]" tableStyle="min-width: 50rem" stripedRows
      :loading="asyncLowStockStatus === 'loading'">
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold text-red-500">Medicamentos Low Stock</span>
        </div>
      </template>


      <Column field="name" header="Nombre"></Column>
      <Column field="minQuantity" header="Min Existencias"></Column>
      <Column field="quantity" header="Existencias"></Column>

      <Column header="Estado">
        <template #body="slotProps">
          <Tag v-if="slotProps.data.quantity === 0" severity="danger">
            Agotado
          </Tag>
          <Tag v-else severity="warn">Cantidad Muy Baja</Tag>
        </template>
      </Column>

      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/farmacia/${slotProps.data.id}`">
            <Button label="Ver" severity="info" rounded text />
          </RouterLink>
          <RouterLink :to="`/farmacia/editar/${slotProps.data.id}`">
            <Button label="Editar" severity="warning" rounded text />
          </RouterLink>
        </template>
      </Column>

      <template #footer>
        Hay en total
        {{ medicinesLowStockState.data ? (medicinesLowStockState.data as any[]).length : 0 }}
        medicamentos en stock bajo.
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { getAllMedicines, getAllMedicinesLowStock } from "~/lib/api/medicines/medicine";
import { InputText, Button, Tag } from "primevue";

const searchTerm = ref("");

const {
  state: medicinesState,
  asyncStatus,
  refetch: refetchMedicines
} = useCustomQuery({
  key: ["medicines"],
  query: () => getAllMedicines(searchTerm.value === "" ? null : searchTerm.value),
});

const {
  state: medicinesLowStockState,
  asyncStatus: asyncLowStockStatus,
  refetch: refetchLowStock
} = useCustomQuery({
  key: ["medicines-low-stock"],
  query: () => getAllMedicinesLowStock(),
});

const buscarMedicamentos = () => {
  refetchMedicines();
};

const recargarDatos = () => {
  refetchMedicines();
  refetchLowStock();
};
</script>
