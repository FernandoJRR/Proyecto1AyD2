<template>
  <div class="m-6 ml-12">
    <router-link to="/farmacia">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text />
    </router-link>

    <div
      v-if="medicineState.status === 'pending'"
      class="flex flex-col text-black"
    >
      Cargando...
    </div>

    <div v-else-if="medicineState.status === 'error'" class="text-black">
      Ocurrió un error inesperado
    </div>

    <div v-else class="grid grid-cols-1 text-black">
      <div class="flex flex-col gap-4">
        <div class="flex flex-row gap-4 mt-4">
          <h1 class="text-3xl font-semibold text-black">
            {{ medicineState.data.name }}
          </h1>
          <Tag :severity="getStockSeverity(medicineState.data)">
            {{ getStockStatus(medicineState.data) }}
          </Tag>
        </div>

        <div class="text-lg text-black">
          <p class="font-medium text-black">Descripción:</p>
          <p class="font-normal text-black">
            {{ medicineState.data.description }}
          </p>
        </div>

        <div class="flex flex-row gap-x-2 text-lg text-black">
          <p class="font-medium text-black">Precio:</p>
          <p class="font-semibold text-black">
            Q.{{ medicineState.data.price.toFixed(2) }}
          </p>
        </div>

        <div class="flex flex-row gap-x-2 text-lg text-black">
          <p class="font-medium text-black">Cantidad en stock:</p>
          <p class="font-semibold text-black">
            {{ medicineState.data.quantity }}
          </p>
        </div>

        <div class="flex flex-row gap-x-2 text-lg text-black">
          <p class="font-medium text-black">Cantidad mínima requerida:</p>
          <p class="font-semibold text-black">
            {{ medicineState.data.minQuantity }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useQuery } from "@pinia/colada";
import { getMedicine, type Medicine } from "~/lib/api/medicines/medicine";

const { state: medicineState } = useCustomQuery({
  key: ["medicine", useRoute().params.id as string],
  query: () => getMedicine(useRoute().params.id as string),
});

const getStockStatus = (medicine: Medicine) => {
  if (medicine.quantity === 0) {
    return "Agotado";
  } else if (medicine.quantity <= medicine.minQuantity) {
    return "Cantidad Muy Baja";
  } else {
    return "Disponible";
  }
};

const getStockSeverity = (medicine: Medicine) => {
  if (medicine.quantity === 0) {
    return "danger";
  } else if (medicine.quantity <= medicine.minQuantity) {
    return "warn";
  } else {
    return "success";
  }
};
</script>

<style scoped></style>
