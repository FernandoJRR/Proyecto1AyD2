<template>
  <div class="m-6 ml-12">
    <router-link to="/admin/medicos-especialistas">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text />
    </router-link>

    <div v-if="specialistState.status === 'pending'" class="flex flex-col text-black">
      Cargando...
    </div>

    <div v-else-if="specialistState.status === 'error'" class="text-black">
      Ocurrió un error inesperado
    </div>

    <div v-else class="grid grid-cols-1 text-black">
      <div class="flex flex-col gap-4">

        <div class="flex flex-row gap-4 mt-4">
          <h1 class="text-3xl font-semibold text-black">
            {{ specialistState.data.nombres }} {{ specialistState.data.apellidos }}
          </h1>
        </div>

        <div class="text-lg text-black">
          <p class="font-medium text-black">DPI:</p>
          <p class="font-normal text-black">
            {{ specialistState.data.dpi }}
          </p>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useQuery } from "@pinia/colada";
import { getSpecialistEmployeeById } from "~/lib/api/admin/specialist-employee";

const { state: specialistState } = useQuery({
  key: ["specialist"],
  query: () => getSpecialistEmployeeById(useRoute().params.id as string),
});
</script>

<style scoped></style>
