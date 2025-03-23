<template>
  <div class="m-2">
    <h1 class="text-black mb-2 text-2xl">Farmacia</h1>
    <DataTable
      :value="medicinesState.data as any[]"
      tableStyle="min-width: 50rem"
      stripedRows
      :loading="asyncStatus == 'loading'"
    >
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Medicamentos</span>
          <router-link to="/admin/personal/crear">
            <Button icon="pi pi-plus" rounded raised />
          </router-link>
        </div>
      </template>
      <Column field="id" header="ID"></Column>
      <Column field="name" header="Nombre"></Column>
      <Column field="quantity" header="Existencias"></Column>
      <Column field="price" header="Precio"></Column>
      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/admin/personal/${slotProps.data.id}`">
            <Button label="Ver" severity="info" rounded text />
          </RouterLink>
          <Button label="Deshabilitar" severity="danger" rounded text />
        </template>
      </Column>
      <template #footer>
        Hay en total
        {{ medicinesState.data ? (medicinesState.data as any[]).length : 0 }} medicamentos.
      </template>
    </DataTable>
  </div>
</template>
<script setup lang="ts">
import { RouterLink } from "vue-router";
import { getAllMedicines } from "~/lib/api/medicines/medicine";

const { state: medicinesState, asyncStatus } = useQuery({
  key: ['medicines'],
  query: () => getAllMedicines(),
});

</script>
