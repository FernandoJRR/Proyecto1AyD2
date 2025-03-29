<template>
  <div class="m-6 ml-12">
    <router-link to="/pacientes">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text />
    </router-link>

    <div v-if="patientState.status === 'pending'" class="flex flex-col text-black">
      Cargando...
    </div>

    <div v-else-if="patientState.status === 'error'" class="text-black">
      Ocurrió un error inesperado
    </div>

    <div v-else class="grid grid-cols-1 text-black">
      <div class="flex flex-col gap-4">

        <div class="flex flex-row gap-4 mt-4">
          <h1 class="text-3xl font-semibold text-black">
            {{ patientState.data.firstnames }} {{ patientState.data.lastnames }}
          </h1>
        </div>

        <div class="text-lg text-black">
          <p class="font-medium text-black">DPI:</p>
          <p class="font-normal text-black">
            {{ patientState.data.dpi }}
          </p>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useQuery } from "@pinia/colada"
import { getPatient, type PatientResponseDTO } from "~/lib/api/patients/patients"

const { state: patientState } = useCustomQuery({
  key: ["patient",useRoute().params.id as string],
  query: () => getPatient(useRoute().params.id as string)
})
</script>

<style scoped></style>
