<template>
  <div class="m-2">
    <h1 class="text-black mb-4 text-2xl">Consultas</h1>

    <div class="flex flex-wrap justify-between gap-4 mb-4">
      <!-- Contenedor derecho: botones -->

      <div class="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
        <router-link to="/consultas/crear" class="w-full sm:w-auto">
          <Button
            icon="pi pi-plus"
            label="Crear Consulta"
            rounded
            raised
            class="w-full sm:w-auto"
          />
        </router-link>
        <Button
          icon="pi pi-refresh"
          label="Recargar Página"
          @click="recargarDatos"
          rounded
          outlined
          severity="help"
          class="w-full sm:w-auto"
        />
      </div>
      <!-- Contenedor izquierdo: filtros -->
      <div
        class="flex flex-col md:flex-row md:items-center gap-4 w-full md:w-auto"
      >
        <!-- Filtro por nombre/dpi -->
        <div
          class="flex flex-col sm:flex-row items-start sm:items-center gap-2"
        >
          <InputText
            v-model="searchTerm"
            placeholder="Nombres, apellidos o DPI..."
            class="p-inputtext-sm w-full sm:w-64"
            @keyup.enter="buscarConsultas"
          />
          <Button
            icon="pi pi-search"
            label="Buscar"
            @click="buscarConsultas"
            rounded
            outlined
            severity="info"
            class="w-full sm:w-auto"
          />
        </div>

        <!-- Filtro por ID de consulta -->
        <div
          class="flex flex-col sm:flex-row items-start sm:items-center gap-2"
        >
          <InputText
            v-model="idConsulta"
            placeholder="ID Consulta..."
            class="p-inputtext-sm w-full sm:w-64"
            @keyup.enter="buscarConsultas"
          />
          <Button
            icon="pi pi-search"
            label="Buscar"
            @click="buscarConsultas"
            rounded
            outlined
            severity="info"
            class="w-full sm:w-auto"
          />
        </div>

        <!-- Checkbox pagado -->
        <div class="flex items-center gap-2">
          <Checkbox
            v-model="isPaid"
            :binary="true"
            inputId="paid"
            @change="buscarConsultas"
          />
          <label for="paid">Pagado</label>
        </div>

        <!-- Checkbox internado -->
        <div class="flex items-center gap-2">
          <Checkbox
            v-model="isInternado"
            :binary="true"
            inputId="internado"
            @change="buscarConsultas"
          />
          <label for="internado">Internado</label>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
        <div class="flex flex-wrap justify-between gap-4 mb-4">
          <!-- Boton para limiar filtros -->
          <Button
            icon="pi pi-times"
            label="Limpiar Filtros"
            @click="
              () => {
                searchTerm = '';
                idConsulta = '';
                isPaid = false;
                isInternado = false;
                buscarConsultas();
              }
            "
            rounded
            outlined
            severity="warning"
            class="w-full sm:w-auto"
          />
        </div>
      </div>
    </div>

    <DataTable
      :value="consultState.data as any[]"
      tableStyle="min-width: 50rem"
      stripedRows
      :loading="asyncConsultStatus === 'loading'"
      class="mb-10"
    >
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Listado de Consultas</span>
        </div>
      </template>

      <Column
        field="id"
        header="ID Consulta"
        filter
        filterPlaceholder="Buscar por ID"
      />
      <Column
        header="Paciente"
        :filter="true"
        filterField="patient.firstnames"
        filterPlaceholder="Buscar por nombre"
      >
        <template #body="slotProps">
          {{ slotProps.data.patient.firstnames }}
          {{ slotProps.data.patient.lastnames }}
        </template>
      </Column>
      <Column
        field="patient.dpi"
        header="DPI"
        filter
        filterPlaceholder="Buscar por DPI"
      />
      <Column
        field="isInternado"
        header="Internado"
        filter
        filterPlaceholder="Sí/No"
      >
        <template #body="slotProps">
          <Tag
            v-if="slotProps.data.isInternado"
            severity="success"
            class="w-20 text-center"
          >
            Sí
          </Tag>
          <Tag v-else severity="danger" class="w-20 text-center"> No </Tag>
        </template>
      </Column>
      <Column field="isPaid" header="Pagado" filter filterPlaceholder="Sí/No">
        <template #body="slotProps">
          <Tag
            v-if="slotProps.data.isPaid"
            severity="success"
            class="w-20 text-center"
          >
            Sí
          </Tag>
          <Tag v-else severity="danger" class="w-20 text-center"> No </Tag>
        </template>
      </Column>
      <Column
        field="createdAt"
        header="Fecha de Creación"
        filter
        filterPlaceholder="YYYY-MM-DD"
      />

      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/consultas/${slotProps.data.id}`">
            <Button label="Ver" severity="info" rounded text />
          </RouterLink>
          <Button
            label="Eliminar"
            severity="danger"
            rounded
            text
            @click="eliminarConsulta(slotProps.data.id)"
          />
        </template>
      </Column>

      <template #footer>
        Hay en total {{ consultState.data?.length ?? 0 }} consultas.
      </template>
    </DataTable>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { RouterLink } from "vue-router";
import {
  getAllConsults,
  type ConsultFilterDTO,
} from "~/lib/api/consults/consult";
import { InputText, Button, Checkbox } from "primevue";

const searchTerm = ref("");
const idConsulta = ref("");
const isPaid = ref(false);
const isInternado = ref(false);

const { employee } = storeToRefs(useAuthStore());

const isAdmin = computed(() => employee.value?.employeeType?.name === "Admin");


const {
  state: consultState,
  asyncStatus: asyncConsultStatus,
  refetch: refetchConsults,
} = useCustomQuery({
  key: ["consults"],
  query: () =>
    getAllConsults({
      consultId: idConsulta.value || null,
      patientId: null,
      employeeFirstName: null,
      //Si no es admin, se filtra por el ID del empleado
      employeeId: isAdmin.value ? null : employee.value?.id || null,
      employeeLastName: null,
      patientDpi: searchTerm.value || null,
      patientFirstnames: searchTerm.value || null,
      patientLastnames: searchTerm.value || null,
      isPaid: isPaid.value,
      isInternado: isInternado.value,
    } as ConsultFilterDTO),
});

const buscarConsultas = () => {
  refetchConsults();
};

const recargarDatos = () => {
  refetchConsults();
};

const eliminarConsulta = (id: string) => {
  // Aquí puedes implementar la lógica de eliminación con confirmación y toast
  console.log("Eliminar consulta con ID:", id);
};
</script>

<style></style>
