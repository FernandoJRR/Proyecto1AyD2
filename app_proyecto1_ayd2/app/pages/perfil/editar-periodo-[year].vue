<template>
  <div class="m-6 ml-12">
    <router-link to="/perfil">
      <Button label="Volver al Perfil" icon="pi pi-arrow-left" text />
    </router-link>
    <Form>
      <div>
        <h1 class="font-semibold text-3xl mb-2">Editar Vacaciones</h1>
        <p class="font-semibold text-lg mb-8">Ingresa nuevas fechas de vacaciones para editar el periodo</p>
        <div class="mb-6 flex flex-col gap-4">
          <FloatLabel>
            <DatePicker v-model="currentPeriodYear" view="year" dateFormat="yy" :maxDate="new Date()" disabled/>
            <label>Año</label>
          </FloatLabel>
          <div class="flex flex-row items-center gap-4">
            <p class="font-medium">Dias de Vacaciones Disponibles</p>
            <Tag :severity="remainingVacationDays > 0 ? 'success' : 'danger'">{{ remainingVacationDays }} Dias</Tag>
          </div>
        </div>
        <div class="flex flex-col">
          <h1 class="font-semibold text-xl mb-8">Periodos de Vacaciones</h1>
          <ButtonGroup>
            <FloatLabel>
              <DatePicker v-model="currentVacationPeriods" selectionMode="range" :minDate="minPeriodDate"
                :maxDate="maxPeriodDate" />
              <label>Periodo</label>
            </FloatLabel>
            <Button label="Agregar periodo" icon="pi pi-plus" @click="addPeriod"
              :disabled="remainingVacationDays <= 0" />
          </ButtonGroup>
          <DataTable dataKey="id" :value="vacationPeriods">
            <Column field="beginDate" header="Fecha de Inicio" style="min-width: 12rem">
              <template #body="{ data }">
                {{ data.beginDate.toLocaleDateString() }}
              </template>
            </Column>
            <Column field="endDate" header="Fecha de Fin" style="min-width: 12rem">
              <template #body="{ data }">
                {{ data.endDate.toLocaleDateString() }}
              </template>
            </Column>
            <Column field="takenDays" header="Dias Laborales Ocupados" style="min-width: 14rem"/>
          </DataTable>
          <Button label="Guardar Vacaciones" severity="secondary" icon="pi pi-save" @click="saveVacations" />
        </div>
      </div>
    </Form>
  </div>
</template>
<script setup lang="ts">
import { ButtonGroup, DatePicker, FloatLabel, Tag } from 'primevue';
import { toast } from 'vue-sonner';
import { createVacations, getVacationDays, updateVacations, type CreateVacationsPayload } from '~/lib/api/vacations/vacations';

const { employee } = storeToRefs(useAuthStore())

const { state: vacationDays } = useCustomQuery({
  key: ["vacation-days-edit", useRoute().params.year],
  query: () => getVacationDays(),
});

const remainingVacationDays = ref<number>(0);
watch(vacationDays, (newVal) => {
  remainingVacationDays.value = newVal.data?.days ?? 0;
});

const vacationPeriods = ref<{beginDate: Date, endDate: Date, takenDays: number}[]>([])

const currentPeriodYear = ref<Date>(new Date(
  Number.parseInt(useRoute().params.year as string), 0, 1))

const tomorrowDate = currentPeriodYear.value;
tomorrowDate.setDate(currentPeriodYear.value.getDate() + 1);

const currentVacationPeriods = ref<Date[]>([currentPeriodYear.value, tomorrowDate])

const minPeriodDate = computed(() => new Date(currentPeriodYear.value.getFullYear(), 0, 1));
const maxPeriodDate = computed(() => new Date(currentPeriodYear.value.getFullYear(), 11, 31));

function countWorkingDays(startDate: Date, endDate: Date): number {
  let count = 0;
  let currentDate = new Date(startDate);

  if (!endDate) {
    const day = currentDate.getDay();
    if (day !== 0 && day !== 6) {
      return 1;
    }
  }

  while (currentDate <= endDate) {
    const day = currentDate.getDay();
    if (day !== 0 && day !== 6) {
      count++;
    }
    currentDate.setDate(currentDate.getDate() + 1);
  }

  return count;
}

watch(currentPeriodYear, (newDate) => {
  const year = newDate.getFullYear();
  // resetea los periodos cuando cambia el anio
  remainingVacationDays.value = vacationDays.value.data?.days ?? 0;
  vacationPeriods.value = [];
  currentVacationPeriods.value = [new Date(year, 0, 1), new Date(year, 0, 2)]
});


function addPeriod() {
  if (currentVacationPeriods.value) {
    const beginDate = currentVacationPeriods.value[0]
    const endDate = currentVacationPeriods.value[1]
    const workingDays = countWorkingDays(beginDate, endDate);
    if (remainingVacationDays.value - workingDays < 0) {
      toast.error("Periodo de Vacaciones Invalido",
        { description: "El periodo seleccionado supera la cantidad de dias de vacaciones disponibles" })

      return
    }
    vacationPeriods.value.push({ beginDate: beginDate, endDate: endDate ?? beginDate, takenDays: workingDays })
    remainingVacationDays.value -= workingDays;
  }
}

function saveVacations() {
  if (vacationPeriods.value.length === 0) {
    toast.error("Error al guardar", { description: "Debes seleccionar al menos un periodo de vacaciones" })
    return
  }

  if (remainingVacationDays.value > 0) {
    toast.error("Error al guardar", { description: "Debes agotar todos los dias de descanso disponibles" })
    return
  }

  createVacationsMutation({periods: vacationPeriods.value})
}

const { mutate: createVacationsMutation } = useMutation({
  mutation: (vacationsData: CreateVacationsPayload) => updateVacations(vacationsData, employee.value?.id ?? '', 
      currentPeriodYear.value.getFullYear()),
  onError(error) {
    console.error(error);
    toast.error('Ocurrió un error al actualizar las vacaciones', {
      description: error.message,
    });
  },
  onSuccess() {
    toast.success('Vacaciones actualizadas correctamente');
    navigateTo('/perfil');
  },
});

</script>
