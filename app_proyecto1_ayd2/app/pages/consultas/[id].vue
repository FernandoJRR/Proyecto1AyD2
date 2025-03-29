<template>
  <div class="m-6 ml-12 text-black">
    <router-link to="/consultas">
      <Button label="Ver Consultas" icon="pi pi-arrow-left" text />
    </router-link>

    <!-- Botón para convertir en internado -->
    <div class="mt-4 flex gap-4 flex-wrap">
      <Button
        label="Convertir en Internado"
        icon="pi pi-user-plus"
        severity="warning"
        outlined
        @click="showInternadoDialog = true"
        :disabled="initialValues.consult.isInternado"
      />

      <Button
        v-if="initialValues.consult.isInternado"
        label="Asignar Habitación"
        icon="pi pi-home"
        severity="info"
        outlined
        @click="toast('Abrir diálogo para asignar habitación')"
      />
    </div>

    <div v-if="consultState.status === 'pending'" class="mt-4">Cargando...</div>

    <div v-else-if="consultState.status === 'error'" class="mt-4 text-red-500">
      Ocurrió un error inesperado
    </div>

    <div
      v-else
      class="bg-white p-6 rounded-xl shadow border border-gray-200 mt-4 w-full"
    >
      <div class="flex items-center justify-between mb-4">
        <h1 class="text-2xl font-semibold">Detalle de Consulta</h1>
        <Tag :value="`ID: ${initialValues.consult.id}`" severity="info" />
      </div>

      <!-- Tags de estado -->
      <div class="flex gap-2 mb-4">
        <Tag
          :value="initialValues.consult.isPaid ? 'Pagado' : 'No Pagado'"
          :severity="initialValues.consult.isPaid ? 'success' : 'warn'"
        />
        <Tag
          :value="initialValues.consult.isInternado ? 'Internado' : 'Consulta'"
          :severity="initialValues.consult.isInternado ? 'danger' : 'info'"
        />
      </div>

      <p>
        <strong>Paciente:</strong>
        {{ initialValues.consult.patient.firstnames }}
        {{ initialValues.consult.patient.lastnames }}
      </p>
      <p><strong>DPI:</strong> {{ initialValues.consult.patient.dpi }}</p>
      <p>
        <strong>Costo Consulta:</strong> Q{{
          initialValues.consult.costoConsulta
        }}
      </p>
      <p>
        <strong>Costo Total:</strong> Q{{ initialValues.consult.costoTotal }}
      </p>
      <p>
        <strong>Fecha de Creación:</strong>
        {{ initialValues.consult.createdAt }}
      </p>
      <p>
        <strong>Última Actualización:</strong>
        {{ initialValues.consult.updateAt }}
      </p>

      <!-- Tabla de empleados asignados -->
      <div class="mt-8">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-semibold">Empleados Asignados</h2>
          <div class="flex gap-2">
            <Button
              icon="pi pi-plus"
              label="Agregar Empleado"
              severity="success"
              rounded
              outlined
              :disabled="!initialValues.consult.isInternado"
              @click="() => toast('Abrir diálogo para agregar empleado')"
            />
            <Button
              icon="pi pi-refresh"
              label="Recargar"
              @click="recargarEmpleadosAsignados"
              rounded
              outlined
              severity="help"
            />
          </div>
        </div>

        <DataTable
          :value="initialValues.consultEmployees as any[]"
          tableStyle="min-width: 30rem"
          stripedRows
          :loading="asyncConsultEmployeeStatus === 'loading'"
        >
          <Column field="employeeName" header="Nombre" />
          <Column field="employeeLastName" header="Apellido" />
          <Column field="employeeType" header="Tipo de Empleado" />

          <Column header="Acciones">
            <template #body="slotProps">
              <Button
                label="Eliminar"
                severity="danger"
                rounded
                text
                @click="openDeleteDialog(slotProps.data.employeeId)"
              />
            </template>
          </Column>

          <template #footer>
            Hay en total
            {{ initialValues.consultEmployees?.length ?? 0 }} empleados
            asignados.
          </template>
        </DataTable>
      </div>

      <!-- Tabla de cirugías asignadas -->
      <div v-if="initialValues.consult.isInternado" class="mt-12">
        <h2 class="text-xl font-semibold mb-4">Cirugías Asignadas</h2>
        <DataTable :value="[]" tableStyle="min-width: 30rem" stripedRows>
          <Column field="surgeryName" header="Nombre de Cirugía" />
          <Column field="scheduledDate" header="Fecha Programada" />
          <Column field="responsibleDoctor" header="Doctor Responsable" />
        </DataTable>
      </div>
    </div>

    <!-- Dialogo Confirmar Internado -->
    <Dialog
      v-model:visible="showInternadoDialog"
      modal
      header="Confirmar acción"
    >
      <p>¿Estás seguro que deseas convertir esta consulta en un internado?</p>
      <template #footer>
        <Button label="Cancelar" text @click="showInternadoDialog = false" />
        <Button
          label="Confirmar"
          severity="warning"
          @click="confirmarInternado"
        />
      </template>
    </Dialog>

    <!-- Dialogo Confirmar Eliminación -->
    <Dialog v-model:visible="showDeleteDialog" modal header="Eliminar Empleado">
      <p>¿Deseas eliminar este empleado de la consulta?</p>
      <template #footer>
        <Button label="Cancelar" text @click="showDeleteDialog = false" />
        <Button
          label="Eliminar"
          severity="danger"
          @click="confirmarEliminacionEmpleado"
        />
      </template>
    </Dialog>
  </div>
</template>

<script lang="ts" setup>
import { toast } from "vue-sonner";
import { reactive, ref, watch } from "vue";
import {
  deleteEmployeeFromConsult,
  employeesConsult,
  getConsult,
  updateConsult,
  type AddDeleteEmployeeConsultRequestDTO,
  type ConsultResponseDTO,
  type EmployeeConsultResponseDTO,
  type UpdateConsultRequestDTO,
} from "~/lib/api/consults/consult";
import { Tag, Button, Dialog } from "primevue";
import { getSurgeriesbyConsultId } from "../../lib/api/surgeries/surgeries";

const showInternadoDialog = ref(false);
const showDeleteDialog = ref(false);
const empleadoAEliminar = ref<string | null>(null);

const initialValues = reactive({
  consult: {
    costoConsulta: 0,
    costoTotal: 0,
    createdAt: "",
    id: "",
    isInternado: false,
    isPaid: false,
    patient: {
      dpi: "",
      firstnames: "",
      id: "",
      lastnames: "",
    },
    updateAt: "",
  } as ConsultResponseDTO,
  consultEmployees: [] as EmployeeConsultResponseDTO[],
  surgerysConsult: [] as SurgeryResponseDTO[],
});

const {
  state: consultEmployeeState,
  refetch: refetchConsultEmployee,
  asyncStatus: asyncConsultEmployeeStatus,
} = useQuery({
  key: ["consultEmployees", useRoute().params.id as string],
  query: () => employeesConsult(useRoute().params.id as string),
});

const { state: consultState, refetch: refecthConsult } = useQuery({
  key: ["consult", useRoute().params.id as string],
  query: () => getConsult(useRoute().params.id as string),
});

const { state: consultSurgeriesState, refetch: refetchConsultSurgeries } =
  useQuery({
    key: ["consultSurgeries", useRoute().params.id as string],
    query: () => getSurgeriesbyConsultId(useRoute().params.id as string),
  });

watch(
  () => consultSurgeriesState.value,
  (value) => {
    if (consultSurgeriesState.value.status === "success" && value.data) {
      initialValues.surgerysConsult = value.data;
    }
  },
  { immediate: true }
);

watch(
  () => consultState.value,
  (value) => {
    if (consultState.value.status === "success" && value.data) {
      initialValues.consult = value.data;
    }
  },
  { immediate: true }
);

watch(
  () => consultEmployeeState.value,
  (value) => {
    if (consultEmployeeState.value.status === "success" && value.data) {
      initialValues.consultEmployees = value.data;
    }
  },
  { immediate: true }
);

const recargarEmpleadosAsignados = () => {
  refetchConsultEmployee();
};

const recargarConsulta = () => {
  refecthConsult();
  recargarEmpleadosAsignados();
};

const openDeleteDialog = (id: string) => {
  empleadoAEliminar.value = id;
  showDeleteDialog.value = true;
};

const confirmarEliminacionEmpleado = () => {
  if (!empleadoAEliminar.value) return;
  const deletePayload = {
    consultId: useRoute().params.id as string,
    employeeId: empleadoAEliminar.value,
  } as AddDeleteEmployeeConsultRequestDTO;
  deleteEmployee(deletePayload);
  showDeleteDialog.value = false;
};

const confirmarInternado = () => {
  const payload = {
    costoConsulta: null,
    isInternado: true,
  } as UpdateConsultRequestDTO;
  updateConsultMutation(payload);
  showInternadoDialog.value = false;
};

const { mutate: deleteEmployee, asyncStatus: asyncDeleteEmployeeStatus } =
  useMutation({
    mutation: (payload: AddDeleteEmployeeConsultRequestDTO) =>
      deleteEmployeeFromConsult(payload),
    onError: (error) => {
      toast.error("Ocurrió un error al eliminar el empleado", {
        description: error.message,
      });
    },
    onSuccess: () => {
      recargarEmpleadosAsignados();
    },
  });

const { mutate: updateConsultMutation, asyncStatus: asyncUpdateConsultStatus } =
  useMutation({
    mutation: (payload: UpdateConsultRequestDTO) =>
      updateConsult(useRoute().params.id as string, payload),
    onError: (error) => {
      toast.error("Ocurrió un error al actualizar la consulta", {
        description: error.message,
      });
    },
    onSuccess: () => {
      toast.success("Consulta actualizada correctamente");
      recargarConsulta();
    },
  });
</script>
