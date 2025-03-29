<template>
  <div class="m-2">
    <div class="mb-6">
      <Button
        label="Volver"
        icon="pi pi-arrow-left"
        text
        @click="router.back()"
      />
    </div>
    <!-- Tabla de tipos de cirugía -->
    <div v-if="!tipoCirugiaSeleccionado">
      <h1 class="text-black mb-4 text-2xl">Tipos de Cirugías</h1>
      <div class="flex justify-between items-center mb-4 gap-4">
        <div class="flex items-center gap-2">
          <InputText
            v-model="searchTipoCirugia"
            placeholder="Buscar por tipo..."
            class="p-inputtext-sm w-64"
            @keyup.enter="buscarTiposCirugia"
          />
          <Button
            icon="pi pi-search"
            label="Buscar"
            @click="buscarTiposCirugia"
            rounded
            outlined
            severity="info"
          />
        </div>

        <div class="flex items-center gap-2">
          <Button
            icon="pi pi-refresh"
            label="Recargar Tabla"
            @click="buscarTiposCirugia"
            rounded
            outlined
            severity="help"
          />
        </div>
      </div>

      <DataTable
        :value="surgeryTypesState.data as any[]"
        tableStyle="min-width: 50rem"
        stripedRows
        :loading="asyncSurgeryTypesStatus === 'loading'"
        class="mb-10"
      >
        <template #header>
          <div class="flex flex-wrap items-center justify-between gap-2">
            <span class="text-xl font-bold">Tipos de Cirugía</span>
          </div>
        </template>

        <Column field="id" header="ID" />
        <Column field="type" header="Tipo" />
        <Column field="description" header="Descripción" />
        <Column field="specialistPayment" header="Pago Especialista" />
        <Column field="hospitalCost" header="Costo Hospital" />
        <Column field="surgeryCost" header="Costo Total" />

        <Column header="Acciones">
          <template #body="slotProps">
            <Button
              label="Seleccionar"
              severity="success"
              rounded
              text
              @click="seleccionarTipoCirugia(slotProps.data)"
            />
          </template>
        </Column>

        <template #footer>
          Hay en total {{ surgeryTypesState.data?.length ?? 0 }} tipos de
          cirugía.
        </template>
      </DataTable>
    </div>

    <div v-else class="mb-10">
      <h2 class="text-xl font-semibold mb-4 text-black">
        Tipo de Cirugía Seleccionado
      </h2>
      <div
        class="bg-white p-6 rounded-xl shadow border border-gray-200 mt-4 w-full"
      >
        <p><strong>ID:</strong> {{ tipoCirugiaSeleccionado.id }}</p>
        <p><strong>Tipo:</strong> {{ tipoCirugiaSeleccionado.type }}</p>
        <p>
          <strong>Descripción:</strong>
          {{ tipoCirugiaSeleccionado.description }}
        </p>
        <p>
          <strong>Pago Especialista:</strong> Q{{
            tipoCirugiaSeleccionado.specialistPayment
          }}
        </p>
        <p>
          <strong>Costo Hospital:</strong> Q{{
            tipoCirugiaSeleccionado.hospitalCost
          }}
        </p>
        <p>
          <strong>Costo Total:</strong> Q{{
            tipoCirugiaSeleccionado.surgeryCost
          }}
        </p>
        <Button
          class="mt-4"
          icon="pi pi-times"
          label="Quitar Selección"
          severity="danger"
          @click="quitarSeleccionTipoCirugia"
        />
      </div>
    </div>

    <!-- Sección original de selección de personas -->
    <div v-if="!personaSeleccionada">
      <div>
        <h1 class="text-black mb-2 text-2xl">Médicos Especialistas</h1>
        <div class="flex justify-between items-center mb-4 gap-4">
          <div class="flex items-center gap-2">
            <InputText
              v-model="searchTerm"
              placeholder="Buscar por nombre..."
              class="p-inputtext-sm w-64"
              @keyup.enter="buscarEspecialistas"
            />
            <Button
              icon="pi pi-search"
              label="Buscar"
              @click="buscarEspecialistas"
              rounded
              outlined
              severity="info"
            />
          </div>

          <div class="flex items-center gap-2">
            <Button
              icon="pi pi-refresh"
              label="Recargar Tabla"
              @click="recargarDatos"
              rounded
              outlined
              severity="help"
            />
          </div>
        </div>
        <DataTable
          :value="especialistasState.data as any[]"
          tableStyle="min-width: 50rem"
          stripedRows
          :loading="asyncStatus === 'loading'"
          class="mb-10"
        >
          <template #header>
            <div class="flex flex-wrap items-center justify-between gap-2">
              <span class="text-xl font-bold">Lista de Especialistas</span>
            </div>
          </template>

          <Column field="id" header="ID" />
          <Column field="nombres" header="Nombres" />
          <Column field="apellidos" header="Apellidos" />
          <Column field="dpi" header="DPI" />

          <Column header="Acciones">
            <template #body="slotProps">
              <Button
                label="Seleccionar"
                severity="success"
                rounded
                text
                @click="seleccionarPersona(slotProps.data, true)"
              />
            </template>
          </Column>

          <template #footer>
            Hay en total
            {{
              especialistasState.data
                ? (especialistasState.data as any[]).length
                : 0
            }}
            especialistas.
          </template>
        </DataTable>
      </div>

      <div>
        <h1 class="text-black mt-12 mb-2 text-2xl">Doctores</h1>

        <div class="flex justify-between items-center mb-4 gap-4">
          <div class="flex items-center gap-2">
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

          <div class="flex items-center gap-2">
            <Button
              icon="pi pi-refresh"
              label="Recargar Tabla"
              @click="buscarDoctores"
              rounded
              outlined
              severity="help"
            />
          </div>
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
                @click="seleccionarPersona(slotProps.data, false)"
              />
            </template>
          </Column>
          <template #footer>
            Hay en total {{ doctorsState.data?.length ?? 0 }} doctores
            registrados.
          </template>
        </DataTable>
      </div>
    </div>

    <div v-else class="mt-6">
      <h2 class="text-xl font-semibold mb-4 text-black">
        Persona Seleccionada
      </h2>
      <div
        class="bg-white p-6 rounded-xl shadow border border-gray-200 mt-4 w-full"
      >
        <p>
          <strong>Tipo:</strong>
          {{ personaSeleccionada.isSpecialist ? "Especialista" : "Doctor" }}
        </p>
        <p>
          <strong>Nombre:</strong>
          {{
            (personaSeleccionada.data as any).nombres ||
            (personaSeleccionada.data as any).firstName
          }}
          {{
            (personaSeleccionada.data as any).apellidos ||
            (personaSeleccionada.data as any).lastName
          }}
        </p>
        <p v-if="(personaSeleccionada.data as any).dpi">
          <strong>DPI:</strong> {{ (personaSeleccionada.data as any).dpi }}
        </p>
        <p><strong>ID:</strong> {{ personaSeleccionada.data.id }}</p>
        <Button
          class="mt-4"
          icon="pi pi-times"
          label="Quitar Selección"
          severity="danger"
          @click="quitarSeleccionPersona"
        />
      </div>
    </div>
    <div
      v-if="personaSeleccionada && tipoCirugiaSeleccionado"
      class="mb-6 mt-6 w-full"
    >
      <Button
        class="w-full"
        label="Crear Cirugía"
        icon="pi pi-plus"
        severity="success"
        @click="crearCirugia"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { toast } from "vue-sonner";

import { Button, InputText } from "primevue";
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { getDoctors, type Employee } from "~/lib/api/admin/employee";
import {
  getAllSpecialistEmployees,
  type SpecialistEmployeeResponseDTO,
} from "~/lib/api/admin/specialist-employee";
import {
  createSurgery,
  getSurgeriesTypes,
  type CreateSurgeryRequestDTO,
  type SurgeryTypeResponseDTO,
} from "~/lib/api/surgeries/surgeries";

const router = useRouter();

const searchTerm = ref("");
const searchDoctor = ref("");
const searchTipoCirugia = ref("");

const personaSeleccionada = ref<{
  data: Employee | SpecialistEmployeeResponseDTO;
  isSpecialist: boolean;
} | null>(null);
const tipoCirugiaSeleccionado = ref<SurgeryTypeResponseDTO | null>(null);

const {
  state: doctorsState,
  asyncStatus: asyncDoctorsStatus,
  refetch: refetchDoctors,
} = useCustomQuery({
  key: ["doctors-consult"],
  query: () =>
    getDoctors(searchDoctor.value === "" ? null : searchDoctor.value),
});

const {
  state: especialistasState,
  asyncStatus,
  refetch: refetchEspecialistas,
} = useCustomQuery({
  key: ["especialistas-cirugia-crear"],
  query: () =>
    getAllSpecialistEmployees(
      searchTerm.value === "" ? null : searchTerm.value
    ),
});

const {
  state: surgeryTypesState,
  asyncStatus: asyncSurgeryTypesStatus,
  refetch: refetchSurgeryTypes,
} = useCustomQuery({
  key: ["surgery-types-selector"],
  query: () =>
    getSurgeriesTypes(
      searchTipoCirugia.value === "" ? null : searchTipoCirugia.value
    ),
});

const { mutate: createSugeryMutate, asyncStatus: asyncCreateSurgeryStatus } =
  useMutation({
    mutation: (payload: CreateSurgeryRequestDTO) => createSurgery(payload),
    onSuccess: () => {
      toast.success("Cirugía creada exitosamente.");
      router.back();
    },
    onError: (error) => {
      toast.error("Ocurrio un error al crear la cirugía.", {
        description: error.message,
      });
    },
  });

const buscarDoctores = () => refetchDoctors();
const buscarEspecialistas = () => refetchEspecialistas();
const buscarTiposCirugia = () => refetchSurgeryTypes();

const recargarDatos = () => {
  refetchEspecialistas();
};

const seleccionarPersona = (persona: any, esEspecialista: boolean) => {
  personaSeleccionada.value = {
    data: persona,
    isSpecialist: esEspecialista,
  };
};

const quitarSeleccionPersona = () => {
  personaSeleccionada.value = null;
};

const seleccionarTipoCirugia = (tipo: any) => {
  tipoCirugiaSeleccionado.value = tipo;
};

const quitarSeleccionTipoCirugia = () => {
  tipoCirugiaSeleccionado.value = null;
};

const crearCirugia = () => {
  // Verificamos que se haya seleccionado un tipo de cirugía y una persona
  if (!tipoCirugiaSeleccionado.value || !personaSeleccionada.value) {
    toast.error("Por favor, selecciona un tipo de cirugía y una persona.");
    return;
  }
  const payload = {
    consultId: useRoute().params.id as string,
    surgeryTypeId: tipoCirugiaSeleccionado.value?.id,
    asignedDoctorId: personaSeleccionada.value?.isSpecialist
      ? (personaSeleccionada.value.data as SpecialistEmployeeResponseDTO).id
      : (personaSeleccionada.value.data as Employee).id,
    isSpecialist: personaSeleccionada.value.isSpecialist,
  } as CreateSurgeryRequestDTO;
  createSugeryMutate(payload);
};
</script>
