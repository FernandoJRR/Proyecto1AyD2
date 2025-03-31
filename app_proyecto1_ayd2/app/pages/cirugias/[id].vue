<template>
  <div class="m-6 ml-12 text-black">
    <div class="mb-6">
      <Button
        label="Volver"
        icon="pi pi-arrow-left"
        text
        @click="useRouter().back()"
      />
    </div>

    <div v-if="surgeryState.status === 'pending'" class="text-black">Cargando...</div>

    <div v-else-if="surgeryState.status === 'error'" class="text-red-500">
      Ocurrió un error inesperado
    </div>

    <div
      v-else
      class="bg-white shadow rounded-xl p-6 border border-gray-200 space-y-8"
    >
      <h1 class="text-3xl font-bold">Detalle de la Cirugía</h1>

      <!-- Datos generales -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm mb-1">ID de Cirugía</label>
          <InputText :modelValue="surgeryState.data.id" readonly class="w-full" />
        </div>
        <div>
          <label class="block text-sm mb-1">Fecha de Realización</label>
          <InputText :modelValue="surgeryState.data.performedDate || 'No realizada'" readonly class="w-full" />
        </div>
        <div>
          <label class="block text-sm mb-1">Costo Hospitalario</label>
          <InputNumber
            :modelValue="surgeryState.data.hospitalCost"
            mode="currency"
            currency="GTQ"
            locale="es-GT"
            readonly
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm mb-1">Costo Total</label>
          <InputNumber
            :modelValue="surgeryState.data.surgeryCost"
            mode="currency"
            currency="GTQ"
            locale="es-GT"
            readonly
            class="w-full"
          />
        </div>
      </div>

      <!-- Tipo de cirugía -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm mb-1">Tipo</label>
          <InputText :modelValue="surgeryState.data.surgeryType.type" readonly class="w-full" />
        </div>
        <div>
          <label class="block text-sm mb-1">Descripción</label>
          <InputText :modelValue="surgeryState.data.surgeryType.description" readonly class="w-full" />
        </div>
        <div>
          <label class="block text-sm mb-1">Pago al Especialista</label>
          <InputNumber
            :modelValue="surgeryState.data.surgeryType.specialistPayment"
            mode="currency"
            currency="GTQ"
            locale="es-GT"
            readonly
            class="w-full"
          />
        </div>
      </div>

      <!-- Médicos responsables -->
      <div>
        <label class="block text-sm mb-2 font-semibold">Médicos Encargados</label>
        <ul class="list-disc list-inside text-sm text-gray-700 ml-2">
          <li
            v-for="empleado in surgeryState.data.surgeryEmployees"
            :key="empleado.specialistEmployeeId ?? 'unknown-key'"
          >
            {{ empleado.employeeName }} {{ empleado.employeeLastName }} —
            Q{{ empleado.specialistPayment }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { getSurgery } from "~/lib/api/surgeries/surgeries";
import { InputText, InputNumber, Button } from "primevue";

const {
  state: surgeryState,
  asyncStatus: asyncSurgeryStatus,
  refetch: refetchSurgery,
} = useQuery({
  key: ["surgery-view", useRoute().params.id as string],
  query: () => getSurgery(useRoute().params.id as string),
});
</script>
