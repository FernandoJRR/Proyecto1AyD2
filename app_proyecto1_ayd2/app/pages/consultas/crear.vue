<template>
  <div class="m-2">
    <h1 class="text-4xl font-bold mb-6">Crear Consulta</h1>

    <div v-if="!pacienteSeleccionado">
      <h1 class="text-black mb-2 text-2xl">Pacientes</h1>
      <div class="flex justify-between items-center mb-4 gap-4">
        <div class="flex items-center gap-2">
          <InputText
            v-model="searchTerm"
            placeholder="Buscar por nombre..."
            class="p-inputtext-sm w-64"
            @keyup.enter="buscarPacientes"
          />
          <Button
            icon="pi pi-search"
            label="Buscar"
            @click="buscarPacientes"
            rounded
            outlined
            severity="info"
          />
        </div>

        <div class="flex items-center gap-2">
          <router-link to="/pacientes/crear">
            <Button icon="pi pi-plus" label="Crear Paciente" rounded raised />
          </router-link>

          <Button
            icon="pi pi-refresh"
            label="Recargar Página"
            @click="recargarDatos"
            rounded
            outlined
            severity="help"
          />
        </div>
      </div>

      <DataTable
        :value="patientsState.data as any[]"
        tableStyle="min-width: 50rem"
        stripedRows
        :loading="asyncStatus === 'loading'"
        class="mb-10"
      >
        <template #header>
          <div class="flex flex-wrap items-center justify-between gap-2">
            <span class="text-xl font-bold">Listado de Pacientes</span>
          </div>
        </template>
        <Column field="firstnames" header="Nombres"></Column>
        <Column field="lastnames" header="Apellidos"></Column>
        <Column field="dpi" header="DPI"></Column>

        <Column header="Acciones">
          <template #body="slotProps">
            <Button
              label="Seleccionar"
              severity="success"
              rounded
              text
              @click="seleccionarPaciente(slotProps.data)"
            />
          </template>
        </Column>

        <template #footer>
          Hay en total
          {{ patientsState.data ? (patientsState.data as any[]).length : 0 }}
          pacientes registrados.
        </template>
      </DataTable>
    </div>

    <div v-else class="mt-6 text-black">
      <h2 class="text-xl font-semibold mb-2">Paciente Seleccionado:</h2>
      <p><strong>Nombres:</strong> {{ pacienteSeleccionado.firstnames }}</p>
      <p><strong>Apellidos:</strong> {{ pacienteSeleccionado.lastnames }}</p>
      <p><strong>DPI:</strong> {{ pacienteSeleccionado.dpi }}</p>
      <Button
        icon="pi pi-times"
        label="Quitar Selección"
        severity="danger"
        class="mt-4"
        @click="quitarSeleccion"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import { RouterLink } from "vue-router";
import { getAllPatients } from "~/lib/api/patients/patients";
import { InputText, Button } from "primevue";

const { user, employee } = storeToRefs(useAuthStore());

const isAdmin = computed(() => employee.value?.employeeType?.name === "Admin");
const isDoctor = computed(
  () => employee.value?.employeeType?.name === "Doctor"
);

const searchTerm = ref("");
const pacienteSeleccionado = ref<any | null>(null);

const {
  state: patientsState,
  asyncStatus,
  refetch: refetchPatients,
} = useCustomQuery({
  key: ["patients-consult"],
  query: () =>
    getAllPatients(searchTerm.value === "" ? null : searchTerm.value),
});

const buscarPacientes = () => {
  refetchPatients();
};

const recargarDatos = () => {
  refetchPatients();
};

const seleccionarPaciente = (paciente: any) => {
  pacienteSeleccionado.value = paciente;
};

const quitarSeleccion = () => {
  pacienteSeleccionado.value = null;
};
</script>
