<template>
  <div class="m-2">
    <h1 class="text-4xl font-bold mb-6">Crear Consulta</h1>

    <!-- Tabla de Pacientes -->
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
        <Column field="firstnames" header="Nombres" />
        <Column field="lastnames" header="Apellidos" />
        <Column field="dpi" header="DPI" />
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
          Hay en total {{ patientsState.data?.length ?? 0 }} pacientes
          registrados.
        </template>
      </DataTable>
    </div>

    <!-- Paciente Seleccionado -->
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

    <!-- Tabla de Doctores (solo Admin) -->
    <div v-if="isAdmin && pacienteSeleccionado">
      <div v-if="!doctorSeleccionado">
        <h1 class="text-black mt-12 mb-2 text-2xl">Doctores</h1>

        <div class="flex items-center gap-2 mb-4">
          <InputText
            v-model="searchDoctor"
            placeholder="Buscar doctor por nombre..."
            class="p-inputtext-sm w-64"
            @keyup.enter="buscarDoctores"
          />
          <Button
            icon="pi pi-search"
            label="Buscar"
            @click="buscarDoctores"
            rounded
            outlined
            severity="info"
          />
        </div>

        <DataTable
          :value="doctorsState.data as any[]"
          tableStyle="min-width: 50rem"
          stripedRows
          :loading="asyncDoctorsStatus === 'loading'"
        >
          <template #header>
            <div class="flex flex-wrap items-center justify-between gap-2">
              <span class="text-xl font-bold">Listado de Doctores</span>
            </div>
          </template>
          <Column field="firstName" header="Nombres" />
          <Column field="lastName" header="Apellidos" />
          <Column field="id" header="ID" />
          <Column header="Acciones">
            <template #body="slotProps">
              <Button
                label="Seleccionar"
                severity="success"
                rounded
                text
                @click="seleccionarDoctor(slotProps.data)"
              />
            </template>
          </Column>
          <template #footer>
            Hay en total {{ doctorsState.data?.length ?? 0 }} doctores
            registrados.
          </template>
        </DataTable>
      </div>

      <!-- Doctor Seleccionado -->
      <div v-else class="mt-6 text-black">
        <h2 class="text-xl font-semibold mb-2">Doctor Seleccionado:</h2>
        <p><strong>Nombres:</strong> {{ doctorSeleccionado.firstName }}</p>
        <p><strong>Apellidos:</strong> {{ doctorSeleccionado.lastName }}</p>
        <p><strong>ID:</strong> {{ doctorSeleccionado.id }}</p>
        <Button
          icon="pi pi-times"
          label="Quitar Selección"
          severity="danger"
          class="mt-4"
          @click="quitarSeleccionDoctor"
        />
      </div>
    </div>

    <!-- Botón Final para Crear Consulta -->
    <div v-if="puedeCrearConsulta" class="mt-10">
      <div
        class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200"
      >
        <Form
          v-slot="$form"
          :initialValues
          :resolver
          @submit="crearConsulta"
          class="mt-2 flex justify-center"
        >
          <div class="flex flex-col gap-1 w-full">
            <h1 class="text-2xl font-semibold mb-6 text-black">
              Datos del Paciente
            </h1>

            <div class="flex flex-row gap-4 mt-8">
              <div class="w-full">
                <FloatLabel>
                  <label for="costoConsulta">Costo de Consulta</label>
                  <InputNumber
                    :min="1"
                    :minFractionDigits="2"
                    :maxFractionDigits="2"
                    mode="currency"
                    currency="GTQ"
                    name="costoConsulta"
                    type="number"
                    fluid
                  />
                </FloatLabel>
                <Message
                  v-if="$form.costoConsulta?.invalid"
                  severity="error"
                  size="small"
                  variant="simple"
                >
                  {{ $form.costoConsulta.error?.message }}
                </Message>
              </div>
            </div>
            <Button
              type="submit"
              severity="secondary"
              label="Crear Consulta"
              icon="pi pi-save"
              class="mt-8"
            />
          </div>
        </Form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import { zodResolver } from "@primevue/forms/resolvers/zod";
import { z } from "zod";
import { RouterLink } from "vue-router";
import { getAllPatients } from "~/lib/api/patients/patients";
import { getDoctors } from "~/lib/api/admin/employee";
import { InputText, Button } from "primevue";
import { toast } from 'vue-sonner'
import { createConsult, type CreateConsultRequestDTO } from "~/lib/api/consults/consult";

const { employee } = storeToRefs(useAuthStore());

const isAdmin = computed(() => employee.value?.employeeType?.name === "Admin");

const searchTerm = ref("");
const searchDoctor = ref("");
const pacienteSeleccionado = ref<any | null>(null);
const doctorSeleccionado = ref<any | null>(null);

const initialValues = reactive({
  costoConsulta: 100,
});

const resolver = ref(
  zodResolver(
    z.object({
      costoConsulta: z
        .number()
        .min(0, "El costo de la consulta debe ser mayor a 0"),
    })
  )
);

const {
  state: patientsState,
  asyncStatus,
  refetch: refetchPatients,
} = useCustomQuery({
  key: ["patients-consult"],
  query: () =>
    getAllPatients(searchTerm.value === "" ? null : searchTerm.value),
});

const {
  state: doctorsState,
  asyncStatus: asyncDoctorsStatus,
  refetch: refetchDoctors,
} = useCustomQuery({
  key: ["doctors-consult"],
  query: () =>
    isAdmin.value
      ? getDoctors(searchDoctor.value === "" ? null : searchDoctor.value)
      : Promise.resolve([]),
});

const buscarPacientes = () => refetchPatients();
const recargarDatos = () => refetchPatients();
const buscarDoctores = () => refetchDoctors();

const seleccionarPaciente = (paciente: any) => {
  pacienteSeleccionado.value = paciente;
};
const quitarSeleccion = () => {
  pacienteSeleccionado.value = null;
  doctorSeleccionado.value = null;
};

const seleccionarDoctor = (doctor: any) => {
  doctorSeleccionado.value = doctor;
};
const quitarSeleccionDoctor = () => {
  doctorSeleccionado.value = null;
};

// Mostrar botón solo si:
// - Si es admin: se necesita paciente + doctor
// - Si no es admin: solo paciente
const puedeCrearConsulta = computed(() => {
  return isAdmin.value
    ? pacienteSeleccionado.value && doctorSeleccionado.value
    : pacienteSeleccionado.value;
});

const crearConsulta = () => {
  const payload: CreateConsultRequestDTO = {
    costoConsulta: initialValues.costoConsulta,
    patientId: pacienteSeleccionado.value.id,
    employeeId: isAdmin.value ? doctorSeleccionado.value.id : null,
  };
  console.log("Payload:", payload);
  
};

const { mutate: createConsultMutation, asyncStatus: asyncCreateConsultStatus } =
  useMutation({
    mutation: (consultData: CreateConsultRequestDTO) =>
      createConsult(consultData),
    onError(error) {
      console.error(error);
      toast.error("Ocurrió un error al crear la consulta", {
        description: error,
      });
    },
    onSuccess() {
      toast.success("Consulta creada con éxito");
      navigateTo("/consultas");
    },
  });
</script>
