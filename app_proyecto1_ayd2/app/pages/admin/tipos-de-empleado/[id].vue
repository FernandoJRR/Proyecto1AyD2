<template>
  <div class="p-8 max-w-4xl mx-auto">
    <!-- encabezado y navegación -->
    <div class="mb-6">
      <router-link to="/admin/tipos-de-empleado">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>

    </div>
    <h1 class="text-4xl font-bold mb-6">Crear Tipo de Empleado</h1>
    <!-- formulario principal -->
    <Form v-if="isReady" v-slot="$form" :initialValues="formInitialValues" :resolver="resolver" @submit="onFormSubmit"
      :key="employeeTypeId" class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200">
      <div class="space-y-6">
        <h2 class="text-2xl font-semibold">Datos del Tipo de Empleado</h2>

        <div class="w-full">
          <FloatLabel>
            <label for="name">Nombre</label>
            <InputText id="name" name="name" type="text" class="w-full" />
          </FloatLabel>
          <Message v-if="$form.name?.invalid" severity="error" size="small" variant="simple">
            {{ $form.name.error?.message }}
          </Message>
        </div>
      </div>

      <!-- Permisos -->
      <div class="space-y-4">
        <h2 class="text-2xl font-semibold">Permisos Asociados</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2">
          <div v-for="permission in permissions.data" :key="permission.id" class="flex items-center">
            <!--Si nosotros establecemos el v-model con nuestro ref entonces view hace el insert y eliminacion en el array (usa el value)-->
            <Checkbox :value="permission.id" v-model="selectedPermissions" />
            <label :for="permission.id" class="ml-2">
              {{ permission.name }}
            </label>
          </div>
          <Message v-if="permissionsError" severity="error" size="small" variant="simple">
            {{ permissionsError }}
          </Message>
        </div>
      </div>

      <!-- Botón de envío -->
      <div class="pt-4">
        <Button type="submit" label="Editar" icon="pi pi-pencil" severity="secondary" />
      </div>
    </Form>
  </div>
</template>

<script setup lang="ts">
import { useQuery } from '@pinia/colada';
import { useRoute } from 'vue-router';
import { ref, computed } from 'vue';
import { editEmployeeType, getEmployeeTypeById } from '~/lib/api/admin/employee-type';
import { getAllPermissions } from '~/lib/api/admin/permission';
import type { EmployeeType, EmployeeTypePayLoad } from '~/lib/api/admin/employee-type';
import type { Permission } from '~/lib/api/admin/permission';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { z } from 'zod';
import { toast } from 'vue-sonner';
import { useQueryCache } from '@pinia/colada'

//este es el controlador de caches de las queries de pinia colada
const queryCache = useQueryCache()

//traemos el id de la ruta
const route = useRoute();//con esto traemos la ruta
const employeeTypeId = route.params.id as string;//de la ruta extraemos el id

//estos son los valores inciales del form, marcados como ref para que vue implemente reactividad
const formInitialValues = ref<{ name: string }>({ name: '' });
const isReady = ref(false);

const selectedPermissions = ref<string[]>([]);
const permissionsError = ref<string | null>(null);

//mandamos a traer el empleado una vez tengamos el id
const { data: employeeType } = useQuery<EmployeeType>({
  key: ['getEmployeeTypeById', employeeTypeId /**employeeTypeId identifica la consulta y su guardado en cache, si este cambia entonces se vuelve ha hacer la consulta */],
  query: () => getEmployeeTypeById(employeeTypeId)
});

const { state: permissions, refetch: asd } = useQuery<Permission[]>({
  key: ['allPermissions'],
  query: () => getAllPermissions()
});


// observa cambios en employeeType cuando la data llegue del backend
watch(
  () => employeeType.value,
  (data) => {
    if (data) {
      // establece los valores iniciales del formulario
      formInitialValues.value = {
        name: data.name,
      };

      // carga los permisos seleccionados usando solo los ids
      selectedPermissions.value = data.permissions.map(p => p.id);

      // habilita el renderizado del formulario
      isReady.value = true;
    }
  },
  { immediate: true } // ejecuta la lógica inmediatamente si employeeType ya tiene valor
);



const resolver = ref(zodResolver(
  z.object({
    name: z.string().min(1, 'El nombre del tipo de empleado no puede estar vacío y es obligatorio.').
      max(100, "El nombre del tipo de empleado no puede tener más de 100 caracteres."),

  }).superRefine((data, ctx) => {

    if (selectedPermissions.value.length === 0) {
      permissionsError.value = 'Debes seleccionar al menos un permiso.';
      //se agrega el error a sistema de validaciones para que el boton este bloqueado
      ctx.addIssue({
        path: ['permissions'],
        code: z.ZodIssueCode.custom
      });
    } else {
      permissionsError.value = '';
    }

  })
))

function onFormSubmit(e: any) {
  if (e.valid) {
    console.log(e.values)

    let payload: EmployeeTypePayLoad = {
      name: e.values.name,
      permissions: selectedPermissions.value.map(id => ({ id }))
    };

    mutate(payload)
  }
}



const { mutate } = useMutation({
  mutation: (employeeData: EmployeeTypePayLoad) => editEmployeeType(employeeTypeId, employeeData),
  onError(error) {
    toast.error('Ocurrió un error al editar el tipo de empleado', {
      description: `${(error.message)}`
    })
  },
  onSuccess() {
    toast.success('Tipo de empleado editado correctamente')
    //debemos marcar como obsoleta el cache de la query getEmployeeTypeById para que al volver a usar la page esta consulta
    //se vuelva ha ejecutar
    queryCache.invalidateQueries({ key: ["getEmployeeTypeById"] });
    navigateTo('/admin/tipos-de-empleado')
  }
})


</script>
