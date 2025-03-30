<template>
  <div class="m-2">
    <div class="mb-2">
      <Button
        label="Volver"
        icon="pi pi-arrow-left"
        text
        @click="useRouter().back()"
      />
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
              @click="() => seleccionarEmpleado(slotProps.data, 'DOCTOR')"
            />
          </template>
        </Column>
        <template #footer>
          Hay en total {{ doctorsState.data?.length ?? 0 }} doctores registrados.
        </template>
      </DataTable>
    </div>

    <!-- Enfermeros -->
    <div>
      <h1 class="text-black mt-12 mb-2 text-2xl">Enfermeros</h1>
      <div class="flex justify-between items-center mb-4 gap-4">
        <div class="flex items-center gap-2">
          <InputText
            v-model="searchNurse"
            placeholder="Buscar enfermero por nombre..."
            class="p-inputtext-sm w-64"
            @keyup.enter="buscarEnfermeros"
          />
          <Button
            icon="pi pi-search"
            label="Buscar"
            @click="buscarEnfermeros"
            rounded
            outlined
            severity="info"
          />
        </div>
        <div class="flex items-center gap-2">
          <Button
            icon="pi pi-refresh"
            label="Recargar Tabla"
            @click="buscarEnfermeros"
            rounded
            outlined
            severity="help"
          />
        </div>
      </div>

      <DataTable
        :value="enfermerosState.data as any[]"
        tableStyle="min-width: 50rem"
        stripedRows
        :loading="asyncEnfermerosStatus === 'loading'"
      >
        <template #header>
          <div class="flex flex-wrap items-center justify-between gap-2">
            <span class="text-xl font-bold">Listado de Enfermeros</span>
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
              @click="() => seleccionarEmpleado(slotProps.data, 'NURSE')"
            />
          </template>
        </Column>
        <template #footer>
          Hay en total {{ enfermerosState.data?.length ?? 0 }} enfermeros registrados.
        </template>
      </DataTable>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { getDoctors, getEnfermeros } from "~/lib/api/admin/employee";

const searchDoctor = ref("");
const searchNurse = ref("");

const {
  state: doctorsState,
  asyncStatus: asyncDoctorsStatus,
  refetch: refetchDoctors,
} = useCustomQuery({
  key: ["doctors-add-consult"],
  query: () => getDoctors(searchDoctor.value === "" ? null : searchDoctor.value),
});

const {
  state: enfermerosState,
  asyncStatus: asyncEnfermerosStatus,
  refetch: refetchEnfermeros,
} = useCustomQuery({
  key: ["enfermeros-add-consult"],
  query: () => getEnfermeros(searchNurse.value === "" ? null : searchNurse.value),
});

const buscarDoctores = () => refetchDoctors();
const buscarEnfermeros = () => refetchEnfermeros();

const seleccionarEmpleado = (empleado: any, tipo: string) => {
  console.log("Empleado seleccionado:", empleado, "Tipo:", tipo);
};
</script>
