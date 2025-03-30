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
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-semibold">Cirugías Asignadas</h2>
          <router-link
            :to="`/consultas/cirugia/crear/${initialValues.consult.id}`"
          >
            <Button
              icon="pi pi-plus"
              label="Crear Cirugía"
              severity="success"
              outlined
            />
          </router-link>
        </div>

        <DataTable
          :value="initialValues.surgerysConsult"
          tableStyle="min-width: 30rem"
          stripedRows
        >
          <Column header="Nombre de Cirugía">
            <template #body="slotProps">
              {{ slotProps.data.surgeryType.type }}
            </template>
          </Column>

          <Column header="Médicos Responsables">
            <template #body="slotProps">
              <ul class="list-disc pl-4 text-sm">
                <li
                  v-for="emp in slotProps.data.surgeryEmployees"
                  :key="emp.specialistEmployeeId || emp.employeeId"
                >
                  {{ emp.employeeName }} {{ emp.employeeLastName }}
                </li>
              </ul>
            </template>
          </Column>

          <Column header="Estado">
            <template #body="slotProps">
              <Tag
                :value="
                  slotProps.data.performedDate !== null
                    ? 'Realizada'
                    : 'Pendiente'
                "
                :severity="
                  slotProps.data.performedDate !== null ? 'success' : 'warn'
                "
              />
            </template>
          </Column>

          <Column header="Costo Total">
            <template #body="slotProps">
              Q{{ slotProps.data.surgeryCost.toFixed(2) }}
            </template>
          </Column>

          <Column header="Acciones">
            <template #body="slotProps">
              <div class="flex gap-2 flex-wrap">
                <RouterLink :to="`/cirugias/${slotProps.data.id}`">
                  <Button
                    icon="pi pi-eye"
                    text
                    severity="info"
                    :pt="{ root: { title: 'Ver Detalles' } }"
                  />
                </RouterLink>

                <RouterLink :to="`/cirugias/editar/${slotProps.data.id}`">
                  <Button
                    v-if="slotProps.data.performedDate === null"
                    icon="pi pi-pencil"
                    text
                    severity="warning"
                    :pt="{ root: { title: 'Editar Cirugía' } }"
                  />
                </RouterLink>
                <Button
                  v-if="slotProps.data.performedDate === null"
                  icon="pi pi-trash"
                  text
                  severity="danger"
                  @click="eliminarCirugia(slotProps.data)"
                  :pt="{ root: { title: 'Eliminar Cirugía' } }"
                />
                <Button
                  v-if="slotProps.data.performedDate === null"
                  icon="pi pi-check"
                  text
                  severity="success"
                  @click="marcarComoRealizada(slotProps.data)"
                  :pt="{ root: { title: 'Marcar como Realizada' } }"
                />
              </div>
            </template>
          </Column>
        </DataTable>
      </div>
    </div>

    <!-- Dialogo Confirmar Internado -->
    <Dialog
      v-model:visible="showInternadoDialog"
      modal
      header="Seleccionar Habitación"
    >
      <template #default>
        <div v-if="!habitacionSeleccionada">
          <div class="flex justify-between items-center mb-4 gap-4">
            <div>
              <DataTable
                :value="stateRooms.data as any[]"
                tableStyle="min-width: 50rem"
                stripedRows
                :loading="roomsAsyncStatus == 'loading'"
              >
                <template #header>
                  <div
                    class="flex flex-wrap items-center justify-between gap-2"
                  >
                    <span class="text-xl font-bold">Habitaciones</span>
                    <Button
                      icon="pi pi-refresh"
                      label="Recargar"
                      @click="recargarHabitaciones"
                      rounded
                      outlined
                      severity="help"
                    />
                  </div>
                </template>
                <Column header="Numero de habitación">
                  <template #body="slotProps">
                    <template v-if="slotProps.data.number !== null">
                      {{ `${slotProps.data.number}` }}
                    </template>
                    <template v-else>
                      {{ `error` }}
                    </template>
                  </template>
                </Column>

                <Column header="Costo de mantenimiento diario">
                  <template #body="slotProps">
                    <template
                      v-if="slotProps.data.dailyMaintenanceCost !== null"
                    >
                      {{ `Q${slotProps.data.dailyMaintenanceCost}` }}
                    </template>
                    <template v-else>
                      {{ `error` }}
                    </template>
                  </template>
                </Column>

                <Column header="Precio diario">
                  <template #body="slotProps">
                    <template v-if="slotProps.data.dailyPrice !== null">
                      {{ `Q${slotProps.data.dailyPrice}` }}
                    </template>
                    <template v-else>
                      {{ `error` }}
                    </template>
                  </template>
                </Column>

                <Column header="Estado">
                  <template #body="slotProps">
                    <Tag :value="slotProps.data.status" />
                  </template>
                </Column>

                <!--Botones de acciones-->
                <Column header="Acciones">
                  <template #body="slotProps">
                    <Button
                      label="Seleccionar"
                      severity="success"
                      rounded
                      text
                      @click="habitacionSeleccionada = slotProps.data"
                    />
                  </template>
                </Column>
                <template #footer>
                  Hay en total
                  {{ stateRooms.data ? (stateRooms.data as any[]).length : 0 }}
                  habitaciones.
                </template>
              </DataTable>
            </div>
          </div>
        </div>
        <div v-else>
          <h3 class="text-xl font-bold mb-4">Habitación Seleccionada</h3>
          <div class="bg-gray-100 p-4 rounded">
            <p><strong>Número:</strong> {{ habitacionSeleccionada.number }}</p>
            <p>
              <strong>Precio Diario:</strong> Q{{
                habitacionSeleccionada.dailyPrice?.toFixed(2) || "0.00"
              }}
            </p>
            <p>
              <strong>Mantenimiento Diario:</strong> Q{{
                habitacionSeleccionada.dailyMaintenanceCost?.toFixed(2) ||
                "0.00"
              }}
            </p>
          </div>
          <Button
            class="mt-4"
            label="Eliminar Selección"
            icon="pi pi-times"
            outlined
            severity="danger"
            @click="habitacionSeleccionada = null"
          />
        </div>
      </template>
      <template #footer>
        <Button
          label="Cancelar"
          text
          @click="
            () => {
              habitacionSeleccionada = null;
              showInternadoDialog = false;
            }
          "
        />
        <Button
          label="Continuar"
          severity="warning"
          @click="() => (showConfirmInternadoDialog = true)"
          :disabled="!habitacionSeleccionada"
        />
      </template>
    </Dialog>

    <Dialog
      v-model:visible="showConfirmInternadoDialog"
      modal
      header="Confirmar Internado"
    >
      <p>Estas seguro que deseas convertir esta consulta en internado?</p>
      <template #footer>
        <Button
          label="Cancelar"
          text
          @click="showConfirmInternadoDialog = false"
        />
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
    <!-- Diálogo Confirmar Eliminación -->
    <Dialog
      v-model:visible="showEliminarDialog"
      modal
      header="Confirmar Eliminación"
    >
      <p>¿Deseas eliminar esta cirugía?</p>
      <template #footer>
        <Button label="Cancelar" text @click="showEliminarDialog = false" />
        <Button
          label="Eliminar"
          severity="danger"
          @click="confirmarEliminarCirugia"
        />
      </template>
    </Dialog>

    <!-- Diálogo Confirmar Realización -->
    <Dialog
      v-model:visible="showRealizadaDialog"
      modal
      header="Confirmar Acción"
    >
      <p>¿Marcar esta cirugía como realizada?</p>
      <template #footer>
        <Button label="Cancelar" text @click="showRealizadaDialog = false" />
        <Button
          label="Marcar como Realizada"
          severity="success"
          @click="confirmarMarcarComoRealizada"
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
  markConsultAsInternado,
  updateConsult,
  type AddDeleteEmployeeConsultRequestDTO,
  type ConsultResponseDTO,
  type EmployeeConsultResponseDTO,
  type MarkConsultAsInternadoDTO,
  type UpdateConsultRequestDTO,
} from "~/lib/api/consults/consult";
import { Tag, Button, Dialog } from "primevue";
import {
  deleteSurgery,
  getSurgeriesbyConsultId,
  markAsCompletedSurgery,
  type SurgeryResponseDTO,
} from "../../lib/api/surgeries/surgeries";
import { getAllAvailableRooms } from "~/lib/api/habitaciones/room";

const showInternadoDialog = ref(false);
const showConfirmInternadoDialog = ref(false);
const showDeleteDialog = ref(false);
const empleadoAEliminar = ref<string | null>(null);
const showEliminarDialog = ref(false);
const showRealizadaDialog = ref(false);
const cirugiaSeleccionada = ref<any | null>(null);
const habitacionSeleccionada = ref<any | null>(null);

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
  state: stateRooms,
  refetch: refetchRooms,
  asyncStatus: roomsAsyncStatus,
} = useCustomQuery({
  key: ["rooms-available"],
  query: () => getAllAvailableRooms(),
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

const recargarCirugiasAsignadas = () => {
  refetchConsultSurgeries();
};

const recargarHabitaciones = () => {
  refetchRooms();
};

const recargarConsulta = () => {
  refecthConsult();
  recargarEmpleadosAsignados();
  recargarCirugiasAsignadas();
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
  if (!habitacionSeleccionada.value) {
    toast.error("Selecciona una habitación primero");
    showConfirmInternadoDialog.value = false;
  }
  const payload = {
    consultId: useRoute().params.id as string,
    roomId: habitacionSeleccionada.value.id,
  } as MarkConsultAsInternadoDTO;
  markAsInternadoMutate(payload);
  habitacionSeleccionada.value = null;
  showInternadoDialog.value = false;
  showConfirmInternadoDialog.value = false;
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

const {
  mutate: markAsInternadoMutate,
  asyncStatus: asyncMarkAsInternadoStatus,
} = useMutation({
  mutation: (payload: MarkConsultAsInternadoDTO) =>
    markConsultAsInternado(payload),
  onError: (error) => {
    toast.error("Ocurrió un error al convertir la consulta en internado", {
      description: error.message,
    });
  },
  onSuccess: () => {
    toast.success("Consulta convertida a internado correctamente");
    recargarConsulta();
  },
});

const { mutate: deleteSurgeryMutate, asyncStatus: asyncDeleteSurgeryStatus } =
  useMutation({
    mutation: (surgeryId: string) => deleteSurgery(surgeryId),
    onError: (error) => {
      toast.error("Ocurrió un error al eliminar la cirugía", {
        description: error.message,
      });
    },
    onSuccess: () => {
      toast.success("Cirugía eliminada correctamente");
      recargarConsulta();
    },
  });

const { mutate: markAsPerformed, asyncStatus: asyncMarkAsPerformedStatus } =
  useMutation({
    mutation: (surgeryId: string) => markAsCompletedSurgery(surgeryId),
    onError: (error) => {
      toast.error("Ocurrió un error al marcar la cirugía como realizada", {
        description: error.message,
      });
    },
    onSuccess: () => {
      toast.success("Cirugía marcada como realizada correctamente");
      recargarConsulta();
    },
  });

const eliminarCirugia = (cirugia: any) => {
  cirugiaSeleccionada.value = cirugia;
  showEliminarDialog.value = true;
};

const confirmarEliminarCirugia = () => {
  if (!cirugiaSeleccionada.value) return;
  deleteSurgeryMutate(cirugiaSeleccionada.value.id);
  showEliminarDialog.value = false;
  cirugiaSeleccionada.value = null;
};

const marcarComoRealizada = (cirugia: any) => {
  cirugiaSeleccionada.value = cirugia;
  showRealizadaDialog.value = true;
};

const confirmarMarcarComoRealizada = () => {
  if (!cirugiaSeleccionada.value) return;
  markAsPerformed(cirugiaSeleccionada.value.id);
  showRealizadaDialog.value = false;
  cirugiaSeleccionada.value = null;
};
</script>
