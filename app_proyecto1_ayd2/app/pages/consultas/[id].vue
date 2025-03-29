<template>
  <div class="m-6 ml-12 text-black">
    <router-link to="/consultas">
      <Button label="Ver Consultas" icon="pi pi-arrow-left" text />
    </router-link>

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
          <Button
            icon="pi pi-refresh"
            label="Recargar"
            @click="recargarEmpleadosAsignados"
            rounded
            outlined
            severity="help"
          />
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
                @click="eliminarEmpleado(slotProps.data.employeeId)"
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
    </div>
  </div>
</template>

<script lang="ts" setup>
import { toast } from "vue-sonner";
import { reactive, ref, watch } from "vue";
import {
  deleteEmployeeFromConsult,
  employeesConsult,
  getConsult,
  type AddDeleteEmployeeConsultRequestDTO,
  type ConsultResponseDTO,
  type EmployeeConsultResponseDTO,
} from "~/lib/api/consults/consult";
import { Tag, Button } from "primevue";

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
});

const {
  state: consultEmployeeState,
  refetch: refetchConsultEmployee,
  asyncStatus: asyncConsultEmployeeStatus,
} = useQuery({
  key: ["consultEmployees", useRoute().params.id as string],
  query: () => employeesConsult(useRoute().params.id as string),
});

const { state: consultState } = useQuery({
  key: ["consult", useRoute().params.id as string],
  query: () => getConsult(useRoute().params.id as string),
});

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

const eliminarEmpleado = (id: string) => {
  const deletePayload = {
    consultId: useRoute().params.id as string,
    employeeId: id,
  } as AddDeleteEmployeeConsultRequestDTO;
  deleteEmployee(deletePayload);
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
</script>
