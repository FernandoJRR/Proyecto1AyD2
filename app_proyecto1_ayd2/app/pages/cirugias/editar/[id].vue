<template>
  <div class="mt-4 ml-12 text-black">
    <div class="mb-6">
      <Button
        label="Volver"
        icon="pi pi-arrow-left"
        text
        @click="useRouter().back()"
      />
    </div>
    <div
      class="bg-white shadow rounded-xl p-6 border border-gray-200 space-y-8"
    >
      <h1 class="text-3xl font-bold">Detalle de la Cirugía</h1>

      <!-- Datos generales -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm mb-1">ID de Cirugía</label>
          <InputText
            :modelValue="initialValues.surgery.id"
            readonly
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm mb-1">Fecha de Realización</label>
          <InputText
            :modelValue="initialValues.surgery.performedDate || 'No realizada'"
            readonly
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm mb-1">Costo Hospitalario</label>
          <InputNumber
            :modelValue="initialValues.surgery.hospitalCost"
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
            :modelValue="initialValues.surgery.surgeryCost"
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
          <InputText
            :modelValue="initialValues.surgery.surgeryType.type"
            readonly
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm mb-1">Descripción</label>
          <InputText
            :modelValue="initialValues.surgery.surgeryType.description"
            readonly
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm mb-1">Pago al Especialista</label>
          <InputNumber
            :modelValue="initialValues.surgery.surgeryType.specialistPayment"
            mode="currency"
            currency="GTQ"
            locale="es-GT"
            readonly
            class="w-full"
          />
        </div>
      </div>
      <div>
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-semibold">Médicos Encargados</h2>
          <Button
            icon="pi pi-plus"
            label="Agregar Personal"
            outlined
            @click="mostrarSelector = true"
          />
        </div>

        <DataTable
          :value="initialValues.surgery.surgeryEmployees"
          tableStyle="min-width: 30rem"
          stripedRows
        >
          <Column field="employeeName" header="Nombre" />
          <Column field="employeeLastName" header="Apellido" />
          <Column field="specialistPayment" header="Pago Especialista" />
          <Column header="Acciones">
            <template #body="slotProps">
              <Button
                label="Eliminar"
                icon="pi pi-trash"
                severity="danger"
                text
                @click="confirmarEliminacionEmpleado(slotProps.data)"
              />
            </template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
  <Dialog v-model:visible="showDeleteDialog" modal header="Eliminar Empleado">
    <p>
      ¿Deseas eliminar a {{ empleadoAEliminar?.employeeName }} de esta cirugía?
    </p>
    <template #footer>
      <Button label="Cancelar" text @click="showDeleteDialog = false" />
      <Button
        label="Eliminar"
        severity="danger"
        @click="eliminarEmpleadoConfirmado"
      />
    </template>
  </Dialog>
  <Dialog
    v-model:visible="mostrarSelector"
    modal
    header="Seleccionar Personal"
    :style="{ width: '90vw' }"
  >
    <template #default>
      <div v-if="!personaSeleccionada">
        <!-- Médicos Especialistas -->
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

        <!-- Doctores -->
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

      <!-- Persona seleccionada -->
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
          <div class="flex gap-2 mt-4">
            <Button
              icon="pi pi-times"
              label="Quitar Selección"
              severity="danger"
              outlined
              @click="quitarSeleccionPersona"
            />
            <Button
              icon="pi pi-check"
              label="Agregar"
              severity="success"
              outlined
              @click="() => agregarPersonaSeleccionada()"
            />
          </div>
        </div>
      </div>
    </template>
  </Dialog>
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
  addDoctorSurgery,
  deleteDoctorSurgery,
  getSurgery,
  type AddDeleteEmployeeSurgeryDTO,
  type SurgeryResponseDTO,
} from "~/lib/api/surgeries/surgeries";

const searchTerm = ref("");
const searchDoctor = ref("");
const mostrarSelector = ref(false);
const showDeleteDialog = ref(false);
const empleadoAEliminar = ref<any | null>(null);

const personaSeleccionada = ref<{
  data: Employee | SpecialistEmployeeResponseDTO;
  isSpecialist: boolean;
} | null>(null);

const initialValues = reactive({
  surgery: {
    id: "",
    consultId: "",
    hospitalCost: 0,
    performedDate: "",
    surgeryCost: 0,
    surgeryType: {
      id: "",
      description: "",
      hospitalCost: 0,
      specialistPayment: 0,
      surgeryCost: 0,
      type: "",
    },
  } as SurgeryResponseDTO,
});

const {
  state: surgeryState,
  asyncStatus: asyncSurgeryStatus,
  refetch: refetchSurgery,
} = useQuery({
  key: ["surgery-edit", useRoute().params.id as string],
  query: () => getSurgery(useRoute().params.id as string),
});

const {
  state: doctorsState,
  asyncStatus: asyncDoctorsStatus,
  refetch: refetchDoctors,
} = useCustomQuery({
  key: ["doctors-cirugia-agregar"],
  query: () =>
    getDoctors(searchDoctor.value === "" ? null : searchDoctor.value),
});

const {
  state: especialistasState,
  asyncStatus,
  refetch: refetchEspecialistas,
} = useCustomQuery({
  key: ["especialistas-cirugia-agregar"],
  query: () =>
    getAllSpecialistEmployees(
      searchTerm.value === "" ? null : searchTerm.value
    ),
});

const { mutate: addEmployeeMutate, asyncStatus: addEmployeeStatus } =
  useMutation({
    mutation: (payload: AddDeleteEmployeeSurgeryDTO) =>
      addDoctorSurgery(payload),
    onError(error) {
      toast.error("Ocurrió un error al agregar el empleado", {
        description: error.message,
      });
    },
    onSuccess() {
      toast.success("Empleado agregado correctamente");
      refetchSurgery();
      mostrarSelector.value = false;
    },
  });

const { mutate: deleteEmployeeMutate, asyncStatus: deleteEmployeeStatus } =
  useMutation({
    mutation: (payload: AddDeleteEmployeeSurgeryDTO) =>
      deleteDoctorSurgery(payload),
    onError(error) {
      toast.error("Ocurrió un error al eliminar el empleado", {
        description: error.message,
      });
    },
    onSuccess() {
      toast.success("Empleado eliminado correctamente");
      refetchSurgery();
    },
  });

watch(
  () => surgeryState.value,
  (value) => {
    if (surgeryState.value.status === "success" && value.data) {
      initialValues.surgery = surgeryState.value.data;
    }
  },
  { immediate: true }
);

const buscarDoctores = () => refetchDoctors();
const buscarEspecialistas = () => refetchEspecialistas();

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

const eliminarEmpleadoConfirmado = () => {
  if (!empleadoAEliminar.value) return;
  const payload = {
    doctorId:
      empleadoAEliminar.value.employeeId ??
      empleadoAEliminar.value.specialistEmployeeId,
    isSpecialist: empleadoAEliminar.value.specialistEmployeeId !== null,
    surgeryId: initialValues.surgery.id,
  } as AddDeleteEmployeeSurgeryDTO;
  console.log("Eliminar empleado", payload);
  deleteEmployeeMutate(payload);
  showDeleteDialog.value = false;
};

const confirmarEliminacionEmpleado = (empleado: any) => {
  empleadoAEliminar.value = empleado;
  showDeleteDialog.value = true;
};

const agregarPersonaSeleccionada = () => {
  if (!personaSeleccionada.value) {
    toast.error("No se ha seleccionado ninguna persona");
    return;
  }
  const payload = {
    doctorId:
      (personaSeleccionada.value.data as any).id ??
      (personaSeleccionada.value.data as any).specialistEmployeeId,
    isSpecialist: personaSeleccionada.value.isSpecialist,
    surgeryId: initialValues.surgery.id,
  } as AddDeleteEmployeeSurgeryDTO;
  addEmployeeMutate(payload);
  personaSeleccionada.value = null;
  mostrarSelector.value = false;
};
</script>
