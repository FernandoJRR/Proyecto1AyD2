<template>
  <div class="m-6">
    <DataTable :value="state.data as any[]" tableStyle="min-width: 50rem" stripedRows
      :loading="asyncStatus == 'loading'">
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Roles</span>
          <!--Bton de mas-->
          <router-link to="/admin/tipos-de-empleado/crear">
            <Button icon="pi pi-plus" rounded raised />
          </router-link>
        </div>
      </template>
      <Column header="Nombre">
        <template #body="slotProps">
          <template v-if="slotProps.data.name !== null">
            {{ `${slotProps.data.name}` }}
          </template>
          <template v-else>
            {{ `Admin` }}
          </template>
        </template>
      </Column>
      <!--Botones de acciones-->
      <Column header="Acciones">
        <template #body="slotProps">
          <RouterLink :to="`/admin/tipos-de-empleado/${slotProps.data.id}`">
            <Button label="Editar" severity="info" rounded text />
          </RouterLink>
          <Button v-if="slotProps.data.name !== null" label="Eliminar" severity="danger" rounded text
            @click="confirmDelete(slotProps.data.id, slotProps.data.name)" />
        </template>
      </Column>
      <template #footer> Hay en total {{ state.data ? (state.data as any[]).length : 0 }} roles. </template>
    </DataTable>
    <ConfirmDialog></ConfirmDialog>

  </div>
</template>
<!--Controlador-->
<script setup lang="ts">
import { useQuery } from '@pinia/colada';
import { RouterLink } from 'vue-router';
import { deleteEmployeeType, getAllEmployeeTypes, type EmployeeType } from '~/lib/api/admin/employee-type';
import ConfirmDialog from 'primevue/confirmdialog';
import { useConfirm } from "primevue/useconfirm";
import { toast } from 'vue-sonner';

//este es el controlador de caches de las queries de pinia colada
const queryCache = useQueryCache()
//esto es la constante que usa vue para manejar sus dialogs
const confirm = useConfirm();

const { state, asyncStatus } = useQuery({
  key: ['getAllEmployeeTypes'],
  query: () => getAllEmployeeTypes()
})



const confirmDelete = (id: string, name: string) => {
  confirm.require({
    message: `¿Estás seguro de que deseas eliminar el tipo de empleado "${name}"? Esta acción no se puede deshacer.`,
    header: 'Confirmation',
    icon: 'pi pi-exclamation-triangle',
    rejectProps: {
      label: 'Cancelar',
      severity: 'secondary',
      outlined: true,
    },
    acceptProps: {
      label: 'Eliminar',
      severity: 'danger',
      icon: 'pi pi-trash'
    },
    accept: () => {
      //si acepta entonces debemos mandar a llamar a mutable de eliminacion
      mutate(id);
    },
    reject: () => {
      toast.warning('Operación cancelada. No se eliminó el tipo de empleado.');
    }
  });
};


const { mutate } = useMutation({
  mutation: (id: string) => deleteEmployeeType(id),
  onError(error: any) {
    console.log(error)
    console.log(error.cause)
    toast.error('Ocurrió un error al eliminar el tipo de empleado', {
      description: `${(error.response._data)}`
    })
  },
  onSuccess() {
    toast.success('Tipo de empleado eliminado correctamente')
    queryCache.invalidateQueries({ key: ["getAllEmployeeTypes"] });
  }
})



</script>