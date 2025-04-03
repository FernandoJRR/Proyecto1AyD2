<template>
  <div class="m-6 ml-12">
    <DataTable :value="employeesWithInvoice.data">
      <template #header>
        <div class="flex justify-start gap-2 max-h-14 mb-6">
          <p class="text-3xl font-medium">Vacaciones</p>
          <div class="mb-4 flex flex-row gap-4">
            <FloatLabel>
              <label for="number">Periodo</label>
              <DatePicker v-model="selectedYear" view="year" dateFormat="yy" @value-change="refetch" />
            </FloatLabel>
            <Button label="Imprimir todos los finiquitos" icon="pi pi-print" severity="help" @click="generateAllInvoice"/>
          </div>
        </div>
      </template>
      <template #loading>
        Cargando a los empleados.
      </template>
      <Column header="Nombre Completo">
        <template #body="slotProps">
          <template v-if="slotProps.data.firstName !== null">
            {{ `${slotProps.data.firstName} ${slotProps.data.lastName}` }}
          </template>
          <template v-else>
            {{ `Admin` }}
          </template>
        </template>
      </Column>
      <Column header="Acciones">
        <template #body="slotProps">
          <Button label="Imprimir finiquito" icon="pi pi-print" severity="warn" variant="outlined" 
            @click="generateInvoiceEmployee(slotProps.data)"/>
        </template>
      </Column>
      <template #footer>
        <div class="flex flex-row gap-4 items-center">
          <p class="font-semibold" v-if="!employeesWithInvoice.data || employeesWithInvoice.data.length <= 0">
            No hay empleados aptos
          </p>
          <p class="font-semibold" v-else>
            Hay {{ employeesWithInvoice.data?.length }} empleados aptos
          </p>
        </div>
      </template>
    </DataTable>
  </div>
</template>
<script setup lang="ts">
import { DatePicker } from 'primevue';
import { getEmployeeInvoiceForPeriod, type Employee } from '~/lib/api/admin/employee';
import { toast } from 'vue-sonner';

const selectedYear = ref<Date>(new Date())

const { state: employeesWithInvoice, refetch } = useCustomQuery({
  key: ['employees-invoice', selectedYear.value.getFullYear()],
  query: () => getEmployeeInvoiceForPeriod(selectedYear.value.getFullYear())
})

const generateAllInvoice = () => {
  if (employeesWithInvoice.value.data === undefined) {
    toast.error("Ocurrio un error al generar los finiquitos", {description: "No hay empleados en este periodo que apliquen para finiquito"})
    return
  }

  generatePeriodVacationInvoicePDF(employeesWithInvoice.value.data, selectedYear.value.getFullYear())
}

const generateInvoiceEmployee = (employeeData: Employee) => {
  if (employeesWithInvoice.value.data === undefined) {
    toast.error("Ocurrio un error al generar los finiquitos", {description: "No hay empleados en este periodo que apliquen para finiquito"})
    return
  }

  generateEmployeePeriodInvoicePDF(employeeData, selectedYear.value.getFullYear())
}
</script>
