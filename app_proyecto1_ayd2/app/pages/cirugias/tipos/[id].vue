<template>
  <div class="m-6 ml-12 text-black">
    <router-link to="/cirugias">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text />
    </router-link>

    <div v-if="surgeryTypeState.status === 'pending'" class="mt-6">Cargando...</div>

    <div v-else-if="surgeryTypeState.status === 'error'" class="mt-6 text-red-500">
      Ocurrió un error inesperado
    </div>

    <div
      v-else
      class="w-full mt-6 bg-white shadow-md rounded-2xl p-6 border border-gray-200"
    >
      <h1 class="text-3xl font-bold mb-6">Detalle del Tipo de Cirugía</h1>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm font-medium mb-1">Nombre</label>
          <InputText :modelValue="surgeryTypeState.data.type" readonly class="w-full" />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">Descripción</label>
          <InputText :modelValue="surgeryTypeState.data.description" readonly class="w-full" />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">Pago al Especialista</label>
          <InputNumber
            :modelValue="surgeryTypeState.data.specialistPayment"
            mode="currency"
            currency="GTQ"
            locale="es-GT"
            readonly
            class="w-full"
          />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">Costo Hospitalario</label>
          <InputNumber
            :modelValue="surgeryTypeState.data.hospitalCost"
            mode="currency"
            currency="GTQ"
            locale="es-GT"
            readonly
            class="w-full"
          />
        </div>

        <div class="md:col-span-2">
          <label class="block text-sm font-medium mb-1">Costo Total de Cirugía</label>
          <InputNumber
            :modelValue="surgeryTypeState.data.surgeryCost"
            mode="currency"
            currency="GTQ"
            locale="es-GT"
            readonly
            class="w-full"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getSurgeryType } from '~/lib/api/surgeries/surgeries';
import { Button, InputText, InputNumber } from 'primevue';

const { state: surgeryTypeState } = useCustomQuery({
  key: ['surgery-type', useRoute().params.id as string],
  query: () => getSurgeryType(useRoute().params.id as string),
});
</script>
