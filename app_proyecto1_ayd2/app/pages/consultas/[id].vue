<template>
  <div class="m-6 ml-12 text-black">
    <router-link to="/consultas">
      <Button label="Ver Consultas" icon="pi pi-arrow-left" text />
    </router-link>

    <div v-if="consultState.status === 'pending'" class="mt-4">
      Cargando...
    </div>

    <div v-else-if="consultState.status === 'error'" class="mt-4 text-red-500">
      Ocurrió un error inesperado
    </div>

    <div
      v-else
      class="bg-white p-6 rounded-xl shadow border border-gray-200 mt-4 max-w-xl"
    >
      <div class="flex items-center justify-between mb-4">
        <h1 class="text-2xl font-semibold">Detalle de Consulta</h1>
        <Tag :value="`ID: ${consultState.data.id}`" severity="info" />
      </div>

      <!-- Tags de estado -->
      <div class="flex gap-2 mb-4">
        <Tag
          :value="consultState.data.isPaid ? 'Pagado' : 'No Pagado'"
          :severity="consultState.data.isPaid ? 'success' : 'warn'"
        />
        <Tag
          :value="consultState.data.isInternado ? 'Internado' : 'Consulta'"
          :severity="consultState.data.isInternado ? 'danger' : 'info'"
        />
      </div>

      <!-- Detalles -->
      <p><strong>Paciente:</strong> {{ consultState.data.patient.firstnames }} {{ consultState.data.patient.lastnames }}</p>
      <p><strong>DPI:</strong> {{ consultState.data.patient.dpi }}</p>
      <p><strong>Costo Consulta:</strong> Q{{ consultState.data.costoConsulta }}</p>
      <p><strong>Costo Total:</strong> Q{{ consultState.data.costoTotal }}</p>
      <p><strong>Fecha de Creación:</strong> {{ consultState.data.createdAt }}</p>
      <p><strong>Última Actualización:</strong> {{ consultState.data.updateAt }}</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { getConsult } from "~/lib/api/consults/consult";
import { Tag } from "primevue";

const { state: consultState } = useQuery({
  key: ["consult", useRoute().params.id as string],
  query: () => getConsult(useRoute().params.id as string),
});
</script>
