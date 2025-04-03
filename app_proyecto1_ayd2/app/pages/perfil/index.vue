<template>
  <div class="m-6 ml-12">
    <router-link to="/">
      <Button label="Volver al Inicio" icon="pi pi-arrow-left" text />
    </router-link>
    <div v-if="state.status === 'pending'" class="flex flex-col">
      Cargando...
    </div>
    <div v-else-if="state.status === 'error'">
      Ocurrio un error inesperado
    </div>
    <div v-else class="grid grid-cols-2 mt-4 gap-4">
      <div class="flex flex-col gap-4">
        <div class="flex flex-row gap-4">
          <h1 class="text-3xl font-semibold">{{ `${state.data.employee.firstName} ${state.data.employee.lastName}` }}
          </h1>
          <Tag :value="state.data.employee.employeeType.name" />
        </div>
        <div class="flex flex-row gap-x-2 text-lg">
          <p class="font-medium">Salario Actual:</p>
          <p class="font-semibold">Q.{{ state.data.employee.salary }}</p>
        </div>
        <h1 class="text-xl font-semibold">Descuentos</h1>
        <div class="flex flex-row gap-6">
          <div class="flex flex-row h-6 gap-4">
            <p class="font-medium">IGGS</p>
            <Tag :severity="state.data.employee.iggsPercentage ? 'success' : 'danger'">
              {{ state.data.employee.iggsPercentage ? `${state.data.employee.iggsPercentage}%` : 'No Aplica' }}</Tag>
          </div>
          <div class="flex flex-row h-6 gap-4">
            <p class="font-medium">IRTRA</p>
            <Tag :severity="state.data.employee.irtraPercentage ? 'success' : 'danger'">
              {{ state.data.employee.irtraPercentage ? `${state.data.employee.irtraPercentage}%` : 'No Aplica' }}
            </Tag>
          </div>
        </div>
        <DataTable :value="filteredVacations">
          <template #header>
            <div class="flex justify-start gap-2 max-h-10 mb-6">
              <p class="text-3xl font-medium">Vacaciones</p>
              <div class="mb-4">
                <Select v-model="selectedYear" :options="availableYears" placeholder="Elige un periodo" class="ml-4">
                  <template #option="slotProps">
                    <div class="flex items-center gap-2">
                      <span>{{ slotProps.option }}</span>
                    </div>
                  </template>
                </Select>
              </div>
              <RouterLink :to="`/perfil/editar-periodo-${selectedYear}`">
                <Button severity="info" label="Editar periodo" icon="pi pi-pencil"></Button>
              </RouterLink>
              <RouterLink to="/perfil/agregar-periodo">
                <Button label="Agregar vacaciones" icon="pi pi-plus"></Button>
              </RouterLink>
            </div>
          </template>
          <template #loading>
            Cargando vacaciones del empleado.
          </template>
          <Column field="beginDate" header="Fecha Inicio">
          </Column>
          <Column field="endDate" header="Fecha Fin">
          </Column>
          <Column field="wasUsed" header="Se Uso">
            <template #body="{ data }">
              <div class="flex items-center gap-2">
                <Tag v-if="data.wasUsed" rounded>
                  Si
                </Tag>
                <Tag v-else severity="danger" rounded>
                  No
                </Tag>
              </div>
            </template>
          </Column>
          <Column header="Acciones">
            <template #body="{ data }">
              <Button v-if="!data.wasUsed" size="small" severity="help" icon="pi pi-check" label="Marcar como Usada"
                @click="markVacationAsUsed(data.id)" />
            </template>
          </Column>
          <template #footer v-if="filteredVacations.every(vacation => vacation.wasUsed)">
            <div class="flex flex-row gap-4 items-center" >
              <p class="font-semibold">
                Todas las Vacaciones Usadas
              </p>
              <Button label="Imprimir Finiquito" size="small" variant="outlined" 
                icon="pi pi-print" severity="warn" @click="printInvoice" />
            </div>
          </template>
        </DataTable>
      </div>
      <div>
        <div class="flex flex-row">
          <DataTable dataKey="id" :value="state.data.histories">
            <template #header>
              <div class="flex justify-start">
                <p class="text-3xl font-medium mb-4">Historial</p>
              </div>
            </template>
            <template #loading> Cargando historial del empleado. </template>
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
import { Select } from 'primevue';
import { toast } from 'vue-sonner';
import { getEmployeeById } from '~/lib/api/admin/employee';
import { updateMarkAsUsed } from '~/lib/api/vacations/vacations';

const { employee } = storeToRefs(useAuthStore())

const { state, refetch } = useCustomQuery({
  key: ['usuario', useRoute().params.id as string],
  query: () => getEmployeeById(employee.value?.id ?? '').then((res) => {
    return {
      employee: res.employeeResponseDTO,
      histories: res.employeeHistories,
      vacations: res.vacations
    }
  })
})

const selectedYear = ref<number | null>(null);

const availableYears = computed(() => {
  return state.value.data?.vacations
    ? Object.keys(state.value.data.vacations).map((key) => Number(key))
    : [];
});

const filteredVacations = computed(() => {
  if (selectedYear.value && state.value.data?.vacations && state.value.data.vacations[selectedYear.value]) {
    return state.value.data.vacations[selectedYear.value];
  }
  return [];
});

const { mutate: markVacationAsUsed } = useMutation({
  mutation: (vacationsId: string) => updateMarkAsUsed(vacationsId),
  onError(error) {
    console.error(error);
    toast.error('Ocurrió un error al actualizar las vacaciones', {
      description: error.message,
    });
  },
  onSuccess() {
    toast.success('Vacaciones actualizadas correctamente');
    refetch()
  },
});

watch(
  availableYears,
  (newYears) => {
    if (!selectedYear.value && newYears.length > 0) {
      selectedYear.value = newYears[0];
    }
  },
  { immediate: true }
);

function printInvoice() {
  alert("por implementar")
}
</script>
