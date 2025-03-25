<template>
  <div class="m-6 ml-12">
    <router-link to="/admin/personal">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text />
    </router-link>
    <div v-if="state.status === 'pending'" class="flex flex-col">
      Cargando...
    </div>
    <div v-else-if="state.status === 'error'">
      Ocurrio un error inesperado
    </div>
    <div v-else class="grid grid-cols-2 mt-4">
      <div class="flex flex-col gap-4">
        <div class="flex flex-row gap-4">
          <h1 class="text-3xl font-semibold">{{ `${state.data.employee.firstName} ${state.data.employee.lastName}` }}</h1>
          <Tag :value="state.data.employee.employeeType.name" />
        </div>
        <div class="flex flex-row gap-x-2 text-lg">
          <p class="font-medium">Salario Actual:</p>
          <p class="font-semibold">Q.{{ state.data.employee.salary }}</p>
        </div>
        <h1 class="text-xl font-medium">Descuentos</h1>
        <div class="flex ">
          <p>IGGS
            <Tag :severity="state.data.employee.iggsPercentage ? 'success' : 'danger'">
              {{ state.data.employee.iggsPercentage ? `${state.data.employee.iggsPercentage}%` : 'No Aplica' }}</Tag>
          </p>
        </div>
        <div>
          <p>IRTRA
            <Tag :severity="state.data.employee.irtraPercentage ? 'success' : 'danger'">
              {{ state.data.employee.irtraPercentage ? `${state.data.employee.irtraPercentage}%` : 'No Aplica' }}</Tag>
          </p>
        </div>
      </div>
      <div>
        <div class="flex flex-row">
          <DataTable dataKey="id" :value="state.data.histories">
            <template #header>
              <div class="flex justify-start">
                <p class="text-3xl font-medium mb-4">Historial</p>
              </div>
            </template>
            <template #loading> Loading customers data. Please wait. </template>
            <Column field="commentary" header="Tipo" style="min-width: 12rem">
              <template #body="{ data }">
                <Tag severity="secondary">
                  {{ data.historyType.type }}
                </Tag>
              </template>
            </Column>
            <Column header="Comentario" style="min-width: 12rem">
              <template #body="{ data }">
                <div class="flex items-center gap-2">
                  <p class="text-lg font-medium">
                    {{ data.historyType.type === "Aumento Salarial" || data.historyType.type === "Disminucion Salarial" 
                      ? `Salario modificado a Q.${data.commentary}` : data.commentary }}
                  </p>
                </div>
              </template>
            </Column>
            <Column header="Fecha" style="min-width: 14rem">
              <template #body="{ data }">
                <div class="flex items-center gap-2">
                  <Tag rounded>
                    {{ data.historyDate }}
                  </Tag>
                </div>
              </template>
            </Column>
          </DataTable>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useQuery } from '@pinia/colada';
import { getEmployeeById } from '~/lib/api/admin/employee';

const { state } = useQuery({
  key: ['usuario', useRoute().params.id as string],
  query: () => getEmployeeById(useRoute().params.id as string).then((res) => { return {
    employee: res.employeeResponseDTO, 
    histories: res.employeeHistories
  }})
})
</script>
